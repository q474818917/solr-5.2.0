package org.apache.lucene.pinyin.dwarf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.hp.hpl.sparta.xpath.ThisNodeTest;
import com.luhuiguo.chinese.ChineseUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class PinyinTokenizer extends Tokenizer {
  
  private static final int DEFAULT_BUFFER_SIZE = 512;
  
  private boolean main_done = false;
  private int finalOffset;
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
  private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
  private HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
  // 链接字符串.
  private String padding_char = " ";
  
  public boolean isOpenIK() {
    return openIK;
  }

  public void setOpenIK(boolean openIK) {
    this.openIK = openIK;
  }

  public int getPinyinnum() {
    return pinyinnum;
  }

  public void setPinyinnum(int pinyinnum) {
    this.pinyinnum = pinyinnum;
  }

  private Reader pinyinReader = null;
  
  private Version luceneMatchVersion;
  
  private IKSegmenter ikSegmenter;
  
  private boolean usedSmart = false;
  
  private boolean openIK = true;
  
  private int pinyinnum = 4;
  
  private boolean strictMode = false;
  
  private String cnCharLetters_String = "";
  
  private boolean done = false;
  
  private String[] cnarray;
  
  private int cn_arr_index = 0;
  
  // 停用词
  private CharArraySet stopwords;
  
  private int result_index = 0;
  
  private String split_delimiter;
  
  private String result = "";
  
  public static List<String> keywords = new ArrayList<String>() {
    {
      add("有限公司");
      add("公司");
    }
  };
  
  public static String filterkeyWords(String s) {
    for (String it : keywords) {
      if (s.contains(it)) {
        return s.replaceAll(it, "");
      } 
    }
    return s;
  }
  
  // 构造函数.
  public PinyinTokenizer(Version version, boolean usedSmart,
      boolean strictMode,  CharArraySet stopwords,
      String split_delimiter) {
    this(DEFAULT_BUFFER_SIZE, version);
    this.usedSmart = usedSmart;
    this.strictMode = strictMode;
    this.stopwords = stopwords;
    this.split_delimiter = split_delimiter;
  }
  
  public PinyinTokenizer(int bufferSize, Version version) {
    pinyinReader = input;
    luceneMatchVersion = version;
    termAtt.resizeBuffer(bufferSize);
    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    format.setVCharType(HanyuPinyinVCharType.WITH_V);
    
  }
  
  @Override
  public final boolean incrementToken() throws IOException {
    if (!main_done) {
      clearAttributes();
      main_done = true;
      int upto = 0;
      char[] buffer = termAtt.buffer();
      // System.out.println(String.valueOf(buffer));
      while (true) {
        final int length = input.read(buffer, upto, buffer.length - upto);
        if (length == -1) break;
        upto += length;
        if (upto == buffer.length) buffer = termAtt
            .resizeBuffer(1 + buffer.length);
      }
      termAtt.setLength(upto);
      //此处进行繁体转简历
      String str = ChineseUtils.toSimplified(termAtt.toString());
      
      termAtt.setEmpty();
      // 停用词过滤
      /*Iterator<Object> stopwordItem = stopwords.iterator();
      while (stopwordItem.hasNext()) {
        String stopword = String.valueOf((char[]) stopwordItem.next());
        boolean isstop = str.contains(stopword);
        if (isstop) {
          termAtt.setEmpty();
          return false;
        }
      }*/
      
      StringBuilder cnIKLetters = new StringBuilder(); // 分词器结果
      StringBuilder cnCharLetters = new StringBuilder(); // 带空格的汉子
      StringBuilder allPinYinLetters = new StringBuilder(); // 不带空格的拼音
      
      Pattern pattern = Pattern.compile("[\\pP\\s]");
      String[] arrStrings = pattern.split(str);
      for (int k = 0; k < arrStrings.length; k++) {
        String words = arrStrings[k];
        String filterword = filterkeyWords(words);
        if (filterword.length() < pinyinnum+1) {
          allPinYinLetters.append(getpinyin(filterword)).append(
              this.padding_char);
        }
        if (openIK) {
          StringReader wordReader = new StringReader(words);
          ikSegmenter = new IKSegmenter(wordReader, this.usedSmart);
          Lexeme nextLexeme = null;
          String nextLexemeStr = "";
          while ((nextLexeme = ikSegmenter.next()) != null) {
            nextLexemeStr = nextLexeme.getLexemeText();
            cnIKLetters.append(nextLexemeStr).append(this.padding_char);
            
            for (int i = 0; i < nextLexemeStr.length(); i++) {
              char c = nextLexemeStr.charAt(i);
              try {
                String[] strs = PinyinHelper
                    .toHanyuPinyinStringArray(c, format);
                if (strs.length == 0) {
                  continue;
                }
                String first_pinyin = strs[0];
                
                // 分词后的词，长度至少为2的才开启拼音功能
                if (nextLexemeStr.length() > 1) {
                  allPinYinLetters.append(first_pinyin);
                }
              } catch (BadHanyuPinyinOutputFormatCombination e) {
                // throw new RuntimeException();
              } catch (NullPointerException e) {
                break;
              }
              
            }
            allPinYinLetters.append(this.padding_char);
          }
        }
        for (int i = 0; i < words.length(); i++) {
          char c = words.charAt(i);
          // if (c < 128) {
          // stringBuilder.append(c);
          // } else {
          
          // cnLetters.append(c);
          cnCharLetters.append(c).append(this.padding_char);
          // if (strs != null) {
          // get first result by default
          // String first_value = strs[0];
          
          // TODO more than one pinyin
          // 拼接中文字符.
          
          // 全部拼音字符.
          // 分词后的词，长度至少为2的才开启拼音功能
          // if (nextLexemeStr.length() > 1) {
          // allPinYinLetters.append(first_value);
          // }
          // 第一个词分词分成单子则执行
          // if (k == 0 && nextLexemeStr.length() == 1) {
          // allPinYinLetters_without_s.append(first_value);
          // firstLetters_name.append(first_value.charAt(0));
          // }
          // }
          
        }
        cnCharLetters.append("|||").append(this.padding_char);
      }
      // let's join them
      
      termAtt.append(allPinYinLetters);
      termAtt.append(this.padding_char);
      // // termAtt.append(cnLetters.toString());
      // termAtt.append(this.padding_char);
      
      termAtt.append(cnIKLetters);
      // termAtt.append(firstLetters.toString());
      termAtt.append(this.padding_char);
      // termAtt.append(stringBuilder.toString());
      if (!this.strictMode) {
        // termAtt.append(cnCharLetters).append(this.padding_char);
        cnCharLetters_String = cnCharLetters.toString().trim();
        cnarray = cnCharLetters_String.split("\\s+");
      } else {
        this.done = true;
      }
      // termAtt.append(this.padding_char);
      // 将全部拼音分词成一个一个输入数据索引。
      // termAtt.append(mergeNGramPinYin(allPinYinLetters.toString()));
      
      // StringUtils.join(arrStrings, this.padding_char);
      posIncrAtt.setPositionIncrement(1);
      finalOffset = correctOffset(termAtt.toString().split("\\s+").length);
      offsetAtt.setOffset(correctOffset(0), finalOffset);
      typeAtt.setType("<IDEOGRAPHIC>");
      return true;
    } else {
      if (done) {
        return false;
      }
      
      termAtt.setEmpty();
      String term = cnarray[cn_arr_index];
      termAtt.append(term);
      posIncrAtt.setPositionIncrement(1);
      cn_arr_index++;
      typeAtt.setType("<IDEOGRAPHIC>");
      finalOffset = offsetAtt.endOffset() + term.length();
      offsetAtt.setOffset(correctOffset(offsetAtt.endOffset()), finalOffset);
      if (cn_arr_index == cnarray.length) {
        this.done = true;
      }
    }
    return true;
  }
  
  @Override
  public final void end() {
    // set final offset
    offsetAtt.setOffset(finalOffset, finalOffset);
  }
  
  @Override
  public void reset() throws IOException {
    super.reset();
    this.main_done = false;
    this.done = false;
    cn_arr_index = 0;
  }
  
  
  public String getpinyin(String s) {
    // 分词后的词，长度至少为2的才开启拼音功能
    if (s.length() < 2) {
      return "";
    }
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      String[] strs = null;
      try {
        strs = PinyinHelper.toHanyuPinyinStringArray(c, format);
      } catch (BadHanyuPinyinOutputFormatCombination e) {} catch (NullPointerException e) {
        break;
      }
      if (strs == null) {
        return "";
      }
      if (strs.length == 0) {
        return "";
      }
      result.append(strs[0]);
      
    }
    return result.toString();
  }
  
  public static void main(String[] args) {
    String string = "啊敢死队天天太美公司";
    // System.out.println(getpinyin(string));
    Pattern pattern = Pattern.compile("[\\pP\\s]");
    String[] arrStrings = pattern.split(string);
    for (int i = 0; i < arrStrings.length; i++) {
      String string2 = arrStrings[i];
      System.out.println(filterkeyWords(string2));
      
    }
  }
  
}

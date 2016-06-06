package org.apache.lucene.analysis.dwarf;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

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

public class PinyinTokenizer extends Tokenizer {
  //IK分词器实现
 private IKSegmenter _IKImplement;

 // 词元文本属性
 private final CharTermAttribute termAtt;
 // 词元位移属性
 private final OffsetAttribute offsetAtt;
 // 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
 private final TypeAttribute typeAtt;
 // 记录最后一个词元的结束位置
 private int endPosition; 
  // 链接字符串.  
  private String padding_char = " ";  
  
  public PinyinTokenizer() { //Lucene 5.x
    // super(in);
    offsetAtt = addAttribute(OffsetAttribute.class);
    termAtt = addAttribute(CharTermAttribute.class);
    typeAtt = addAttribute(TypeAttribute.class);
    _IKImplement = new IKSegmenter(input, true);
  }
  
  @Override
  public boolean incrementToken() throws IOException {
    
    clearAttributes();
    Lexeme nextLexeme = _IKImplement.next();
    if (nextLexeme != null) {
      // 将Lexeme转成Attributes
      // 设置词元文本
      String pinyin = Pinyin4jUtils.makeStringByStringSet(Pinyin4jUtils.str2Pinyin(nextLexeme.getLexemeText()));
      termAtt.append(nextLexeme.getLexemeText()).append(" ").append(pinyin);
      // 设置词元长度
      termAtt.setLength(nextLexeme.getLength() + pinyin.length() + 1);
      // 设置词元位移
      offsetAtt.setOffset(nextLexeme.getBeginPosition(),
          nextLexeme.getEndPosition());
      // 记录分词的最后位置
      endPosition = nextLexeme.getEndPosition();
      // 记录词元分类
      typeAtt.setType(nextLexeme.getLexemeTypeString());
      // 返会true告知还有下个词元
      return true;
    }
    // 返会false告知词元输出完毕
    return false;
  }

  /*@Override
  public final boolean incrementToken() throws IOException {
    if (!done) {  
      clearAttributes(); 
      done = true;  
      int upto = 0;  
      char[] buffer = termAtt.buffer();  
      System.out.println(String.valueOf(buffer));  
      while (true) {  
          final int length = input.read(buffer, upto, buffer.length  
                  - upto);  
          if (length == -1)  
              break;  
          upto += length;  
          if (upto == buffer.length)  
              buffer = termAtt.resizeBuffer(1 + buffer.length);  
      }  
      termAtt.setLength(upto);  
      String str = termAtt.toString();  
      termAtt.setEmpty();  
      
      //IK 
      StringReader wordReader = new StringReader(str);
      IKSegmenter ikSegmenter = new IKSegmenter(wordReader, true);
      Lexeme nextLexeme = null;
      String nextLexemeStr = "";
      
      StringBuilder stringBuilder = new StringBuilder();  
      StringBuilder firstLetters = new StringBuilder();  
      StringBuilder cnLetters = new StringBuilder();  
      StringBuilder allPinYinLetters = new StringBuilder();
      StringBuilder splitBuilder = new StringBuilder();
      
      while ((nextLexeme = ikSegmenter.next()) != null) {
        nextLexemeStr = nextLexeme.getLexemeText();
        for (int i = 0; i < nextLexemeStr.length(); i++) {  
          char c = nextLexemeStr.charAt(i);  
          if (c < 128) {  
              stringBuilder.append(c);  
          } else {  
               
                String first_value = Pinyin4jUtils.makeStringByStringSet(Pinyin4jUtils.str2Pinyin(String.valueOf(c)));
                String first_v = Pinyin4jUtils.makeStringByStringSet(Pinyin4jUtils.str2Pinyin(String.valueOf(c), Pinyin4jUtils.RET_PINYIN_TYPE_HEADCHAR));
                // 全部拼音字符.  
                allPinYinLetters.append(first_value);  
                // 拼接拼音字符.  
                stringBuilder.append(first_value);  
                 
                // 拼接首字母字符.  
                firstLetters.append(first_v);  
                
                splitBuilder.append(c);
                splitBuilder.append(this.padding_char);
          }  
          
        } 
        stringBuilder.append(this.padding_char); 
        cnLetters.append(nextLexemeStr);  
        cnLetters.append(this.padding_char);
      }

      // let's join them  

      termAtt.append(allPinYinLetters.toString());  
      termAtt.append(this.padding_char); 
      termAtt.append(stringBuilder.toString());  
      termAtt.append(this.padding_char);
      termAtt.append(cnLetters.toString());  
      termAtt.append(this.padding_char);  
      termAtt.append(firstLetters.toString());  
      termAtt.append(this.padding_char); 
      termAtt.append(splitBuilder.toString());
      // 将全部拼音分词成一个一个输入数据索引。  
      termAtt.append(mergeNGramPinYin(allPinYinLetters.toString())); 
      
      finalOffset = correctOffset(upto);  
      offsetAtt.setOffset(correctOffset(0), finalOffset); 
      return true;
    }
     return false;
    
  }*/
  
  
  @Override
  public final void end() throws IOException {
    int finalOffset = correctOffset(this.endPosition);
    offsetAtt.setOffset(finalOffset, finalOffset);
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    _IKImplement.reset(input);
  }

}

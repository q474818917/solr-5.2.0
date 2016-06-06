package org.wltea.analyzer.pinyin;

import java.io.IOException;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

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

public class PinyinTokenFilter extends TokenFilter {
  
  private final CharTermAttribute termAtt;
  private HanyuPinyinOutputFormat outputFormat;
  private boolean firstChar;
  private int minTermLength;
  private char[] curTermBuffer;
  private int curTermLength;
  private boolean outChinese;

  public PinyinTokenFilter(TokenStream input)
  {
    this(input, false, 2);
  }

  public PinyinTokenFilter(TokenStream input, boolean firstChar) {
    this(input, firstChar, 2);
  }

  public PinyinTokenFilter(TokenStream input, boolean firstChar, int minTermLenght)
  {
    super(input);

    this.termAtt = ((CharTermAttribute)addAttribute(CharTermAttribute.class));
    this.outputFormat = new HanyuPinyinOutputFormat();
    this.firstChar = false;
    this.minTermLength = 2;

    this.outChinese = true;

    this.firstChar = firstChar;
    this.minTermLength = minTermLenght;
    if (this.minTermLength < 1) {
      this.minTermLength = 1;
    }
    this.outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    this.outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
  }

  public static boolean containsChinese(String s)
  {
    if ((s == null) || ("".equals(s.trim())))
      return false;
    for (int i = 0; i < s.length(); i++) {
      if (isChinese(s.charAt(i)))
        return true;
    }
    return false;
  }

  public static boolean isChinese(char a) {
    int v = a;
    return (v >= 19968) && (v <= 171941);
  }

  public final boolean incrementToken() throws IOException {
    while (true) {
      if (this.curTermBuffer == null) {
        if (!this.input.incrementToken()) {
          return false;
        }
        this.curTermBuffer = ((char[])this.termAtt.buffer().clone());
        this.curTermLength = this.termAtt.length();
      }

      if (this.outChinese) {
        this.outChinese = false;
        this.termAtt.copyBuffer(this.curTermBuffer, 0, 
          this.curTermLength);
        return true;
      }
      this.outChinese = true;
      String chinese = this.termAtt.toString();

      if (containsChinese(chinese)) {
        this.outChinese = true;
        if (chinese.length() >= this.minTermLength) {
          try {
            String chineseTerm = getPinyinString(chinese);
            this.termAtt.copyBuffer(chineseTerm.toCharArray(), 0, 
              chineseTerm.length());
          } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
          }
          this.curTermBuffer = null;
          return true;
        }

      }

      this.curTermBuffer = null;
    }
  }

  public void reset() throws IOException {
    super.reset();
  }

  private String getPinyinString(String chinese) throws BadHanyuPinyinOutputFormatCombination
  {
    String chineseTerm = null;
    if (this.firstChar) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < chinese.length(); i++) {
        String[] array = PinyinHelper.toHanyuPinyinStringArray(
          chinese.charAt(i), this.outputFormat);
        if ((array != null) && (array.length != 0)) {
          String s = array[0];
          char c = s.charAt(0);

          sb.append(c);
        }
      }
      chineseTerm = sb.toString();
    } else {
      chineseTerm = PinyinHelper.toHanyuPinyinString(chinese, 
        this.outputFormat, "");
    }
    return chineseTerm;
  }
  
}

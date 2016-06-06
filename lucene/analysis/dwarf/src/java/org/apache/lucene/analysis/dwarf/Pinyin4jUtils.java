package org.apache.lucene.analysis.dwarf;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

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

public class Pinyin4jUtils {
  /**
   * 返回拼音类型：全拼
   */
  public static final String RET_PINYIN_TYPE_FULL = "full";
   
  /**
   * 返回拼音类型：首字母
   */
  public static final String RET_PINYIN_TYPE_HEADCHAR = "headChar";
   
  /**
   * 字符串集合转换字符串(逗号分隔)
   */
  public static String makeStringByStringSet(Set<String> stringSet) {
      StringBuilder str = new StringBuilder();
      int i = 0;
      for (String s : stringSet) {
          if (i == stringSet.size() - 1) {
              str.append(s);
          } else {
              str.append(s + ",");
          }
          i++;
      }
      return str.toString().toLowerCase();
  }

  public static Set<String> str2Pinyin(String src) {
      return str2Pinyin(src, null);
  }
   
  /**
   * 字符串转换为拼音
   * @param src   需要转换的字符串
   * @param retType   返回拼音结果类型
   * @return  如果retType为RET_PINYIN_TYPE_FULL，则返回全拼；如果retType为RET_PINYIN_TYPE_HEADCHAR;如果传入其他值，返回全拼
   */
  public static Set<String> str2Pinyin(String src, String retType) {
      if (src != null && !src.trim().equalsIgnoreCase("")) {
          char[] srcChar;
          srcChar = src.toCharArray();
          // 汉语拼音格式输出类
          HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

          // 输出设置，大小写，音标方式等
          hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
          hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
          hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

          String[][] temp = new String[src.length()][];
          for (int i = 0; i < srcChar.length; i++) {
              char c = srcChar[i];
                   
              try {
                  temp[i] = PinyinHelper.toHanyuPinyinStringArray(
                          srcChar[i], hanYuPinOutputFormat);
                   
                  if(temp[i] == null){//如果str.charAt(i)非汉字，则保持原样
                      temp[i] = new String[] { String.valueOf(srcChar[i]) };
                  }else{
                      //如果retType是RET_PINYIN_TYPE_HEADCHAR，则只取转换后的首字母
                      if(RET_PINYIN_TYPE_HEADCHAR.equalsIgnoreCase(retType)){
                          String[] temptemp = new String[temp[i].length];
                          for(int j = 0 ; j < temp[i].length; j++){
                              temptemp[j] =String.valueOf(temp[i][j].charAt(0));
                          }
                          temp[i] = temptemp;
                      }
                  }
                   
              } catch (BadHanyuPinyinOutputFormatCombination e) {
                  e.printStackTrace();
              }
          }
          String[] pingyinArray = Exchange(temp);
          Set<String> pinyinSet = new HashSet<String>();
          for (int i = 0; i < pingyinArray.length; i++) {
              pinyinSet.add(pingyinArray[i]);
          }
          return pinyinSet;
      }
      return null;
  }
   
  /**
   * 递归
   * 
   */
  public static String[] Exchange(String[][] strJaggedArray) {
      String[][] temp = DoExchange(strJaggedArray);
      return temp[0];
  }

  /**
   * 递归
   * 
   */
  private static String[][] DoExchange(String[][] strJaggedArray) {
      int len = strJaggedArray.length;
      if (len >= 2) {
          int len1 = strJaggedArray[0].length;
          int len2 = strJaggedArray[1].length;
          int newlen = len1 * len2;
          String[] temp = new String[newlen];
          int Index = 0;
          for (int i = 0; i < len1; i++) {
              for (int j = 0; j < len2; j++) {
                  temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
                  Index++;
              }
          }
          String[][] newArray = new String[len - 1][];
          for (int i = 2; i < len; i++) {
              newArray[i - 1] = strJaggedArray[i];
          }
          newArray[0] = temp;
          return DoExchange(newArray);
      } else {
          return strJaggedArray;
      }
  }

  public static void main(String[] args) {
      String str = "s王振兴";
      System.out.println(makeStringByStringSet(str2Pinyin(str)));
      System.out.println(makeStringByStringSet(str2Pinyin(str, RET_PINYIN_TYPE_HEADCHAR)));
  }
}

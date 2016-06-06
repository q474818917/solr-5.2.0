package org.wltea.analyzer.pinyin;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

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

public class PinyinTokenFilterFactory extends TokenFilterFactory {
  private boolean firstChar;
  private int minTermLenght;

  public PinyinTokenFilterFactory(Map<String, String> args)
  {
    super(args);
    this.firstChar = getBoolean(args, "firstChar", false);
    this.minTermLenght = getInt(args, "minTermLength", 4);
  }

  public boolean isFirstChar() {
    return this.firstChar;
  }

  public void setFirstChar(boolean firstChar) {
    this.firstChar = firstChar;
  }

  public int getMinTermLenght() {
    return this.minTermLenght;
  }

  public void setMinTermLenght(int minTermLenght) {
    this.minTermLenght = minTermLenght;
  }
  @Override
  public TokenStream create(TokenStream input) {
    return new PinyinTokenFilter(input, this.firstChar, 
        this.minTermLenght);
  }
  
}

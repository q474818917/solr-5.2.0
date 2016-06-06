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

import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
  
/** 
 */  
public class PinyinTokenizerFactory extends TokenizerFactory {  
  
  private boolean usedSmart = false;
  
  private boolean strictMode = false;
  private boolean openIK = true;
  private int pinyinnum = 4;
  
  private CharArraySet stopWords;
  
  private final String stopWordFiles;
  private final String format;
  private final boolean ignoreCase;
  private final String split_delimiter;
  private final boolean enablePositionIncrements;

    public PinyinTokenizerFactory(Map<String,String> args) {
          super(args); 
          if(args.get("openIK")!=null){
            openIK =  Boolean.valueOf(args.get("openIK"));
          }
          if(args.get("pinyinnum")!=null){
            pinyinnum =  Integer.valueOf(args.get("pinyinnum"));
          }
          usedSmart =  Boolean.valueOf(args.get("useSmart"));
          strictMode =  Boolean.valueOf(args.get("strictMode"));
          
          stopWordFiles = get(args, "words","stopwords.txt");
          format = get(args, "format");
          ignoreCase = getBoolean(args, "ignoreCase", false);
          enablePositionIncrements = getBoolean(args, "enablePositionIncrements", true);
          split_delimiter= get(args, "split_delimiter"," ");
  }
    
    @Override
    public Tokenizer create(AttributeFactory factory) {
      PinyinTokenizer pinyinTokenizer = new PinyinTokenizer(luceneMatchVersion,usedSmart,strictMode,stopWords,split_delimiter);
      pinyinTokenizer.setOpenIK(openIK);
      pinyinTokenizer.setPinyinnum(pinyinnum);
      return pinyinTokenizer;
    }
}  

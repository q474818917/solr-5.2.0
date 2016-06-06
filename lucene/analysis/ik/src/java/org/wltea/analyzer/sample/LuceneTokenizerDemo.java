package org.wltea.analyzer.sample;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

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

public class LuceneTokenizerDemo {
  
  public static void main(String[] args) {
    new LuceneTokenizerDemo().testNT();
  }
  
  /**
   * 测试标准的分词器StandardTokenizer
   */
  public void testST(){
    Tokenizer tokenizer = new StandardTokenizer();
    try {
      tokenizer.setReader(new StringReader("这一个中文分词的例子，你可以直接运行它！IKAnalyer can analysis english text too"));
    } catch (IOException e) {
      throw new RuntimeException();
    }
    TokenStreamComponents tsc = new TokenStreamComponents(tokenizer);
    TokenStream ts = tsc.getTokenStream();
    OffsetAttribute  offset = ts.addAttribute(OffsetAttribute.class); 
    CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
    TypeAttribute type = ts.addAttribute(TypeAttribute.class);
    try {
      ts.reset();
      while(ts.incrementToken()){
        System.out.println(term.toString() + "->" + offset.startOffset() + "-" + offset.endOffset() + "->" + type.type());
      }
      ts.end();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
  
  /**
   * 测试标准的分词器ClassTokenizer
   */
  public void testCT(){
    Tokenizer tokenizer = new ClassicTokenizer();
    try {
      tokenizer.setReader(new StringReader("这一个中文分词的例子，你可以直接运行它！IKAnalyer can analysis english text too"));
    } catch (IOException e) {
      throw new RuntimeException();
    }
    TokenStreamComponents tsc = new TokenStreamComponents(tokenizer);
    TokenStream ts = tsc.getTokenStream();
    OffsetAttribute  offset = ts.addAttribute(OffsetAttribute.class); 
    CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
    TypeAttribute type = ts.addAttribute(TypeAttribute.class);
    try {
      ts.reset();
      while(ts.incrementToken()){
        System.out.println(term.toString() + "->" + offset.startOffset() + "-" + offset.endOffset() + "->" + type.type());
      }
      ts.end();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
  
  /**
   * 测试标准的分词器NGramTokenizer
   * min:1,max:2
   */
  public void testNT(){
    Tokenizer tokenizer = new NGramTokenizer();
    try {
      tokenizer.setReader(new StringReader("这一个中文分词的例子，你可以直接运行它！IKAnalyer can analysis english text too"));
    } catch (IOException e) {
      throw new RuntimeException();
    }
    TokenStreamComponents tsc = new TokenStreamComponents(tokenizer);
    TokenStream ts = tsc.getTokenStream();
    OffsetAttribute  offset = ts.addAttribute(OffsetAttribute.class); 
    CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
    TypeAttribute type = ts.addAttribute(TypeAttribute.class);
    try {
      ts.reset();
      while(ts.incrementToken()){
        System.out.println(term.toString() + "->" + offset.startOffset() + "-" + offset.endOffset() + "->" + type.type());
      }
      ts.end();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
  
}

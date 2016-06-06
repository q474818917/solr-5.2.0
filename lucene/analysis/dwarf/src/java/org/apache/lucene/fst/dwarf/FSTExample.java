package org.apache.lucene.fst.dwarf;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;

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

public class FSTExample {
  
  
  
  public static void main(String[] args) {
    //CharSequenceOutputs, ByteSequenceOutputs
    File fstFile = new File("C:\\Users\\jiyu\\Desktop\\1.fst");
    
    CharSequenceOutputs charSequenceOutputs = CharSequenceOutputs.getSingleton();
    Builder<CharsRef> builder = new Builder<CharsRef>(FST.INPUT_TYPE.BYTE1, charSequenceOutputs);
    IntsRefBuilder intsRefBuilder = new IntsRefBuilder();
    try {
      builder.add(Util.toIntsRef(new BytesRef("我们"), intsRefBuilder), new CharsRef("他们"));
      FST<CharsRef> fst = builder.finish();
      fst.save(fstFile.toPath());
    } catch (IOException e) {
      throw new RuntimeException();
    }
    
    /*try {
      FST<CharsRef> fst = FST.read(fstFile.toPath(), charSequenceOutputs);
      System.out.println(Util.get(fst, new BytesRef("我们")));
    } catch (IOException e) {
      throw new RuntimeException();
    }*/
    
  }
  
  int[] toCodePointArray(String str) { // Example 1-1
    int len = str.length(); // the length of str
    int[] acp = new int[len]; // an array of code points
    
    for (int i = 0, j = 0; i < len; i++) {
      acp[j++] = str.charAt(i);
    }
    return acp;
  }
  
  int[] toCodePointArray2(String str) { // Example 1-2
    int len = str.length(); // the length of str
    int[] acp; // an array of code points
    int surrogatePairCount = 0; // the count of surrogate pairs
    
    for (int i = 1; i < len; i++) {
      if (Character.isSurrogatePair(str.charAt(i - 1), str.charAt(i))) {
        surrogatePairCount++;
        i++;
      }
    }
    acp = new int[len - surrogatePairCount];
    for (int i = 0, j = 0; i < len; i++) {
      char ch0 = str.charAt(i); // the current char
      if (Character.isHighSurrogate(ch0) && i + 1 < len) {
        char ch1 = str.charAt(i + 1); // the next char
        if (Character.isLowSurrogate(ch1)) {
          acp[j++] = Character.toCodePoint(ch0, ch1);
          i++;
          continue;
        }
      }
      acp[j++] = ch0;
    }
    return acp;
  }
  
}

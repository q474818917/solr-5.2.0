package org.apache.lucene.fst.dwarf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
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

public class FSTTokenFilterTest {
  
  public static void main(String[] args) {
    try {
      new FSTTokenFilterTest().timedThroughput("C:\\Users\\jiyu\\Desktop\\wikipedia_sample.txt");
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
  
  
  public void timedThroughput(String documentFileName) throws IOException {
    String fstFileName = "C:\\Users\\jiyu\\Desktop\\slovaklemma_ascii.fst";

    File fstFile = new File(fstFileName);
    FST<CharsRef> fst = FST.read(fstFile.toPath(), CharSequenceOutputs.getSingleton());

    File documentFile = new File(documentFileName);

    BufferedReader br = new BufferedReader(new FileReader(documentFile));
    String line;
    int n=0,m=0;
    while ((line = br.readLine()) != null) {
        CharsRef output = Util.get(fst, new BytesRef(line));
        if (output == null) {
            n++;
        } else {
            m++;
            Arrays.asList(output.toString().split("\\|"));
        }
    }
    br.close();
    System.out.println(documentFileName);
    System.out.println("FST returned null: " + n);
    System.out.println("Found match: " + m);
  }

}

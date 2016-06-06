package org.apache.lucene.atlas.dwarf;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

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

/**
 * 1、计算tf-> queryNorm-> idf
 * 2、计算
 */
public class AltasSimilarity extends TFIDFSimilarity {
  
  @Override
  public float coord(int overlap, int maxOverlap) {
    return 0;
  }
  
  @Override
  public float queryNorm(float sumOfSquaredWeights) {
    return (float)(1.0 / Math.sqrt(sumOfSquaredWeights));
  }
  
  @Override
  public float tf(float freq) {
    return (float)Math.sqrt(freq);
  }
  
  @Override
  public float idf(long docFreq, long numDocs) {
    return (float)(Math.log(numDocs/(double)(docFreq+1)) + 1.0);
  }
  
  @Override
  public float lengthNorm(FieldInvertState state) {
    return 0;
  }
  
  @Override
  public float decodeNormValue(long norm) {
    return 0;
  }
  
  @Override
  public long encodeNormValue(float f) {
    return 0;
  }
  
  @Override
  public float sloppyFreq(int distance) {
    return 0;
  }
  
  @Override
  public float scorePayload(int doc, int start, int end, BytesRef payload) {
    return 0;
  }
  
}

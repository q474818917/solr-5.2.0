package org.apache.lucene.function.dwarf;

import java.util.Map;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.MultiFloatFunction;

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

public class ManualScore extends MultiFloatFunction{

  public ManualScore(ValueSource[] sources) {
    super(sources);
  }

  @Override
  protected String name() {
    return "ManualScore";
  }

  @Override
  protected float func(int doc, FunctionValues[] valsArr) {
    String input = valsArr[0].strVal(doc);
    String[] keywordArr =input.split("\\pP|\\pS|\\s");
    
    float factor=1;
    for(String keyword:keywordArr){
      String uid = valsArr[1].strVal(doc);
      Map<String,Float> umap = KeyWordsCache.configMap.get(keyword);
      
      if(umap!=null){
        if(umap.get(uid)!=null){
          factor=umap.get(uid)*100f;
        }
      }
    }
    
    return factor;
  }
  
}

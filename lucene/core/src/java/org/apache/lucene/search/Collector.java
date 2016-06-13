package org.apache.lucene.search;

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

import java.io.IOException;

import org.apache.lucene.index.LeafReaderContext;

/**
 * <p>Expert: Collectors are primarily meant to be used to
 * gather raw results from a search, and implement sorting
 * or custom result filtering, collation, etc. </p>
 * 搜集原始结果，排序和过滤
 * 
 * <p>Lucene's core collectors are derived from {@link Collector}
 * and {@link SimpleCollector}. Likely your application can
 * use one of these classes, or subclass {@link TopDocsCollector},
 * instead of implementing Collector directly:
 * 使用Collector和SimpleCollector或者子类TopDocsCollector
 * <ul>
 *   抽象基类，根据条件获取top N的docs
 *   <li>{@link TopDocsCollector} is an abstract base class
 *   that assumes you will retrieve the top N docs,
 *   according to some criteria, after collection is
 *   done.  </li>
 *   
 *   TopScoreDocCollector具体子类，根据score和docID，不能使用sort排序，
 *   使用最多的Collector
 *   <li>{@link TopScoreDocCollector} is a concrete subclass
 *   {@link TopDocsCollector} and sorts according to score +
 *   docID.  This is used internally by the {@link
 *   IndexSearcher} search methods that do not take an
 *   explicit {@link Sort}. It is likely the most frequently
 *   used collector.</li>
 *  
 *   根据指定的field的排序，搜索时显示的使用sort
 *   <li>{@link TopFieldCollector} subclasses {@link
 *   TopDocsCollector} and sorts according to a specified
 *   {@link Sort} object (sort by field).  This is used
 *   internally by the {@link IndexSearcher} search methods
 *   that take an explicit {@link Sort}.
 *
 *   <li>{@link TimeLimitingCollector}, which wraps any other
 *   Collector and aborts the search if it's taken too much
 *   time.</li>
 *
 *   <li>{@link PositiveScoresOnlyCollector} wraps any other
 *   Collector and prevents collection of hits whose score
 *   is &lt;= 0.0</li>
 *
 * </ul>
 * 
 * 5.0之前Collector和LeafCollector接口是合并的，现在是拆分出来了
 * @lucene.experimental
 */
public interface Collector {

  /**
   * 收集给定的context, LeafReaderContext对应着索引中的segment
   * Create a new {@link LeafCollector collector} to collect the given context.
   *
   * @param context
   *          next atomic reader context
   */
  LeafCollector getLeafCollector(LeafReaderContext context) throws IOException;
  
  /**
   * Indicates if document scores are needed by this collector.
   * 
   * @return {@code true} if scores are needed.
   */
  boolean needsScores();
}

package org.wltea.analyzer.sample;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.atlas.dwarf.AltasSimilarity;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BitSet;

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

public class LuceneQueryDemo {
  
  public Directory directory;
  public IndexReader indexReader;
  public IndexSearcher indexSearcher;
  
  public LuceneQueryDemo(){
    try {
      directory = FSDirectory.open(Paths.get("C:\\Solr\\solrconfig\\solr-5.2.0\\solr\\collection1\\data\\index"));
      indexReader = DirectoryReader.open(directory);
    } catch (IOException e) {
      throw new RuntimeException();
    }
    indexSearcher = new IndexSearcher(indexReader);
    System.out.println("LuceneQueryDemo init constract");
  }
  
  public static void main(String[] args) {
    /*LuceneQueryDemo.TermQueryClass tqc = new LuceneQueryDemo.TermQueryClass();
    tqc.query();*/
    
    LuceneQueryDemo.PhraseQueryClass pqc = new LuceneQueryDemo.PhraseQueryClass();
    pqc.query();
    
    /*LuceneQueryDemo.QueryParserClass qpc = new LuceneQueryDemo.QueryParserClass();
    qpc.query();*/
    
  }
  
  static class QueryParserClass extends LuceneQueryDemo{
    public void query(){
      indexSearcher.setSimilarity(new AltasSimilarity());
      QueryParser qp =  new QueryParser("name",  new StandardAnalyzer());
      Query query;
      try {
        query = qp.parse("技术");
        TopDocs topDocs = indexSearcher.search(query, 5);
        for(ScoreDoc scoreDoc : topDocs.scoreDocs){
          System.out.println(scoreDoc.doc);
          //doc 0,freq 1
          //doc 9,freq 2
          //docfreq 2, numDocs 17
          System.out.println(indexSearcher.explain(query, scoreDoc.doc));
        }
      } catch (ParseException e) {
        throw new RuntimeException();
      } catch(IOException e){
        e.printStackTrace();
      }
      
    }
  }
  
  static class TermQueryClass extends LuceneQueryDemo{
    
    public void query(){
      TermQuery termQuery = new TermQuery(new Term("name", "介绍"));
      BooleanQuery booleanQuery = new BooleanQuery();
      booleanQuery.add(termQuery, Occur.SHOULD);
      try {
        System.out.println(indexSearcher.search(termQuery, 1));
      } catch (IOException e) {
        throw new RuntimeException();
      }
    }
  }
  
  final static class PhraseQueryClass extends LuceneQueryDemo{
    
    public void query(){
      /*
       * eg:介绍一下，技术不是很强
       * */
      PhraseQuery query = new PhraseQuery();
      //query.add(new Term("post", "心理咨询"));
      query.add(new Term("post", "歌手"));
      //query.add(new Term("post", "询"));
      query.setSlop(3);
      /*try {
        System.out.println(indexSearcher.search(query, 10).totalHits);
      } catch (IOException e) {
        throw new RuntimeException();
      }*/
      try {
        indexSearcher.search(query, new Collector() {

          public LeafCollector getLeafCollector(LeafReaderContext context)
              throws IOException {
            final int docBase = context.docBase;
            return new LeafCollector() {

              // ignore scorer
              public void setScorer(Scorer scorer) throws IOException {
              }

              public void collect(int doc) throws IOException {
                System.out.println(docBase + "->" + doc);
              }

            };
          }

          @Override
          public boolean needsScores() {
            return false;
          }

        });
      } catch (IOException e) {
        throw new RuntimeException();
      }

    }
  }
  
}

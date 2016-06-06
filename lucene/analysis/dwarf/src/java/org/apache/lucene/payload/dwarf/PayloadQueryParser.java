package org.apache.lucene.payload.dwarf;

import org.apache.lucene.index.Term;
import org.apache.solr.parser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

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

public class PayloadQueryParser extends QueryParser {
  PayloadQueryParser(QParser parser, String defaultField) {
    super(parser.getReq().getCore().getSolrConfig().luceneMatchVersion, defaultField, parser);
  }

  @Override
  protected Query getFieldQuery(String field, String queryText, boolean quoted) throws SyntaxError {
    SchemaField sf = this.schema.getFieldOrNull(field);
    // Note that this will work for any field defined with the
    // <fieldType> of "payloads", not just the field "payloads".
    // One could easily parameterize this in the config files to
    // avoid hard-coding the values.
    if (sf != null && sf.getType().getTypeName().equalsIgnoreCase("payloads")) {
      return new PayloadTermQuery(new Term(field, queryText), new AveragePayloadFunction(), true);
    }
    return super.getFieldQuery(field, queryText, quoted);
  }
}

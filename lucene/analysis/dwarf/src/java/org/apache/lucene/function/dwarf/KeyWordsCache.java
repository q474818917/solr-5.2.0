package org.apache.lucene.function.dwarf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import sun.misc.ClassLoaderUtil;





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

public class KeyWordsCache {
  public static Map<String,Map<String,Float>> configMap = null;
  static {
    System.out.println("i am start KeyWordsCache");
    configMap = new HashMap<String,Map<String,Float>>();
    ReloadCache();
  }
  
  public static void ReloadCache(){
    configMap.clear();
    
    Connection mysqlconn = getMysqlConnection();
    String sql = "select * from user.solr_manualscore";
    try {
      Statement st = (Statement) mysqlconn.createStatement();
      ResultSet rs = st.executeQuery(sql);  //执行sql查询语句，返回查询数据的结果集
      while (rs.next()) { // 判断是否还有下一个数据
        
        // 根据字段名获取相应的值
        String keyword = rs.getString("keyword");
        float factor = rs.getFloat("factor");
        String uid = rs.getString("uid");
        if(configMap.containsKey(keyword)){
          configMap.get(keyword).put(uid, factor);
        }else {
          Map<String,Float> u = new HashMap<String,Float>();
          u.put(uid, factor);
          configMap.put(keyword,u );
        }
        
        
      }
       //关闭数据库连接
    } catch (SQLException e) {
      throw new RuntimeException();
    } finally{
      try {
        mysqlconn.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }
  
  /* 获取数据库连接的函数*/
  public static Connection getMysqlConnection() {
    
    Properties dbprop = new Properties();
    
//      String currpath = KeyWordsCache.class.getClassLoader().get.getPath();
//      System.out.println(currpath);
      InputStream inputStream = KeyWordsCache.class.getResourceAsStream("mysql.properties");
      try {
        dbprop.load(inputStream);
      } catch (IOException e1) {
        throw new RuntimeException();
      }
    
    
    Connection mysqlconn = null;  //创建用于连接数据库的Connection对象
    try {
      Class.forName(dbprop.getProperty("driver"));// 加载Mysql数据驱动
      
      mysqlconn = DriverManager.getConnection(
          dbprop.getProperty("url"), dbprop.getProperty("user"), dbprop.getProperty("password"));// 创建数据连接
      
    } catch (Exception e) {
      System.out.println("数据库连接失败" + e.getMessage());
    }
    return mysqlconn; //返回所建立的数据库连接
  }
  
  
//  public static void main(String[] args) {
//    System.out.println(KeyWordsCache.configMap.keySet().size());
//    System.out.println(KeyWordsCache.configMap.keySet().size());
//    KeyWordsCache.configMap.get("da").get("a");
//  }
  
}

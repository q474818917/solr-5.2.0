Solr-5.2.0
============

####源码编译：（git上传入的是eclipse下工程配置好的，有.setting和.classpath）
+ 源码包解压后：ant eclipse （前提得装有ant和ivy的环境）
+ 导入eclipse中，工程Properties=>convert to faceted form=>Dynamic Web Module
+ 修改setting下的org.eclipse.wst.common.component文件的source-path="/solr/webapp/web"
+ 涉及到lucene的spi，将lucene的jar包放入到WEB-INF/lib下，该目录还用来存放第三方的jar包

####启动：
+ 指定solr home和collections，这些在/solr/example/example-DIH/solr/目录
+ 运行环境tomcat，当然也可以是jetty，jvm参数：
```-Dbootstrap_confdir=C:\Solr\solrconfig\solr-5.2.0\solr\collection1\conf
-Dcollection.configName=collection1 -Dsolr.solr.home=C:\Solr\solrconfig\solr-5.2.0\solr
```
+ 针对于solrcloud集群：可以使用嵌入式的zk，配置jvm参数：-DzkRun -DnumShards=1 

####特性：
+ 1、增加拼音检索(一个字的不做拼音处理，>=2个字的词，做拼音处理)
+ 2、增加NLP检索
+ 3、增加繁体处理(索引阶段，在IK分词的粒度上，将繁体转换为简体，需要jar包<chinese-utils>)
+ 4、增加PayloadSimilarity，针对分词后的term赋予权重
+ 5、增加类似百度竞价排名检索方式，针对检索的keyword，动态的指定doc的排序
  


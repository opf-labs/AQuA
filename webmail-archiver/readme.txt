WebMailArchiver prototype
==========================

This project aims to archive web accounts in arc file format.


It's a web project.

The dependencies are :
- javamail : mail.jar, mailapi.jar, pop3.jar
- logging : log4j
- heritrix : heritrix-1.14.4.jar 
- and its dependencies : 
	commons-cli-1.0.jar
	commons-codec-1.3.jar
	commons-collections-3.1.jar
	commons-httpclient-3.1.jar
	commons-io-1.3.1.jar
	commons-lang-2.3.jar
	commons-logging-1.0.4.jar
	commons-net-2.0.jar
	commons-pool-1.3.jar
	fastutil-5.0.3-heritrix-subset-1.0.jar
	
Since heritrix-1.14.4.jar is not in a maven repository, it needs to be added to your local repo by a :
mvn install:install-file -DgroupId=org.archive.heritrix -DartifactId=archive-commons -Dversion=1.14.4 -Dpackaging=jar -Dfile=/path/to/heritrix-1.14.4.jar

The generation of the war to be deployed in a web server is made by :
  mvn clean
  mvn package

The generation of the appropriate Eclipse files :
mvn eclipse:eclipse -Dwtpversion=2.0

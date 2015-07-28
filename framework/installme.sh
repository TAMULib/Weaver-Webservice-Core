mvn clean install
mvn install:install-file -Dfile=core/target/webservice-framework-core-1.0.0$1.jar -DgroupId=edu.tamu.framework -DartifactId=webservice-framework-core -Dversion=1.0.0$1 -Dpackaging=jar

#quick script to install the framework to your local .m2

if [[ $# -ne 1 ]]; then
    echo 'usage: install.me [release]'
    exit -1
fi

mvn clean install
mvn install:install-file -Dfile=core/target/webservice-framework-core-$1.jar -DgroupId=edu.tamu.framework -DartifactId=webservice-framework-core -Dversion=$1 -Dpackaging=jar

#quick script to install the framework to your local .m2

if [[ $# -ne 1 ]]; then
    echo 'usage: install.me [release]'
    exit -1
fi

mvn clean install
mvn install:install-file -Dfile=core/target/weaver-webservice-core-$1.jar -DgroupId=edu.tamu.weaver -DartifactId=weaver-webservice-core -Dversion=$1 -Dpackaging=jar

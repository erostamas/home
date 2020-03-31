#/bin/bash

cd $(dirname $0)/..

ROOT_DIR=$(git rev-parse --show-toplevel)

cd $ROOT_DIR
mkdir -p build
rm -rf build/*
#find ./src/ -name *.java | xargs javac -d build/ -classpath ".:/usr/lib/erostamas/jcommon.jar"
javac -verbose -d build/ -classpath "/usr/lib/erostamas/jcommon.jar" src/home/*

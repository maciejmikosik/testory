#!/bin/bash

BUILD_DIR=`dirname ${0}`
cd "${BUILD_DIR}/.."
MAIN=`pwd`

#cleanup
rm \
  --recursive \
  --force \
  ./sink/building

#compile
mkdir \
  --parents \
  ./sink/building
javac \
  -classpath "./jar/cglib-nodep-2.2.3.jar:./jar/objenesis-2.0.jar" \
  -sourcepath "./java" \
  -source 1.6 \
  -target 1.6 \
  -d "./sink/building" \
  ./java/org/testory/Testory.java

#copy sources
cp \
  --recursive \
  ./java/. \
  ./sink/building

#copy dependencies
unzip \
  -q \
  ./jar/cglib-nodep-2.2.3.jar \
  -d ./sink/building \
  net/*
unzip \
  -q \
  ./jar/objenesis-2.0.jar \
  -d ./sink/building \
  org/*

#copy license files
cp \
  --recursive \
  ./build/license/. \
  ./sink/building

#zip jar
cd ./sink/building
zip \
  --quiet \
  --recurse-paths \
  ./testory.jar \
  ./*
cd $MAIN

#refactor dependencies
java \
  -jar ./build/jarjar-1.4.jar \
  process ./build/jarjar-rules.txt \
  ./sink/building/testory.jar \
  ./sink/building/testory.jar

#copy testory.jar
cp \
  ./sink/building/testory.jar \
  ./sink

#cleanup
rm \
  --recursive \
  --force \
  ./sink/building

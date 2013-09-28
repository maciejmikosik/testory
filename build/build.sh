#!/bin/bash

#cleanup
rm \
  --recursive \
  --force \
  ./tmp
mkdir \
  --parents \
  ./tmp

#compile
mkdir \
  --parents \
  ./tmp/bin
javac \
  -classpath "./../dep/cglib-nodep-2.2.3.jar:./../dep/objenesis-2.0.jar" \
  -sourcepath "./../src" \
  -d "./tmp/bin" \
  ./../src/org/testory/Testory.java

#copy sources
cp \
  --recursive \
  ./../src/. \
  ./tmp/bin

#copy dependencies
unzip \
  ./../dep/cglib-nodep-2.2.3.jar \
  -d ./tmp/bin \
  net/*
unzip \
  ./../dep/objenesis-2.0.jar \
  -d ./tmp/bin \
  org/*

#copy license files
cp \
  --recursive \
  ./license/. \
  ./tmp/bin

#zip jar
cd ./tmp/bin
zip \
  --recurse-paths \
  ./../testory.jar \
  ./*
cd ./../..

#refactor dependencies
java \
  -jar jarjar-1.4.jar \
  process jarjar-rules.txt \
  ./tmp/testory.jar \
  ./tmp/testory.jar

#cleanup
rm \
  --recursive \
  --force \
  ./tmp/bin
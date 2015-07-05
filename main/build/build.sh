#!/bin/bash -e

HERE=`dirname ${0}`
cd "${HERE}/../.."
PROJECT=`pwd`
	MAIN="${PROJECT}/main"
		 RUN="${MAIN}/build"
		JAVA="${MAIN}/java"
		 JAR="${MAIN}/jar"
		SINK="${MAIN}/sink"
			DRAFT="${SINK}/draft"

#cleanup
rm \
  --recursive \
  --force \
  ${DRAFT}

#compile
mkdir \
  --parents \
  ${DRAFT}
javac \
  -classpath "${JAR}/cglib-nodep-2.2.3.jar:${JAR}/objenesis-2.0.jar" \
  -sourcepath "${JAVA}" \
  -source 1.7 \
  -target 1.7 \
  -d "${DRAFT}" \
  "${JAVA}/org/testory/Testory.java"

#copy sources
cp \
  --recursive \
  "${JAVA}/." \
  "${DRAFT}"

#copy dependencies
unzip \
  -q \
  "${JAR}/cglib-nodep-2.2.3.jar" \
  -d "${DRAFT}" \
  net/*
unzip \
  -q \
  "${JAR}/objenesis-2.0.jar" \
  -d "${DRAFT}" \
  org/*

#copy license files
cp \
  --recursive \
  "${RUN}/license/." \
  "${DRAFT}"

#zip jar
cd ${DRAFT}
zip \
  --quiet \
  --recurse-paths \
  ./testory.jar \
  ./*
cd "${PROJECT}"

#refactor dependencies
java \
  -jar "${RUN}/jarjar-1.4.jar" \
  process "${RUN}/jarjar-rules.txt" \
  "${DRAFT}/testory.jar" \
  "${DRAFT}/testory.jar"

#copy testory.jar
cp \
  "${DRAFT}/testory.jar" \
  "${SINK}"

echo ""
echo "BUILD SUCCESSFUL"
echo "created ${SINK}/testory.jar"

#cleanup
rm \
  --recursive \
  --force \
  "${DRAFT}"

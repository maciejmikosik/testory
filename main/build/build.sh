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

########## CLEANUP ##########
rm \
  --recursive \
  --force \
  ${DRAFT}

mkdir \
  --parents \
  ${DRAFT}

########## COMPILE SOURCES ##########
javac \
  -classpath "${JAR}/cglib-nodep-2.2.3.jar:${JAR}/objenesis-2.0.jar" \
  -sourcepath "${JAVA}" \
  -source 1.7 \
  -target 1.7 \
  -d "${DRAFT}" \
  "${JAVA}/org/testory/Testory.java"

########## COPY SOURCES ##########
cp \
  --recursive \
  "${JAVA}/." \
  "${DRAFT}"

########## COPY DEPENDENCIES ##########
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

########## COPY LICENSE FILES ##########
cp \
  --recursive \
  "${RUN}/license/." \
  "${DRAFT}"

########## ZIP JAR ##########
cd ${DRAFT}
zip \
  --quiet \
  --recurse-paths \
  ./testory.jar \
  ./*
cd "${PROJECT}"

########## INLINE DEPENDENCIES ##########
java \
  -jar "${RUN}/jarjar-1.4.jar" \
  process "${RUN}/jarjar-rules.txt" \
  "${DRAFT}/testory.jar" \
  "${DRAFT}/testory.jar"

########## COPY PRODUCED JAR ##########
cp \
  "${DRAFT}/testory.jar" \
  "${SINK}"

echo ""
echo "BUILD SUCCESSFUL"
echo "created ${SINK}/testory.jar"

########## CLEANUP ##########
rm \
  --recursive \
  --force \
  "${DRAFT}"

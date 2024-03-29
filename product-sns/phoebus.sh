#!/bin/sh
#
# Phoebus launcher for Linux or Mac OS X

# When deploying, change "TOP"
# to the absolute installation path
TOP="$( cd "$(dirname "$0")" ; pwd -P )"
echo $TOP

# Look for Java in a parallel 'jdk' folder.
# Alternatively, define JAVA_HOME as desired.
if [ -d "${TOP}/../jdk" ]
then
  export JAVA_HOME=`(cd "${TOP}/../jdk"; pwd)`
  export PATH="$JAVA_HOME/bin:$PATH"
  echo "Using JDK $JAVA_HOME"
fi

if [ -d "${TOP}/target" ]
then
  TOP="${TOP}/target"
fi

if [ -d "${TOP}/update" ]
then
  echo "Installing update..."
  cd ${TOP}
  
  if [ -d update/jdk ]
  then
      echo "Updating JDK"
      rm -rf ../jdk
      mv update/jdk ..
      chmod +x ../jdk/bin/*
  fi
  
  rm -rf doc lib product*.jar
  mv update/* .
  rmdir update
  echo "Updated."
fi

JAR="${TOP}/product-sns-*.jar"

# Reduce VIRT memory
export MALLOC_ARENA_MAX=4

# Memory
export JDK_JAVA_OPTIONS="-Xms500M -Xmx2G"

JDK_JAVA_OPTIONS+=" -Dfile.encoding=UTF-8 "

# When using Java 14+:
JDK_JAVA_OPTIONS+=" -XX:+ShowCodeDetailsInExceptionMessages "

# Don't start a CA Repeater
JDK_JAVA_OPTIONS+=" -DCA_DISABLE_REPEATER=true "

# Disable warnings
JDK_JAVA_OPTIONS+=" -Dnashorn.args=--no-deprecation-warning "

# Use GTK 2 (GTK 3 drag/drop doesn't always work)
JDK_JAVA_OPTIONS+=" -Djdk.gtk.verbose=false -Djdk.gtk.version=2"

# Drawing pipeline
JDK_JAVA_OPTIONS+=" -Dprism.verbose=false -Dprism.forceGPU=true"
# Disable acceleration
# JDK_JAVA_OPTIONS+=" -Dprism.order=sw"

OPT=""
# To get one instance, use server mode
# OPT+=" -server 4918"

if [ "x$1" == "x-main"  -o  "x$1" == "x-help" ]
then
  # Run MEDM converter etc. in foreground
  java -jar $JAR $OPT "$@"
else
  # Run UI as separate thread
  java -jar $JAR $OPT "$@" &
fi


#!/bin/sh
#
# Phoebus launcher for Linux or Mac OS X

# When deploying, change "TOP"
# to the absolute installation path
TOP="$( cd "$(dirname "$0")" ; pwd -P )"
echo $TOP

# Ideally, assert that Java is found
# export JAVA_HOME=/opt/jdk-9
# export PATH="$JAVA_HOME/bin:$PATH"

if [ -d "${TOP}/target" ]
then
  TOP="${TOP}/target"
fi

if [ -d "${TOP}/update" ]
then
  echo "Installing update..."
  cd ${TOP}
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


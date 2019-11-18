#!/bin/sh
#
# Phoebus launcher for Mac OS X

# Location of this script
# cd .. pwd determines the full path
# even if called from shell with relative path
CONTENTS="$( cd "$(dirname "$0")" ; pwd -P )"
BASE=${CONTENTS%/*/*}

# Phoebus TOP
TOP=`echo "$BASE/product-sns-*"`

if [ -d ${TOP}/update ]
then
  echo "Installing update..."
  cd ${TOP}
  rm -rf doc lib product-sns-*.jar
  mv update/* .
  rmdir update
  echo "Updated."
fi

export JAVA_HOME="$BASE/jdk/Contents/Home"

export PATH="$JAVA_HOME/bin:$PATH"

JAR=`echo "${TOP}/product-sns-*.jar"`

# To get one instance, use server mode
# OPT="-server 4918"

java -jar $JAR $OPT "$@" &


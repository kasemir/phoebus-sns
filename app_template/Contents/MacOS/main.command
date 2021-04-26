#!/bin/sh
#
# Phoebus launcher for Mac OS X

# Location of this script
# cd .. pwd determines the full path
# even if called from shell with relative path
CONTENTS="$( cd "$(dirname "$0")" ; pwd -P )"
BASE=${CONTENTS%/*/*}

# Phoebus TOP: phoebus-.. or product-..
TOP=`echo "$BASE/p*"`

if [ -d ${TOP}/update ]
then
  echo "Installing update..."
  cd ${TOP}
  
  if [ -d update/jdk ]
  then
      echo "Updating JDK"
      rm -rf ../jdk
      mv update/jdk ..
      chmod +x ../jdk/Contents/Home/bin/*
  fi
  
  rm -rf doc lib p*.jar
  mv update/* .
  rmdir update
  echo "Updated."
fi

export JAVA_HOME="$BASE/jdk/Contents/Home"

export PATH="$JAVA_HOME/bin:$PATH"

export HOSTNAME=`/bin/hostname`

JAR=`echo "${TOP}/p*.jar"`

# To get one instance, use server mode
# OPT="-server 4918"

java -Xdock:icon="$BASE/Contents/Resources/cmd.icns" -jar $JAR $OPT "$@" &


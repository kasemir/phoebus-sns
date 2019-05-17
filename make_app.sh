#!/bin/sh
#
# Assuming a product-sns-0.0.1-mac.zip of
# the same basic layout that's used on Linux,
# package as Mac OS X phoebus.app with JDK:
#
# phoebus.app/jdk
# phoebus.app/product-sns-0.0.1
# phoebus.app/Contents/MacOS/phoebus

V="0.0.1"
JDK="/opt/jdks/mac/jdk"


unzip product-sns-${V}-mac.zip
mkdir -p phoebus.app/Contents/MacOS
cd phoebus.app
cp -r ${JDK} .
mv ../product-sns-${V} .
cd Contents/MacOS
echo >phoebus '#!/bin/sh
#
# Phoebus launcher for Mac OS X

V="0.0.1"

# Location of this script
CONTENTS_MacOS="$( cd "$(dirname "$0")" ; pwd -P )"

# Phoebus TOP
TOP="$CONTENTS_MacOS/../../product-sns-0.0.1"

if [ -d "${TOP}/update" ]
then
  echo "Installing update..."
  cd ${TOP}
  rm -rf doc lib
  mv update/* .
  rmdir update
  echo "Updated."
fi

export JAVA_HOME="$TOP/../jdk/Contents/Home/"
export PATH="$JAVA_HOME/bin:$PATH"

# Use ant or maven jar?
if [ -f ${TOP}/product-sns-${V}.jar ]
then
  JAR="${TOP}/product-sns-${V}.jar"
else
  JAR="${TOP}/product-sns-${V}-SNAPSHOT.jar"
fi

# To get one instance, use server mode
# OPT="-server 4918"

java -jar $JAR $OPT "$@" &
'

chmod +x phoebus

cd ../../..
rm product-sns-${V}-mac.zip
zip -r product-sns-${V}-mac.zip phoebus.app
rm -rf phoebus.app

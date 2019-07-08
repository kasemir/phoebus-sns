#!/bin/sh
#
# Assuming a product-sns-0.0.1-mac.zip of
# the same basic layout that's used on Linux,
# package as Mac OS X phoebus.app with JDK:
#
# phoebus.app/jdk
# phoebus.app/product-sns-0.0.1
# phoebus.app/Contents/MacOS/phoebus

V="4.6.0"
JDK="/opt/jdks/mac/jdk"

unzip product-sns-*-mac.zip
rm product-sns-*-mac.zip
mkdir -p phoebus.app/Contents/MacOS
cd phoebus.app
cp -r ${JDK} .
mv ../product-sns-${V} .
cd Contents/MacOS
echo >phoebus '#!/bin/sh
#
# Phoebus launcher for Mac OS X

# Location of this script
CONTENTS_MacOS="$( cd "$(dirname "$0")" ; pwd -P )"

# Phoebus TOP
TOP=`echo "$CONTENTS_MacOS/../../product-sns-*"`

if [ -d "${TOP}/update" ]
then
  echo "Installing update..."
  cd ${TOP}
  rm -rf doc lib product-sns-*.jar
  mv update/* .
  rmdir update
  echo "Updated."
fi

export JAVA_HOME="$( cd $TOP/../jdk/Contents/Home/ ; pwd -P )"
export PATH="$JAVA_HOME/bin:$PATH"

JAR=`echo "${TOP}/product-sns-*.jar"`

# To get one instance, use server mode
# OPT="-server 4918"

java -jar $JAR $OPT "$@" &
'

chmod +x phoebus

cd ../../..
zip -r product-sns-${V}-mac.zip phoebus.app
rm -rf phoebus.app

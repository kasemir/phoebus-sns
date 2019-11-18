#!/bin/sh
#
# Wrap product-snsproduct-sns/target/product-sns-4.6.0-mac.zip
# as Mac OS X CSS_Phoebus.app with JDK based on $JAVA_HOME
#
# phoebus.app/jdk
# phoebus.app/product-sns-4.6.0
# phoebus.app/Contents/...
#
# phoebus.app/Contents started out as mostly the Contents/MacOS/command,
# but newer versions of Mac OS require more and more content,
# otherwise *.app is considered "damaged or incomplete".
# Contents skeleton thanks to https://github.com/thedzy/Run-script-as-an-Applicaiton 
#
# Author: Kay Kasemir

APP=CSS_Phoebus.app
# *.app skeleton w/ launch script
rm -rf $APP
cp -r app_template $APP

# Add JDK
JDK="${JAVA_HOME%/*/*}"
if [ -d "$JDK/Contents/Home" ]
then
  echo "Adding $JDK"
  cp -r $JDK $APP/jdk
else
  echo "Missing $JAVA_HOME set to JDK/Contents/Home"
  exit 1
fi

# Add product
PROD=`echo product-sns/target/product-sns-*-mac.zip`
if [ -r "$PROD" ]
then
  echo "Adding $PROD"
  unzip -q $PROD -d $APP
else
  echo "Cannot locate product-sns-*-mac.zip"
  exit 2
fi

V=`echo $PROD | egrep -o '[0-9.]+' | head -n1`

echo "Packing product-sns-${V}-mac.zip"
rm -f product-sns-${V}-mac.zip
zip -qr product-sns-${V}-mac.zip $APP
# rm -rf $APP

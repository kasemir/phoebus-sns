#!/bin/sh
#
# Wrap product-sns/target/product-sns-4.6.0-mac.zip
# or ../phoebus/phoebus-product/target/phoebus-4.6.3-mac.zip
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

if [ $# -eq 1 ]
then
  PROD=$1
else
  PROD=`echo product-sns/target/product-sns-*-mac.zip`
fi

# Leaving original ZIP, creating new ZIP here
DEST=`basename $PROD`
echo "Turning ZIP-distro $PROD into Mac OS app $DEST"

if [ "$PROD" = "$DEST" ]
then
  echo "$PROD must be in different directory than $DEST"
  exit 1
fi

APP=CSS_Phoebus.app

# *.app skeleton w/ launch script
echo "Creating app skeleton $APP"
rm -rf $APP
cp -r app_template $APP

# Add JDK
JDK="${JAVA_HOME%/*/*}"
if [ -d "$JDK/Contents/Home" ]
then
  echo "Adding JRE $JDK"
  cp -r $JDK $APP/jdk
else
  echo "Missing $JAVA_HOME set to JDK/Contents/Home"
  exit 2
fi

# Add product
if [ -r "$PROD" ]
then
  echo "Adding contents of ZIP-distro $PROD"
  unzip -q $PROD -d $APP
else
  echo "Cannot locate product-sns-*-mac.zip"
  exit 3
fi

echo "Packing $APP as $DEST"
rm -f $DEST
zip -qr $DEST $APP
# rm -rf $APP

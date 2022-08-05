# Get sources
git clone https://github.com/ControlSystemStudio/phoebus.git
git clone https://github.com/kasemir/phoebus-sns.git

cd phoebus

if [ "x$WORKSPACE" = "x" ]
then
    echo "Plain Linux setup"
    B=`git branch | sed 's/[*] //'`
    M2_HOME=$HOME/Eclipse/apache-maven
    ANT_HOME=$HOME/Eclipse/apache-ant
    JAVA_HOME=$HOME/Eclipse/jdk
else
    echo "Jenkins"
    B=`echo $GIT_BRANCH | sed 's/.*\///'`
    M2_HOME=/opt/apache-maven
    ANT_HOME=/opt/apache-ant
    JAVA_HOME=/opt/jdk11
fi

export ORACLE_JDBC_JAR=/opt/Oracle/ojdbc8-12.2.0.1.jar
export PATH="$M2_HOME/bin:$ANT_HOME/bin:$JAVA_HOME/bin:$PATH"

D=`date +'%Y-%m-%d %H:%M'`
VERSION="$B $D"

echo "============================================="
echo "VERSION: $VERSION"
echo "============================================="

mkdir -p dependencies/install-jars/lib/ojdbc
cp $ORACLE_JDBC_JAR dependencies/install-jars/lib/ojdbc

java -version
mvn -version

rm -f *.zip

# Create Javadoc
( cd app/display/editor;  ant -f javadoc.xml clean all )

# Create documentation
( cd docs; make clean html )
# The following 'ant clean' steps will remove the javadoc,
# but we now have it copied into the documentation, so no problem.

# Create Update info
URL='https://controlssoftware.sns.ornl.gov/css_phoebus/nightly/phoebus-$(arch).zip'
sh app/update/mk_update_settings.sh $URL > phoebus-product/settings.ini


# Set 'version'
git checkout -- core/ui/src/main/resources/org/phoebus/ui/application/messages.properties
sed -i "s/\${version}/$VERSION/" core/ui/src/main/resources/org/phoebus/ui/application/messages.properties
git diff core/ui/src/main/resources/org/phoebus/ui/application/messages.properties


cd ../phoebus-sns

# Cleanup
rm -f *.zip

# Update
URL='https://controlssoftware.sns.ornl.gov/css_phoebus/nightly/product-sns-$(arch).zip'
git checkout -- product-sns/settings.ini
sh ../phoebus/app/update/mk_update_settings.sh $URL >> product-sns/settings.ini
git diff product-sns/settings.ini

# Windows ---------------------------------------

# Get deps for windows
( cd ../phoebus/dependencies; mvn -Djavafx.platform=win clean install )
rm -f ../phoebus/dependencies/phoebus-target/target/lib/*log4j*
# Zip phoebus-target
rm -f phoebus-target-win.zip
zip -qr phoebus-target-win.zip ../phoebus/dependencies/phoebus-target/target/lib/

# Build Windows products
ant clean dist
mv ../phoebus/phoebus-product/target/phoebus-*-win.zip .
mv product-sns/target/product-sns-*-win.zip .

# Remove the linux starter, since windows user tend to not see file extensions
# and phoebus.sh looks just like phoebus.bat
PROD=`echo product-sns-*-win.zip | sed 's/-win.zip//'`
zip -d $PROD-win.zip $PROD/phoebus.sh
# Bundle JRE?
if [ -d /opt/jdks/windows ]
then
    WS=`pwd`
    ( cd /opt/jdks/windows; zip -r $WS/product-sns-*-win.zip jdk )
fi


# Mac  ---------------------------------------
( cd ../phoebus/dependencies; mvn -Djavafx.platform=mac clean install )
rm -f ../phoebus/dependencies/phoebus-target/target/lib/*log4j*
# Zip phoebus-target
rm -f phoebus-target-mac.zip
zip -qr phoebus-target-mac.zip ../phoebus/dependencies/phoebus-target/target/lib/

# Build Mac products
ant clean dist

# Bundle as Mac app (plain as well as SNS product)
( export JAVA_HOME=/opt/jdks/mac/jdk/Contents/Home;
  sh make_app.sh ../phoebus/phoebus-product/target/phoebus-*-mac.zip )
( export JAVA_HOME=/opt/jdks/mac/jdk/Contents/Home;
  sh make_app.sh product-sns/target/product-sns-*-mac.zip )


# Linux  ---------------------------------------
# Build the SNS product with maven to test one complete maven build
mvn -DskipTests -Djavafx.platform=linux clean install
rm -f ../phoebus/dependencies/phoebus-target/target/lib/*log4j*

# Zip phoebus-target
rm -f phoebus-target-linux.zip
zip -qr phoebus-target-linux.zip ../phoebus/dependencies/phoebus-target/target/lib/


# Show command-line options, basic test that it 'runs'
java -jar product-sns/target/product-sns-*-SNAPSHOT.jar -help

# Build with ant (online help, dist)
ant clean dist
java -jar product-sns/target/product-sns-*.jar -help

mv ../phoebus/phoebus-product/target/phoebus-*-linux.zip .
mv product-sns/target/product-sns-*-linux.zip .
# Bundle JRE?
if [ -d /opt/jdks/linux ]
then
    WS=`pwd`
    ( cd /opt/jdks/linux; zip -r $WS/product-sns-*-linux.zip jdk )
fi

# Delete stuff we don't need
zip -d phoebus-[0-9].[0-9].[0-9]*-linux.zip '*app-trends-rich-adapters*'
zip -d phoebus-[0-9].[0-9].[0-9]*-win.zip '*app-trends-rich-adapters*'
zip -d phoebus-[0-9].[0-9].[0-9]*-mac.zip '*app-trends-rich-adapters*'
zip -d product-sns-[0-9].[0-9].[0-9]*-linux.zip '*app-trends-rich-adapters*'
zip -d product-sns-[0-9].[0-9].[0-9]*-win.zip '*app-trends-rich-adapters*'
zip -d product-sns-[0-9].[0-9].[0-9]*-mac.zip '*app-trends-rich-adapters*'

echo Show command line options
( cd ../phoebus/phoebus-product; ./phoebus.sh -help )
( cd ../phoebus/services/alarm-server; ./alarm-server.sh -help )
( cd ../phoebus/services/alarm-logger; ./alarm-logger.sh -help )
( cd ../phoebus/services/scan-server; ./scan-server.sh -help )
( cd ../phoebus/services/archive-engine; sh archive-engine.sh -help )


# Rename
mv phoebus-[0-9].[0-9].[0-9]*-linux.zip                           phoebus-linux.zip
mv phoebus-[0-9].[0-9].[0-9]*-win.zip                             phoebus-win.zip
mv phoebus-[0-9].[0-9].[0-9]*-mac.zip                             phoebus-mac.zip
mv product-sns-[0-9].[0-9].[0-9]*-linux.zip                       product-sns-linux.zip
mv product-sns-[0-9].[0-9].[0-9]*-win.zip                         product-sns-win.zip
mv product-sns-[0-9].[0-9].[0-9]*-mac.zip                         product-sns-mac.zip
mv ../phoebus/services/scan-server/target/scan-server-*.zip       scan-server.zip
mv ../phoebus/services/alarm-server/target/alarm-server-*.zip     alarm-server.zip
mv ../phoebus/services/alarm-logger/target/alarm-logger-*.zip     alarm-logger.zip
mv ../phoebus/services/archive-engine/target/archive-engine-*.zip archive-engine.zip



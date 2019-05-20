# phoebus-sns

Phoebus (https://github.com/shroffk/phoebus) product for SNS

 * Site-specific PVProposalProvider (SNSPVProposals).
 * Distribution created by `ant dist` includes site-specific `settings.ini`

## Requirements
 * Java 9 or 10
 * mvn 3 or ant
 * `( cd ..; git clone https://github.com/shroffk/phoebus.git )`

## Build & Run with Maven
```
mvn -DskipTests clean install
java -jar product-sns/target/product-sns-0.0.1-SNAPSHOT.jar 
```

## Build & Run with Ant
```
ant clean
ant product
ant run
ant dist
```

## Develop with Eclipse
 * Import Generic phoebus projects as described in ../phoebus/README.md
 * Import phoebus-sns project (one project)
 * After running the generic "Launcher" once, edit the run configuration
   to add the "phoebus-sns" Project to the Dependencies/Classpath Entries.
 * Run with `-settings /path/to/phoebus-sns/product-sns/settings.ini` to use
   settings that will be included in distribution.
 

## Complete Build and Distribution with Documentation
`pip install Sphinx` or `yum install python-sphinx`


```
git clone https://github.com/kasemir/phoebus-doc.git
git clone https://github.com/shroffk/phoebus.git
git clone https://github.com/kasemir/phoebus-sns.git


# Create generated html doc
( cd phoebus/app/display/editor;  ant -f javadoc.xml clean all )
 
 # Build documentation which includes phobus/app/**/doc
( cd phoebus-doc; make clean html )

# Build products
( cd phoebus/dependencies;  mvn clean install )
( cd phoebus-sns; ant clean dist )

```

## Hudson Example
Builds for all architectures. Adds JDK to some of the ZIP files.

```
# Setup
pwd
export JAVA_HOME=/path/to/JDK
export ORACLE_JDBC_JAR=/path/to/ojdbc8-12.2.0.1.jar
export PATH="$M2_HOME/bin:$ANT_HOME/bin:$JAVA_HOME/bin:$PATH"

# Create doc and base as parallel dirs
rm -f ../phoebus
rm -f ../phoebus-doc
ln -s ../Phoebus/workspace ../phoebus
ln -s ../Phoebus-Doc/workspace ../phoebus-doc

# Create javadoc & help
( cd ../phoebus/app/display/editor;  ant -f javadoc.xml clean all )
( cd ../phoebus-doc; make clean html )

# Update
URL='https://controlssoftware.sns.ornl.gov/css_phoebus/nightly/product-sns-$(arch).zip'
git checkout -- product-sns/settings.ini
../phoebus/app/update/mk_update_settings.sh $URL >> product-sns/settings.ini 


# Windows ---------------------------------------
( cd ../phoebus/dependencies; mvn -Djavafx.platform=win clean install )
ant clean dist
mv product-sns/target/product-sns-0.0.1-win.zip product-sns-0.0.1-win.zip

# Mac  ---------------------------------------
( cd ../phoebus/dependencies; mvn -Djavafx.platform=mac clean install )
ant clean dist
mv product-sns/target/product-sns-0.0.1-mac.zip product-sns-0.0.1-mac.zip

# Linux  ---------------------------------------
# Build the SNS product with maven
mvn -DskipTests -Djavafx.platform=linux clean install

# Show command-line options, basic test that it 'runs'
java -jar product-sns/target/product-sns-0.0.1-SNAPSHOT.jar -help

# Build with ant (online help, dist)
ant clean dist
java -jar product-sns/target/product-sns-0.0.1.jar -help

mv product-sns/target/product-sns-0.0.1-linux.zip product-sns-0.0.1-linux.zip


# Bundle JRE ----------------------------------------------------------
# ---------------------------------------------- Windows
( cd /opt/jdks/windows; zip -r $WORKSPACE/product-sns-0.0.1-win.zip jdk )

# ---------------------------------------------- Mac OS X
sh make_app.sh

# ---------------------------------------------- Linux
# No change...

```


 

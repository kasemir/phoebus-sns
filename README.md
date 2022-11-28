# phoebus-sns

Phoebus (https://github.com/ControlSystemStudio/phoebus) product for SNS

 * Site-specific PVProposalProvider (SNSPVProposals).
 * Distribution created by `ant dist` includes site-specific `settings.ini`

## Requirements
 * Java 17 or higher
 * mvn 3 or ant
 * `( cd ..; git clone https://github.com/shroffk/phoebus.git )`

## Build & Run with Maven
```
mvn -DskipTests clean install
java -jar product-sns/target/product-sns-0.0.1-SNAPSHOT.jar 
```

The maven build downloads all dependencies into `dependencies/phoebus-target/target/lib/`.

## Build & Run with Ant

The ant build is completely local, using `dependencies/phoebus-target/target/lib/`.

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

See `build.sh`

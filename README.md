# phoebus-sns

Phoebus product for SNS

 * Site-specific PVProposalProvider (SNSPVProposals).
 * Distribution created by `ant dist` includes site-specific `settings.ini`

## Requirements
 * Java 9 or 10
 * mvn 3 or ant
 * `( cd ..; git clone https://github.com/shroffk/phoebus.git )`

## Optional: Add documentation
 * `pip install Sphinx` or `yum install python-sphinx`
 * `( cd ..; git clone https://github.com/kasemir/phoebus-doc.git )`
 * `( cd ../phoebus-doc; make clean html )`

## Build & Run with Maven
```
mvn -DskipTests clean install
java -jar product-sns/target/product-sns-0.0.1-SNAPSHOT.jar 
```

## Build & Run with Ant
```
ant clean
ant product-sns
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
 

 
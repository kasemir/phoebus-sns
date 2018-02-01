# phoebus-sns

Phoebus product for SNS


## Requirements
 * Java 9
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
ant run
```

## Develop with Eclipse
 * Import Generic phoebus projects as described in ../phoebus/README.md
 * Import phoebus-sns project (one project)
 * After running the generic "Launcher" once, edit the run configuration
   to add the "phoebus-sns" Project to the classpath. 

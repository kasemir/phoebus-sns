# phoebus-sns

Phoebus product for SNS


## Requirements
 * Java 9
 * mvn 3 or ant
 * `( cd ..; git clone https://github.com/shroffk/phoebus.git )`

## Optional: Add documentation
 * `pip install Spinx`
 * `( cd ..; git clone https://github.com/kasemir/phoebus-doc.git )`
 * `( cd ../phoebus-doc; make clean html )`

## Build & Run with Maven
```
mvn -DskipTests clean install
java -jar phoebus-sns/target/phoebus-sns-0.0.1-SNAPSHOT.jar 
```

## Build & Run with Ant
```
ant run
```

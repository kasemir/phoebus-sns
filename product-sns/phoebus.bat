REM Phoebus launcher for Windows
REM When deploying, you might want to change "TOP"
REM to the absolute installation path

set V=0.0.1

IF EXIST product-sns-%V%.jar (
  SET JAR=product-sns-%V%.jar
) ELSE (
  SET JAR=product-sns-%V%-SNAPSHOT.jar

)

java -jar %JAR% %*


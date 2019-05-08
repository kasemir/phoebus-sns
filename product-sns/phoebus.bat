@REM Phoebus launcher for Windows
@REM
@REM Uses a JDK in ..\jdk
@REM
@REM If you want to use a specific Java Runtime,
@REM replace the following with
@REM    set JAVA_HOME=c:\path\to\that\runtime

IF EXIST "..\jdk" (
    set JAVA_HOME=..\jdk
    @path %JAVA_HOME%\bin;%PATH%
) ELSE (
    ECHO Cannot locate JDK
)

@java -version

@set V=0.0.1

@IF EXIST product-%V%.jar (
  SET JAR=product-%V%.jar
) ELSE (
  SET JAR=product-%V%-SNAPSHOT.jar

)

@REM java -jar %JAR% %*

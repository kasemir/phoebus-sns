@REM Phoebus launcher for Windows
@REM
@REM Uses a JDK in ..\jdk
@REM
@REM If you want to use a specific Java Runtime,
@REM replace the following with
@REM    set JAVA_HOME=c:\path\to\that\runtime

@IF EXIST "%~P0%..\jdk" (
    set JAVA_HOME=%~P0%..\jdk
    @path %JAVA_HOME%\bin
    @ECHO Found JDK %JAVA_HOME%
) ELSE (
    @ECHO Cannot locate JDK
)

@cd %~P0

@java -version

@set V=0.0.1

@IF EXIST product-sns-%V%.jar (
  SET JAR=product-sns-%V%.jar
) ELSE (
  SET JAR=product-sns-%V%-SNAPSHOT.jar
)

@java -jar %JAR% %*

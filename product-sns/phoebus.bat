@REM Phoebus launcher for Windows
@REM
@REM Uses a JDK in ..\jdk
@REM
@REM If you want to use a specific Java Runtime,
@REM replace the following with
@REM    set JAVA_HOME=c:\path\to\that\runtime

@cd %~P0

@IF EXIST "%~P0%..\jdk" (
    setx JAVA_HOME %~P0%..\jdk
    @path %JAVA_HOME%\bin
    @ECHO Found JDK %JAVA_HOME%
) ELSE (
    @ECHO Cannot locate JDK
)

@if EXIST "update" (
    @ECHO Installing update...
    @rd /S/Q doc
    @rd /S/Q lib
    @del product*.jar
    @move /Y update\*.* .
    @move /Y update\doc .
    @move /Y update\lib .
    @rmdir update
    @ECHO Updated.
)

@java -version

@set V=4.6.0

@IF EXIST product-sns-%V%.jar (
    SET JAR=product-sns-%V%.jar
) ELSE (
    SET JAR=product-sns-%V%-SNAPSHOT.jar
)

@java -jar %JAR% %*

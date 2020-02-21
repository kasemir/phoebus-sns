@REM Phoebus launcher for Windows
@REM
@REM Uses a JDK in ..\jdk
@REM
@REM If you want to use a specific Java Runtime,
@REM replace the following with
@REM    set JAVA_HOME=c:\path\to\that\runtime

@cd %~P0

@REM Variables inside IF body are replaced early.
@REM Can set JAVA_HOME, but access to %JAVA_HOME%
@REM inside IF will get the old value.
@IF EXIST "%~P0%..\jdk" (
    set JAVA_HOME=%~P0%..\jdk
    @path %~P0%..\jdk\bin
    @ECHO Found JDK %~P0%..\jdk
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

@set V=4.6.1

@IF EXIST product-sns-%V%.jar (
    SET JAR=product-sns-%V%.jar
) ELSE (
    SET JAR=product-sns-%V%-SNAPSHOT.jar
)


@java -jar %JAR% %*

@REM   Add in case of problems
@REM pause

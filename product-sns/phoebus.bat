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
    
    @IF EXIST "update\jdk" (
        @ECHO Update JDK
        @rd /S/Q ..\jdk
        @move /Y update\jdk ..
    )
    
    @rd /S/Q doc
    @rd /S/Q lib
    @del product*.jar
    @move /Y update\*.* .
    @move /Y update\doc .
    @move /Y update\lib .
    @rmdir /s update
    @ECHO Updated.
)

@java -version

@REM Locate product-sns jar file, any version
echo off
FOR /F "tokens=* USEBACKQ" %%F IN (`dir /B product-sns*.jar`) DO (SET JAR=%%F)
echo on


@java -Dfile.encoding=UTF-8 -DCA_DISABLE_REPEATER=true -jar %JAR% %*

@REM   Add in case of problems
@REM pause

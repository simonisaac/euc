
@echo off

rem #### Change following parameters based on the environment ####

if not defined JAVA_HOME set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_17
if not defined MULE_HOME set MULE_HOME=C:\mule-standalone-3.1.2

rem ##############################################################

set PATH=%PATH%;%JAVA_HOME%\bin
set PATH=%PATH%;%MULE_HOME%\bin
set MULE_LIB=%MULE_HOME%\lib
set MULE_USER=%MULE_HOME%\lib\user

@echo on

rem ### Delete the existing custom components before copying the new one across ###
rem ### This will safegaurd against version updates for these custom components ###
del %MULE_HOME%\apps\app-euc\*.*
del %MULE_HOME%\apps\app-euc\lib\*.*
del %MULE_HOME%\apps\app-euc\classes\*.*
del %MULE_HOME%\apps\app-euc\classes\xsd\*.*
del %MULE_USER%\nodeservice*.*
del %MULE_USER%\poi*.*
del %MULE_USER%\jxls-core*.*
del %MULE_USER%\xsd\*.*

mkdir %MULE_HOME%\apps\app-euc
mkdir %MULE_HOME%\apps\app-euc\lib
mkdir %MULE_HOME%\apps\app-euc\classes
mkdir %MULE_USER%\xsd

rem ### copy all the custom jars to the mule lib\user directory so that mule can load custom components 
copy *.jar %MULE_HOME%\apps\app-euc\lib
copy *.properties %MULE_HOME%\apps\app-euc\classes
copy *.xml %MULE_HOME%\apps\app-euc
copy xsd\*.xsd %MULE_USER%\xsd
move %MULE_HOME%\apps\app-euc\lib\nodeservice.transform.excel-*.jar %MULE_USER%
move %MULE_HOME%\apps\app-euc\lib\nodeservice.core-*.jar %MULE_USER%
move %MULE_HOME%\apps\app-euc\lib\poi-*.jar %MULE_USER% 
move %MULE_HOME%\apps\app-euc\lib\jxls-core-*.jar %MULE_USER%  

%MULE_HOME%\bin\mule -app app-euc
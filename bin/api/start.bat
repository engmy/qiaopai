@echo off
title ���ƽӿڷ���
cd %~dp0
setlocal enabledelayedexpansion
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m
start javaw -Dfile.encoding=utf-8 -jar %JAVA_OPTS% api-0.0.1-snapshot.jar --spring.profiles.active=pro
echo.
echo ------------------------------
echo ���ƽӿڷ���������
echo ------------------------------
echo.
pause
exit
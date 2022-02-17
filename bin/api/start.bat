@echo off
title 壳牌接口服务
cd %~dp0
setlocal enabledelayedexpansion
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m
start javaw -Dfile.encoding=utf-8 -jar %JAVA_OPTS% api-0.0.1-snapshot.jar --spring.profiles.active=pro
echo.
echo ------------------------------
echo 壳牌接口服务已启动
echo ------------------------------
echo.
pause
exit
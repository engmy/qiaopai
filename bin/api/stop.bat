@echo off
title 壳牌管理服务
setlocal enabledelayedexpansion
for /f "tokens=1-5" %%a in ('netstat -ano ^| find ":6002"') do (
	if "%%e%" == "" (
		set pid=%%d
	) else (
		set pid=%%e
	)
	echo !pid!
	taskkill /f /pid !pid!
)
echo.
echo ------------------------------
echo 壳牌接口服务已停止
echo ------------------------------
echo.
pause
exit
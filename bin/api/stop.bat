@echo off
title ���ƹ������
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
echo ���ƽӿڷ�����ֹͣ
echo ------------------------------
echo.
pause
exit
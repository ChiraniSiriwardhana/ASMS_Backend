@echo off
COLOR 0A
title ASMS Backend - Port 8080 Fix and Start

echo.
echo ========================================================
echo    ASMS BACKEND - PORT 8080 FIX AND START
echo ========================================================
echo.
echo This script will:
echo   1. Find and stop any process using port 8080
echo   2. Start your Spring Boot application
echo.
echo ========================================================
echo.

REM Check if running as administrator
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo WARNING: Not running as administrator.
    echo Some operations may fail.
    echo.
    echo Recommended: Right-click and "Run as administrator"
    echo.
    pause
)

echo [STEP 1/3] Checking port 8080...
echo.

REM Find process using port 8080
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    set PID=%%a
    goto :found
)

echo Port 8080 is free. No process to kill.
goto :start

:found
echo Found process using port 8080: PID %PID%
echo Killing process %PID%...
taskkill /F /PID %PID% >nul 2>&1
if %errorLevel% equ 0 (
    echo   SUCCESS! Process killed.
) else (
    echo   WARNING: Could not kill process. It may have already stopped.
)
timeout /t 2 /nobreak >nul
echo.

:start
echo [STEP 2/3] Verifying port 8080 is free...
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if %errorLevel% equ 0 (
    echo   WARNING: Port 8080 is still in use!
    echo   Please close any applications using this port manually.
    echo.
    pause
    exit /b 1
) else (
    echo   SUCCESS! Port 8080 is now free.
)
echo.

echo [STEP 3/3] Starting Spring Boot application...
echo.
echo ========================================================
echo   APPLICATION STARTING
echo ========================================================
echo.
echo   Backend URL: http://localhost:8080
echo   Database: demo (PostgreSQL)
echo.
echo IMPORTANT: Keep this window open!
echo Closing this window will stop the application.
echo.
echo Press Ctrl+C to stop the application.
echo.
echo ========================================================
echo.
timeout /t 2 /nobreak >nul

REM Start the application
call mvnw.cmd spring-boot:run

echo.
echo ========================================================
echo   APPLICATION STOPPED
echo ========================================================
echo.
pause


@echo off
set /P confirm=Are you sure? :
if "%confirm%"=="y" (
    echo.Processing...
    call mvn clean deploy
)

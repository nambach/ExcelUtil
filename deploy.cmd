@echo off
set /P confirm=Are you sure? :
if "%1"=="y" (
    call mvn clean deploy -P release
)

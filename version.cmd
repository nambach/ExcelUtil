if "%1"=="" (
  echo.Please provide version number
  GOTO EXIT
)
call mvn versions:set -DnewVersion="%1"
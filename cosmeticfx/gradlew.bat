@echo off
setlocal
set "APP_HOME=%~dp0"
set "PROPS=%APP_HOME%gradle\wrapper\gradle-wrapper.properties"

for /f "tokens=1,* delims==" %%A in ('findstr /b "distributionUrl=" "%PROPS%"') do set "DIST_URL=%%B"
set "DIST_URL=%DIST_URL:\:=:%"
set "DIST_URL=%DIST_URL:\=%"

for /f "tokens=2 delims=gradle-" %%A in ("%DIST_URL%") do set "GRADLE_VERSION=%%A"
for /f "tokens=1 delims=-" %%A in ("%GRADLE_VERSION%") do set "GRADLE_VERSION=%%A"

if not defined GRADLE_VERSION (
  echo Could not determine Gradle version from %PROPS%
  exit /b 1
)

if defined GRADLE_USER_HOME (
  set "GH=%GRADLE_USER_HOME%"
) else (
  set "GH=%USERPROFILE%\.gradle"
)

set "DIST_DIR=%GH%\wrapper\dists\gradle-%GRADLE_VERSION%-bin"
set "INSTALL_DIR=%DIST_DIR%\gradle-%GRADLE_VERSION%"

if exist "%INSTALL_DIR%\bin\gradle.bat" goto run

mkdir "%DIST_DIR%" >nul 2>nul
set "TMP=%DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
echo Downloading Gradle %GRADLE_VERSION%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%DIST_URL%' -OutFile '%TMP%'"
if errorlevel 1 exit /b 1
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%TMP%' '%DIST_DIR%'"
del /q "%TMP%" >nul 2>nul

:run
call "%INSTALL_DIR%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%

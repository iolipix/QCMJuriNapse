@echo off
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%PATH%

echo Compilation de QCM Juridique Napse...
echo.

if not exist "gradle-8.4" (
    echo Telechargement de Gradle...
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.4-bin.zip' -OutFile 'gradle.zip'"
    powershell -Command "Expand-Archive -Path 'gradle.zip' -DestinationPath '.' -Force"
)

echo Configuration Java: %JAVA_HOME%
echo.
java -version
echo.

echo Compilation en cours...
gradle-8.4\bin\gradle.bat assembleDebug

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ✓ APK genere avec succes !
    echo Fichier: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Transfer ce fichier sur ton Poco F3 et installe-le !
) else (
    echo.
    echo ✗ Erreur lors de la compilation
)

pause
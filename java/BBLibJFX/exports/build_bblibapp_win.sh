#!/bin/sh
DATE=`date +%Y-%m-%d_%H-%M-%S`
ZIP_NAME=${DATE}_BBLibApp_win.zip

cd ..; java "-jar" "proguard.jar" "@proguard.cfg"
cd "exports"
./launch4j/launch4j "build_bblibapp_wincfg.xml"
echo "Zipping JAR"
cd "../store"; zip "-r" "../exports/bin/$ZIP_NAME" "BBLibApp.exe"
rm "BBLibApp.exe"
echo "DONE"

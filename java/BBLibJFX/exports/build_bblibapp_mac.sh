#!/bin/sh

cd ..; java "-jar" "proguard.jar" "@proguard.cfg"
cd "exports"

version=$(unzip -p ../store/bblib_enc.jar META-INF/MANIFEST.MF | grep Implementation-Version)
version=${version:24}
version=${version:0:${#version}-1}

app_name="BBLibApp M${version}0"
out_folder="bin"
app_out=${out_folder}/${app_name}

echo "Create MAC app for BBLib $version"

mkdir $out_folder
rm "-rf" "$app_out.app"
jar2app "../store/bblib_enc.jar" "-i" "bblibapp.icns" "-n" "$app_name" "-j" "-Dprism.order=sw -Dprism.verbose=true" #"-r" "/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk"
mv "$app_name.app" "$app_out.app"
#cp "-R" "../dist/lib" "$app_out.app/Contents/Java"

DATE=`date +%Y-%m-%d_%H-%M-%S`
ZIP_NAME=${DATE}_BBLibApp_mac.zip

echo "Zipping JAR"
cd "bin"; zip "-r" "$ZIP_NAME" "$app_name.app"

rm "-rf" "$app_out.app"
#!/bin/sh

version=$(unzip -p ../dist/BBJarUpdater.jar META-INF/MANIFEST.MF | grep Implementation-Version)
version=${version:24}
version=${version:0:${#version}-1}

app_name="BBUpdater $version"
out_folder="builds"
app_out=${out_folder}/${app_name}

echo "Create MAC app for updater"

mkdir $out_folder
rm "-rf" "$app_out.app"
jar2app "../dist/BBJarUpdater.jar" "-i" "icon.icns" "-n" "$app_name" #"-r" "/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk"
mv "$app_name.app" "$app_out.app"
cp "-R" "../dist/lib" "$app_out.app/Contents/Java"
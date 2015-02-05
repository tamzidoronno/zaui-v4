echo "Please enter the versionnumber. followed by [ENTER]:"
read version
echo "Please enter the versioncode followed by [ENTER]:"
read versioncode
cd platforms/android/;
cp AndroidManifest-NO.xml AndroidManifest.xml;
sed -i "s/{APK_VERSION}/$version/g" AndroidManifest.xml
phonegap local build android;
cp AndroidManifest-NO.xml AndroidManifest.xml;
sed -i "s/{APK_VERSION}/$version/g" AndroidManifest.xml
sed -i "s/{APK_VERSION_CODE}/$versioncode/g" AndroidManifest.xml
ant release;
cp -rf bin/HelloWorld-release.apk ../../apk/ProMeister\ Academy\ Norway-$version.apk
cd ../../;

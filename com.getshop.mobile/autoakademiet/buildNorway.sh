echo "Please enter the versionnumber. followed by [ENTER]:"
read version
phonegap local build android;
cd platforms/android/;
cp AndroidManifest-NO.xml AndroidManifest.xml;
sed -i "s/{APK_VERSION}/$version/g" AndroidManifest.xml
ant release;
cp -rf bin/HelloWorld-release.apk ../../apk/ProMeister\ Academy\ Norway-$version.apk
cd ../../;

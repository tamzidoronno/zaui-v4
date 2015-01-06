echo "Please enter the versionnumber. followed by [ENTER]:"
read version
cd platforms/android/;
cp AndroidManifest-NO.xml AndroidManifest.xml;
sed -i "s/{APK_VERSION}/$version/g" AndroidManifest.xml
phonegap local build android;
ant release;
cp -rf bin/HelloWorld-release.apk ../../apk/ProMeister\ Academy\ Norway-$version.apk
cd ../../;

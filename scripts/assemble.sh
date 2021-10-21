#!/bin/bash
gradleFolder=`pwd`
folder="artifacts/builds/"$1"/tmp"
zipFile=backend_$1.tar.gz

#Java package
mkdir -p $folder/dist/libs
cd $folder
cp $gradleFolder/downloaded/* dist/libs/
cp $gradleFolder/scripts/start.sh dist/
cp $gradleFolder/core/build/libs/core-4.1.0.jar dist
cp $gradleFolder/messages/build/libs/messages-4.1.0.jar dist
cp -rf $gradleFolder/apitodb.json .
tar zcf $zipFile *
mv $zipFile ../
cd ../
rm -rf tmp

#Php package
mkdir php
cd php
rsync -a --exclude='/app' --exclude='uploadedfiles' --exclude='gimp' --exclude='docs' --exclude='etc' $gradleFolder/com.getshop.client/ .
tar czf php_$1.tar.gz *
mv php_$1.tar.gz ../
cd ../
rm -rf php;

#Javascript API
cp $gradleFolder/core/build/getshopapi.js .
cp $gradleFolder/javaapi/build/libs/javaapi-4.1.0.jar .

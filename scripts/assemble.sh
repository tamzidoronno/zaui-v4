#!/bin/bash
gradleFolder=`pwd`
folder="artifacts/builds/"$1"/tmp"
zipFile=backend_$1.tar.gz

#Java package
mkdir -p $folder/dist/libs
cd $folder
cp $gradleFolder/libs/* dist/libs/
cp $gradleFolder/scripts/start.sh dist/
cp $gradleFolder/core/build/libs/core-3.0.0.jar dist
cp $gradleFolder/messages/build/libs/messages-3.0.0.jar dist
cp -rf $gradleFolder/apitodb.json .
tar zcvf $zipFile *
mv $zipFile ../
cd ../
rm -rf tmp

#Php package
mkdir php
cd php
rsync -av --exclude='/app' --exclude='uploadedfiles' --exclude='gimp' --exclude='docs' --exclude='etc' $gradleFolder/com.getshop.client/ .
tar czvf php_$1.tar.gz *
mv php_$1.tar.gz ../
cd ../
rm -rf php;

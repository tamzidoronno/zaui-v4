#!/bin/bash
gradleFolder=`pwd`
folder="/opt/getshop/builds/"$1"/tmp"
zipFile=backend_$1.tar.gz

#Java package
mkdir -p $folder/dist
cd $folder
cp $gradleFolder/core/build/libs/core-3.0.0.jar dist
cp -rf $gradleFolder/apitodb.json .
tar zcvf $zipFile *
mv $zipFile ../
cd ../
rm -rf $folder

mkdir php
cd php
rsync -av --exclude='app' --exclude='uploadedfiles' --exclude='gimp' --exclude='docs' --exclude='etc' $gradleFolder/com.getshop.client/ .
tar czvf php_$1.tar.gz *
mv php_$1.tar.gz ../
cd ../
rm -rf php;

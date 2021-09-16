#!/bin/bash
cd ../../
mkdir tmpfrontend
cd tmpfrontend
cp -r ../com.getshop.client/* .
rm -rf etc
rm -rf app

mkdir app;
mkdir etc;
chmod 777 app;
chmod 777 etc;

rm -rf events/
cp -r ../../build/events .

echo "backenddb=116.202.132.139" > etc/config.txt
echo "port=25554" >> etc/config.txt

#Extra folders
echo "Creating extra folders";
mkdir ROOT/cssfolder
mkdir ROOT/javascripts
chmod 777 ROOT/cssfolder
chmod 777 ROOT/javascripts

cd ../
rm -fr frontend
mv tmpfrontend frontend

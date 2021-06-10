#!/bin/bash
cd C:/wamp64/www/getshop-v4
mkdir tmpfrontend
cd tmpfrontend
cp -r ../com.getshop.client/* .
rm -rf etc
rm -rf app

mkdir app;
mkdir etc;
chmod 777 app;
chmod 777 etc;

echo "backenddb=116.202.132.139" > etc/config.txt
echo "port=25554" >> etc/config.txt

#Extra folders
echo "Creating extra folders";
mkdir ROOT/cssfolder
mkdir ROOT/javascripts
chmod 777 ROOT/cssfolder
chmod 777 ROOT/javascripts

cd ../
cp -r tmpfrontend C:/wamp64/www/getshopServer
rm -rf tmpfrontend;

cd C:/wamp64/www/getshopServer
rm -fr frontend
mv tmpfrontend frontend

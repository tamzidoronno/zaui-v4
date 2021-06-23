#!/bin/bash

echo "Deploying Frontend code to Apache docroot"
#Enter your project directory
project_directory=$HOME
mkdir $project_directory/getshop-php
mkdir $project_directory/getshop-php/dist
cp $project_directory/getshop-v4/artifacts/builds/4.1.0/php_4.1.0.tar.gz $project_directory/getshop-php/dist
cd $project_directory/getshop-php/dist

FILE=php_4.1.0.tar.gz
if test -f "$FILE"; then

	mkdir tmp;
	cd tmp;

	echo "Unzipping";
	tar zxvf ../php_4.1.0.tar.gz > /dev/null 2>&1;

	echo "Adding config file";
	mkdir app;
	mkdir etc;
	chmod 777 app;
	chmod 777 etc;

	echo "backenddb=127.0.0.1" > etc/config.txt
	echo "port=25554" >> etc/config.txt

	# Setting up symlink
	ln -s /thundashopimages uploadedfiles

	#Extra folders
	echo "Creating extra folders";
	mkdir ROOT/cssfolder
	mkdir ROOT/javascripts
	chmod 777 ROOT/cssfolder
	chmod 777 ROOT/javascripts

	echo "Adding partner applications";
	cd app;
	ln -s ../../partners/jason/ns_747cc6f9_6c1b_4759_b03d_16c474cc77ae ns_747cc6f9_6c1b_4759_b03d_16c474cc77ae
	ln -s ../../partners/jason/ns_94ca2750_341c_4290_93c1_5ec8a447d292 ns_94ca2750_341c_4290_93c1_5ec8a447d292
	ln -s ../../partners/jason/ns_e72c1c7d_4af8_41ca_bb1b_22d8468edf0a ns_e72c1c7d_4af8_41ca_bb1b_22d8468edf0a
	cd ../

	echo "Activating and cleaning up";
	cd ../
	sudo rm -rf frontend
	mv tmp frontend
	rm -rf php_4.1.0.tar.gz
	#sudo /etc/init.d/apache2 restart
else
    echo "Release file at $FILE does not exist. No action taken."
fi;


echo "Deploying Backend code to Apache docroot"
mkdir $project_directory/getshop-java
mkdir $project_directory/getshop-java/dist
cp $project_directory/getshop-v4/artifacts/builds/4.1.0/backend_4.1.0.tar.gz $project_directory/getshop-java/dist
cd $project_directory/getshop-java/dist

FILE=backend_4.1.0.tar.gz
if test -f "$FILE"; then
    echo "Found valid backend release file at $FILE"
    echo "Stopping java";
    kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'` > /dev/null 2>&1

    echo "Cleaning up old java files";
    rm -rf dist/*.jar
    rm -rf dist/libs

    tar xzvf backend_4.1.0.tar.gz > /dev/null;
    chmod +x dist/start.sh;
    cd dist;
    ./start.sh
    cd ..;
    rm -rf backend_4.1.0.tar.gz;
else
    echo "Backend release file at $FILE does not exist. No action taken."
fi

#!/bin/bash
DIR=databases/$1

if [ -d "$DIR" ]; then
	echo "Making package"
	if [ -d "package" ]; then
		rm -rf package
	fi
	if [ -f "package.tar.gz" ]; then
		rm -rf package
	fi
	mkdir package;
	cd package;
	cp -rf ../www/* .
	cd root;
	rm -rf `find . |grep DomainConfig.php |sed 's/\// /g' |awk {'print $2'}`
	cp -rf ../../www/root/$1 .
	cd ../
	mkdir $1
	mv * $1 2> /dev/null
	mkdir databases;
	cp -rf ../databases/$1 databases;
	tar czvf package.tar.gz * > /dev/null
	mv package.tar.gz ../
	cd ../
	rm -rf package;

	echo "Uploading package";
	scp package.tar.gz naxa@10.0.4.50:.
	rm -rf package.tar.gz

	ssh naxa@10.0.4.50 "./release.sh $1"
else
	echo "Did not find webpage in databases folder"
	echo "Usage $0 <webpage>";
	exit 1;
fi

#!/bin/bash
# New setupscript after move to new build server, new repos, etc.

# check and if need be remove tmp folder
if [ -d "tmp" ]; then
	rm -rf tmp;
fi;

# check where we are and set server to our corresponding backend server
SERVER="10.0.9.99"

FILE=/home/naxa/php_4.1.0.tar.gz
if test -f "$FILE"; then
    echo "New release file found"

	mkdir tmp;
	cd tmp;

	echo "Unzipping";
	tar zxvf $FILE > /dev/null 2>&1;

	echo "Adding config file";
	mkdir app;
	mkdir etc;
	chmod 777 app;
	chmod 777 etc;

	echo "backenddb=$SERVER" > etc/config.txt
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
	sudo rm -rf www
	mv tmp www
	rm -rf php_4.1.0.tar.gz
	#sudo /etc/init.d/apache2 restart

else
    echo "Release file at $FILE does not exist. No action taken."
fi;
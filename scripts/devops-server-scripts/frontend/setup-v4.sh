#!/bin/bash
# New setupscript after move to new build server, new repos, etc.

# check and if need be remove tmp folder
if [ -d "tmp" ]; then
	rm -rf tmp;
fi;

# check where we are and set server to our corresponding backend server
SERVER="10.0.4.33"
ifconfig | grep '4.32' &> /dev/null
if [ $? == 0 ]; then
   echo "We are on Cluster 4"
   SERVER="10.0.4.33"
fi
ifconfig | grep '5.32' &> /dev/null
if [ $? == 0 ]; then
   echo "We are on Cluster 5"
   SERVER="10.0.5.33"
fi

ifconfig | grep '6.32' &> /dev/null
if [ $? == 0 ]; then
   echo "We are on Cluster 6"
   SERVER="10.0.6.33"
fi

ifconfig | grep '9.32' &> /dev/null
if [ $? == 0 ]; then
   echo "We are on Cluster 9"
   SERVER="10.0.9.33"
fi


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
#!/bin/bash

if [ "1" == `ping -c 1 www.getshop.com |grep "bytes from" |grep 127.0.0.1 |wc -l` ]
  then
     echo -e "";
     echo -e " ########## ERROR !!!   ###############";
     echo -e " # www.getshop.com is in /etc/hosts   #";
     echo -e " ######################################";
     echo -e "";
     exit;
fi
  
#DELETE LOCAL DATABASE
echo -e "";
echo -e " ####################################";
echo -e " # Syncing online database to local #";
echo -e " ####################################";
echo -e "";
echo -e " Deleting local database";
mongo <<< 'db.adminCommand("listDatabases").databases.forEach( function (d) {   if (d.name != "local" && d.name != "admin" && d.name != "config") db.getSiblingDB(d.name).dropDatabase(); });' > /dev/null;

#Dumping online database and compressing it.
echo -e " Dumping and compressing database on server";
ssh -T naxa@backend.getshop.com << EOF > /dev/null
/home/naxa/backup.sh
EOF

#transfer file
cat << EOF > batchfile
get dump.tar.gz
EOF

echo -e " Fetching database file from server";
sftp -b batchfile naxa@backend.getshop.com > /dev/null
rm -rf batchfile;

echo -e " Importing database to local";
tar xzvf dump.tar.gz > /dev/null
mongorestore &> /dev/null
rm -rf dump.tar.gz

#transfer images
echo -e " Syncing images";
rsync -avz -e ssh naxa@www.getshop.com:/thundashopimages/ ../com.getshop.client/uploadedfiles/ &> /dev/null
rsync -avz -e ssh naxa@www.getshop.com:/home/naxa/FrontEnd/ROOT/diplomapictures/ ../com.getshop.client/ROOT/diplomapictures/ &> /dev/null

echo -e " Done!"
echo -e " Note: if you wish to run resin on port 80 run: "
echo -e "   iptables -t nat -A OUTPUT -d localhost -p tcp --dport 80 -j REDIRECT --to-ports 8080";

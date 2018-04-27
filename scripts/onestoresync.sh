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

echo "What server do you want to sync from?"
echo "3 = Server 3 ( First server ever created )";
echo "4 = Server 4 ( Created: 8 june 2017 )";
read serverQuestion;


SERVER="NONE";
if [ $serverQuestion = "3" ]; then
        SERVER="148.251.15.227";
elif [ $serverQuestion = "4" ]; then
        SERVER="138.201.203.177"
else
        echo "Invalid server setup";
fi;

echo "What store?";
cat $serverQuestion | while read line
do
  array=(${line//;/ })
  echo ${array[0]}"="${array[2]} ${array[3]} ${array[4]} ${array[5]} ${array[6]} ${array[7]};
done
read storeQuestion;

STOREID="NONE";

while read line
do
  array=(${line//;/ })
  val=${array[0]};
  if [ "$storeQuestion" == "$val" ]
  then
      STOREID="${array[1]}";
  fi
done <<< "$(cat $serverQuestion)"

if [ $STOREID = "NONE" ]; then
        echo "Invalid store setup";
        exit 1;
fi;
  
#DELETE LOCAL DATABASE
echo -e "";
echo -e " ####################################";
echo -e " # Syncing online database to local #";
echo -e " ####################################";
echo -e "";
echo -e " Deleting local database";
mongo --port 27018 <<< 'db.adminCommand("listDatabases").databases.forEach( function (d) {   if (d.name != "local" && d.name != "admin" && d.name != "config") db.getSiblingDB(d.name).dropDatabase(); });' > /dev/null;

#Dumping online database and compressing it.
echo -e " Dumping and compressing database on server";
ssh -oPort=4223 -T naxa@$SERVER << EOF > /dev/null
/home/naxa/backup2.sh $STOREID
EOF

if [ -f dump.tar.gz ]; then
    rm -rf dump.tar.gz;
fi

#transfer file
cat << EOF > batchfile
get dump.tar.gz
EOF

echo -e " Fetching database file from server";
sftp -oPort=4223 -b batchfile naxa@$SERVER > /dev/null
rm -rf batchfile;

echo -e " Importing database to local";
if [ -d dump ]; then
    rm -rf dump;
fi

tar xzvf dump.tar.gz > /dev/null
mongorestore --port 27018  &> /dev/null
rm -rf dump.tar.gz

#transfer images
echo -e " Syncing images";
rsync -avz -e ssh naxa@10.0.4.32:/thundashopimages/ ../com.getshop.client/uploadedfiles/ &> /dev/null

echo -e " Done!"
echo -e " Note: if you wish to run resin on port 80 run: "
echo -e "   iptables -t nat -A OUTPUT -d localhost -p tcp --dport 80 -j REDIRECT --to-ports 8080";

echo "Importing shared database"
rm -rf dump;
mongodump --host clients.getshop.com --port 27017 -u getshopadmin -p commondatabaseadministrator &> /dev/null
mongorestore --port 27018 &> /dev/null

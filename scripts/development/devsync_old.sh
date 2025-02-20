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
IFS=';'
echo "What store?";
cat 9 | while read line
do
  array=(${line})
  echo ${array[0]}"="${array[2]};
done
read storeQuestion;

echo  -e " GO ON HERE ";
IFS=';'

serverQuestion="9"
SERVER="10.0.9.33"
STOREID="NONE";
MASTERSTOREID="NONE";
while read line
do
  array=(${line})
  val=${array[0]};
  if [ "$storeQuestion" == "$val" ]
  then
      echo "FOUND IN FILE"
      STOREID="${array[1]}";
      if [ ${#array[@]}  == 4 ] ; then
          MASTERSTOREID="${array[3]}";
      fi
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
echo " Deleting exisisting local collections for $STOREID";
echo -e "";
mongo --port=27018 --eval "var storeId='$STOREID'" onestoresync_clear_existing_data.js
#Dumping online database and compressing it.
echo -e " Dumping and compressing database on server";

ssh -T naxa@$SERVER << EOF > /dev/null
/home/naxa/backup2.sh $STOREID $MASTERSTOREID >> /dev/null 2>&1
EOF
if [ -f dump.tar.gz ]; then
    rm -rf dump.tar.gz;
fi
#transfer file
cat << EOF > batchfile
get dump.tar.gz
EOF
echo -e " Fetching database file from server";
sftp -b batchfile naxa@$SERVER > /dev/null
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
#rsync -avz -e ssh naxa@10.0.4.32:/thundashopimages/ ../com.getshop.client/uploadedfiles/ &> /dev/null
echo -e " Done!"
echo -e " Note: if you wish to run resin on port 80 run: "
echo -e "   iptables -t nat -A OUTPUT -d localhost -p tcp --dport 80 -j REDIRECT --to-ports 8080";
echo "Importing shared database"
mongodump -o shareddump --host 192.168.100.1 --port 27017 -u getshopadmin -p commondatabaseadministrator &> /dev/null
mongorestore --port 27018 shareddump &> /dev/null

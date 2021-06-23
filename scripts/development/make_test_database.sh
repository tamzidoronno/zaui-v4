#!/bin/bash
# dump originaldb   # upload dump to testdb   # run gdprizer

set -e
PORT_SOURCEDB=$1 # port of source mongo db
PORT_TESTDB=$2 # port where all mocked data will end up, default 27017
if [ -z "$PORT_SOURCEDB" ]
then
   echo "Please restart and provide port of source database as input."
   exit 0
fi
if [ -z "$PORT_TESTDB" ]
then
   PORT_TESTDB=27017
fi
# dump originaldb
if [ -d dump ]; then
    rm -rf dump;
fi
echo "Dumping old db.."
DIR="$(pwd)"
DIR_DUMP=$PORT_SOURCEDB
$DIR/mongodump.sh -d $DIR_DUMP -p $PORT_SOURCEDB

# upload dump to testdb
echo "Importing old db.."
cd $DIR_DUMP
tar xzvf dump.tar.gz > /dev/null
mongorestore --port $PORT_TESTDB  &> /dev/null
rm -rf dump.tar.gz

# run gdprizer
echo `python3 --version`
echo "Make sure you have installed requirenments.txt"
python3 $DIR/mongo_gdprizer/gdprizer.py --port $PORT_TESTDB
echo "GDPR done"
## dump testdb
#rm -rf dump;
#echo "Dumping new testing db.."
#$DIR/backup2.sh -d $DIR -p $PORT_TESTDB  -i "8016f02d-95bc-4910-b600-bbea8c06bedf"
#echo "All done."


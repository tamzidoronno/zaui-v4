#!/bin/bash
# dump originaldb   # upload dump to testdb   # run gdprizer

PORT_SOURCEDB=$1 # port of source mongo db
DIR=$2
PORT_TESTDB=$3 # port where all mocked data will end up, default 27017
if [ -z "$PORT_SOURCEDB" ]
then
   echo "Please restart and provide port of source database on input arg-1."
   exit 0
fi
if [ -z "$DIR" ]
then
   echo "Please restart and provide working directory on input arg-2."
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
echo "Dumping source db from port $PORT_SOURCEDB.."
DIR_DUMP=$DIR
$DIR/mongodump.sh -d $DIR_DUMP -p $PORT_SOURCEDB
cluster_number=${PORT_SOURCEDB:(-1)}
#delete existing data in test db for collections which will be imported
echo "Deleting existing overlapping data in test db .. using mongodrop_$cluster_number.sh"
$DIR/mongodrop_$cluster_number.sh
# upload dump to testdb
echo "Importing source db.."
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


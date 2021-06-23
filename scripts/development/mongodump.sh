#!/bin/bash
# dump of whole database in a form of dump.tar.gz
# input -d for the working directory, and -p for mongodb port
while getopts d:p: flag
  do
      case "${flag}" in
          d) dir=${OPTARG};;
          p) port=${OPTARG};;
      esac
  done
echo "dir $dir  port: $port"
if [ -z "$dir" ]
then
   path="~"
fi
if [ -z "$port" ]
then
   port=27018
fi
echo "connected to port: $port"

cd $dir;
if [ -d dump ]; then
    rm -rf dump;
fi
dbs=`echo "rs.slaveOk(); db.getMongo().getDBNames()"|mongo --port $port --quiet |tr -d \[\] | tr , "\n"|cut -c3-| tr -d \" |grep -Ev "^$"`

for db in $dbs
do
	[ -z "$db" ] && continue
	mongodump --forceTableScan --db $db  --port $port --host localhost  &> /dev/null

done

cd dump;
#rm -rf LoggerManager;
cd ../;
if [ -f dump.tar.gz ]; then
    rm -rf dump.tar.gz
fi

DATE=`date`;
tar zcf dump.tar.gz dump

#!/bin/bash
#
echo "Dump executed.";

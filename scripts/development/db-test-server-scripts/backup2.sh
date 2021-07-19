#!/bin/bash
# dumping database in a form of dump.tar.gz based on store_id
# input -d for the working directory, and -p for mongodb port, -i for store_id
# (!important to  put flags in the specified order)
while getopts d:p:i: flag
  do
      case "${flag}" in
          d) dir=${OPTARG};;
          p) port=${OPTARG};;
          i) id=${OPTARG};;
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
dbs=`echo "db.getMongo().getDBNames()"|mongo --port $port --quiet |tr -d \[\] | tr , "\n"|cut -c3-| tr -d \" |grep -Ev "^$"`
echo "$dbs"

for db in $dbs
do
	[ -z "$db" ] && continue
	mongodump --forceTableScan --db $db --collection "col_47522cce-8fda-4538-9b61-84eb72faf03f"  --port $port --host localhost &> /dev/null
	mongodump --forceTableScan --db $db --collection "col_13442b34-31e5-424c-bb23-a396b7aeb8ca"  --port $port --host localhost &> /dev/null
	mongodump --forceTableScan --db $db --collection "col_$id"  --port $port --host localhost &> /dev/null
	mongodump --forceTableScan --db $db --collection "col_$id_log"  --port $port --host localhost &> /dev/null
	mongodump --forceTableScan --db $db --collection "col_all"  --port $port --host localhost &> /dev/null
	mongodump --forceTableScan --db $db --collection "dbscripts"  --port $port --host localhost &> /dev/null
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

#!/bin/bash
# dump of whole database on a specific port in a form of dump.tar.gz
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
echo "working directory:: $dir"

cd $dir;
if [ -d dump ]; then
    rm -rf dump;
    rm -rf dump.*;
fi
mkdir dump
dbs=`echo "rs.slaveOk(); db.getMongo().getDBNames()"|mongo --port $port --quiet |tr -d \[\] | tr , "\n"|cut -c3-| tr -d \" |grep -Ev "^$"`
cluster_number=${port:(-1)}

for db in $dbs
  do
    [ -z "$db" ] && continue
    if [[ $db != *"_"* || ( $db == *"_default"  ) ]]; then
      if [[ $db != "test" && ( $db != "local" ) ]]; then
        echo "Dumping $db"
	pwd
        $dir/dump_collections_of_cluster$cluster_number.sh $port $db


      fi
    fi

  done


#rm -rf LoggerManager;
if [ -f dump.tar.gz ]; then
    rm -rf dump.tar.gz
fi

DATE=`date`;
tar zcf dump.tar.gz dump

echo "Dump executed.";

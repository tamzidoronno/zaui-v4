#!/bin/bash

while getopts "p:" opt; do

  case $opt in
  p) port=${OPTARG} ;;
  esac

done

if [ -z "$port" ]; then
  port=27018
fi

readonly PMS_MANAGER="PmsManager_default"
readonly FILTER_CONDITION='{"className": "com.thundashop.core.pmsmanager.PmsLog"}'
readonly COLLECTIONS=$(echo "db.getMongo().getDB(\"${PMS_MANAGER}\").getCollectionNames()" | mongo --port $port --quiet | tr -d \[\] | tr , "\n" | cut -c3- | tr -d \" | grep -Ev "^$")

echo "Going to delete from collections: ${COLLECTIONS}"

while true; do
    read -p "Really want to delete? Think again..." yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

for it in $COLLECTIONS; do

  echo -e "\n ********************************** \n"
  countQuery="db.getMongo().getDB(\"${PMS_MANAGER}\").getCollection('${it}').find(${FILTER_CONDITION}).count()"
  echo "$(echo $countQuery | mongo --port $port --quiet) PmsLog will be deleted from ${it}"

#  deleteQuery="db.getMongo().getDB(\"${PMS_MANAGER}\").getCollection('${it}').deleteMany(${FILTER_CONDITION})"
#  echo $deleteQuery | mongo --port $port --quiet

done

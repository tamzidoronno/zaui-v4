#!/bin/bash

# The purpose of this script is to migrate PmsLog from PmsManager_default to PmsLogManager database.

# This scripts takes 1 command line argument.
#   `-p` is for the configuration of the mongodb port. If none is passed default port will be set to 27018.

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

for it in $COLLECTIONS; do

  nsFrom="${PMS_MANAGER}.${it}"
  nsTo="PmsLogManager.${it}"

  echo -e "\n ********************************** \n"

  mongodump --query "${FILTER_CONDITION}" \
    --db $PMS_MANAGER \
    --collection $it \
    --port $port \
    --archive=- |
    mongorestore --port $port \
      --nsFrom=$nsFrom \
      --nsTo=$nsTo --archive=-

done
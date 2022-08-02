#!/bin/bash

echo "What store?";

cat 100 | while read line

do

  array=(${line})

  echo ${array[0]}"="${array[2]};

done

read storeQuestion;


IFS=';'



serverQuestion="100"

STOREID="NONE";


while read line

do

  array=(${line})

  val=${array[0]};

  if [ "$storeQuestion" == "$val" ]

  then

      echo "FOUND IN FILE ${array[1]}"

      STOREID="${array[1]}";

  fi

done <<< "$(cat $serverQuestion)"


if [ $STOREID = "NONE" ]; then

        echo "Invalid store setup";

        exit 1;

fi;


mongoimport --port=27018 --db UserManager --collection "col_${STOREID}" --file pms_user_data.json

echo -e " Done!"
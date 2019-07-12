#!/bin/bash

NOGOOD="Please enter ip adress and name and destination for backup example: ./makebackup.sh 10.200.1.50 /home/boggi/backups"

if [ -z "$1" ]
then
      echo $NOGOOD
      exit 1
fi
if [ -z "$2" ]
then
      echo $NOGOOD
      exit 1
fi

rm $2/$1.tar.gz
scp backup.sh pi@$1:/home/pi/
ssh pi@$1 '/home/pi/backup.sh'
scp pi@$1:/backup.tar.gz $2/$1.tar.gz
ssh pi@$1 'sudo rm /backup.tar.gz'

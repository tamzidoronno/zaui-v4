#!/bin/bash

NOGOOD="Please enter ip adress and name and destination for backup example: ./makebackup.sh 10.200.1.50 whpos /home/boggi/backups"

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
if [ -z "$3" ]
then
      echo $NOGOOD
      exit 1
fi


scp backup.sh pi@$1:/home/pi/
ssh pi@$1 '/home/pi/backup.sh'
scp pi@$1:/backup.tar.gz $3/$1_$2.tar.gz
ssh pi@$1 'sudo rm /backup.tar.gz'

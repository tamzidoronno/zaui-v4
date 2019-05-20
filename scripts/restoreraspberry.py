#!/bin/bash
if [ -z "$1" ]
then
      echo "Please enter source and destination example: /home/boggi/backups/10.200.1.50.tar.gz /media/boggi/rootfs"
      exit 1
fi
if [ -z "$2" ]
then
      echo "Please enter source and destination example: /home/boggi/backups/10.200.1.50.tar.gz /media/boggi/rootfs"
      exit 1
fi

rm -rf $2/etc
rm -rf $2/root
rm -rf $2/home
rm -rf $2/opt/z-way-server
rm -rf $2/storage
rm -rf $2/var/spool/cron/crontabs

cd $2
tar xzvf $1
sync





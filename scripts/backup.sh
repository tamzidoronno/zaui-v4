#!/bin/bash
cd /
echo "Making backup"
sudo tar zcvf backup.tar.gz  /etc /root /home /opt/z-way-server /storage /var/spool/cron/crontabs /sbin/forwardPort.sh
echo "Done making backup"



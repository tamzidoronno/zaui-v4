#!/bin/bash
cd /
id=`cat /storage/unitid.txt`
FILE=/storage/setupdone.txt
if test -f "$FILE"; then
    echo "System has been restored"
    exit
fi

canstart=$(wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&canStartDownload=true" -q -O -)
while [ "$canstart" -ne "1" ]
do 
    echo "Waiting for restore startup"
    sleep 5s
    canstart=$(wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&canStartDownload=true" -q -O -)
done

`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=$id is connected."`
`wget -O backup.tar.gz "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&downloadfile=true"`
`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=Extracting backup..."`
tar xzvf backup.tar.gz
`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=Done extracting, now restoring"`
rm -rf /etc
rm -rf /root
rm -rf /home
rm -rf /opt/z-way-server
rm -rf /storage
rm -rf /var/spool/cron/crontabs
tar backup.tar.gz
sync
rm -rf systembackupstatus.php*
`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=Done restoring, now restarting..."`
echo "done" > /storage/setupdone.txt
reboot

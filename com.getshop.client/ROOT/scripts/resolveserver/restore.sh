#!/bin/bash
echo "staring" > /storage/restoring.txt
cd /
id=`cat /storage/unitid.txt`
FILE=/storage/setupdone.txt
if test -f "$FILE"; then
    echo "System has been restored"
    echo "System already restored" > /storage/restoring.txt
    exit
fi

canstart=$(wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&canStartDownload=true" -q -O -)
while [ "$canstart" -ne "1" ]
do 
    echo "Waiting for restore startup" > /storage/restoring.txt
    sleep 5s
    canstart=$(wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&canStartDownload=true" -q -O -)
done

echo "Connected and ready to restore" > /storage/restoring.txt
echo "Connected" > /storage/restoring.txt
`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=$id is connected."`
`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=Downloading backup..."`
`wget -O /backup.tar.gz "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&downloadfile=true"`
`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=Done downloading backup, restoring, this might take a couple of minutes, please wait..."`
rm -rf /etc
rm -rf /root
rm -rf /home
rm -rf /opt/z-way-server
rm -rf /storage
rm -rf /var/spool/cron/crontabs
cd /
tar xzvf /backup.tar.gz
sync
echo "domain home
nameserver 8.8.8.8" > /etc/resolv.conf
rm -rf systembackupstatus.php*
echo "done" > /storage/setupdone.txt
`wget "https://system.getshop.com/scripts/systembackupstatus.php?id=$id&recoverymessage=Restore complete, please reboot your server by unplugging its power and connect it back again"`

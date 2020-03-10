#!/bin/bash
#/etc/init.d/z-way-server stop
#/etc/init.d/zbw_connect stop
#/root/getshop_door_lock/stop.sh

rm -rf etc/init.d/z-way-server
rm -rf etc/init.d/zbw_connect
rm -rf etc/rc*.d/K01z-way-server
rm -rf etc/rc*.d/K01zbw_connect
rm -rf root/getshop_door_lock

cp -rf /source/getshop/3.0.0/arduino/RasbperryLoraGatewaySocket/getshopLoraSocket storage/
cp -rf /source/getshop/3.0.0/arduino/RasbperryLoraGatewaySocket/startLoraSocket sbin/
cp -rf /source/getshop/3.0.0/arduino/RasbperryLoraGatewaySocket/loraSocketWatchDog.sh sbin/
cp -prf /source/getshop/3.0.0/arduino/RasbperryLoraGatewaySocket/root var/spool/cron/crontabs/
chown root var/spool/cron/crontabs/root
sed "4s/$/ $(uuidgen)/" /source/getshop/3.0.0/arduino/RasbperryLoraGatewaySocket/startLoraSocket > sbin/startLoraSocket
chmod +x sbin/startLoraSocket

CONTENT=`cat etc/rc.local |grep -v getshop_door_lock |grep -v iptables |sed 's/root\/startGetShop.sh/sbin\/startLoraSocket\&/g'`;
echo "$CONTENT" > etc/rc.local

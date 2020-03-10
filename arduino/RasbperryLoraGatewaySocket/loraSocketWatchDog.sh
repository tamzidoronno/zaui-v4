#!/bin/bash
COUNT=`ps aux|grep getshopLora |grep storage |wc -l`

if [ "$COUNT" == "3" ]; then
    echo "All good."
else
    echo "Restarting"
    kill `ps aux |grep getshopLora |grep storage | awk {'print $2}'`
fi

#!/bin/bash
curl -L "https://trysilhotell.getshop.com/scripts/fetchCodes.php?engine=default&username=entrancedoor@getshop.com&password=g44gfd22"  > /root/getshop_door_lock/validcodes_tmp.txt
foo="`cat /root/getshop_door_lock/validcodes_tmp.txt`"
echo $foo
extracode="783625,"
if [[ $foo == ,* ]] && [[ $foo == *, ]]
then
	echo $foo$extracode > /root/getshop_door_lock/validcodes.txt
	#echo "783625," >> /root/getshop_door_lock/validcodes.txt
fi
rm -rf /root/getshop_door_lock/validcodes_tmp.txt

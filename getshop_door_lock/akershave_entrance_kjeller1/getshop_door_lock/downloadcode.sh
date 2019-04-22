#!/bin/bash
curl -L "https://20363.getshop.com/scripts/fetchCodes.php?engine=default&username=entrancedoor@getshop.com&password=vvueeh0k9"  > /root/getshop_door_lock/validcodes_tmp.txt
foo="`cat /root/getshop_door_lock/validcodes_tmp.txt`"
echo $foo
if [[ $foo == ,* ]] && [[ $foo == *, ]]
then
	echo $foo > /root/getshop_door_lock/validcodes.txt
fi
rm -rf /root/getshop_door_lock/validcodes_tmp.txt

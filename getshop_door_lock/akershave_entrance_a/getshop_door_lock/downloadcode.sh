#!/bin/bash
curl -L "https://20363.getshop.com/scripts/fetchCodes.php?engine=default&username=entrancedoor@getshop.com&password=vvueeh0k9"  > /storage/validcodes_tmp.txt
foo="`cat /storage/validcodes_tmp.txt`"
echo $foo
if [[ $foo == ,* ]] && [[ $foo == *, ]]
then
	echo $foo > /storage/validcodes.txt
fi
rm -rf /storage/validcodes_tmp.txt

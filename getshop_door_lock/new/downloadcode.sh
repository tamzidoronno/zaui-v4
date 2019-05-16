#!/bin/bash
token=`ruby -rjson -e 'j = JSON.parse(File.read("/storage/getshop/getshop.db")); puts j["token"]'`
address=`ruby -rjson -e 'j = JSON.parse(File.read("/storage/getshop/getshop.db")); puts j["remotehostname"]'`
downloaddress="http://$address/scripts/fetchCodesApac.php?token=$token"
echo $downloaddress;
curl -L "$downloaddress" > /storage/validcodes_tmp.txt
foo="`cat /storage/validcodes_tmp.txt`"
echo $foo
if [[ $foo == \{* ]] && [[ $foo == *\} ]]
then
	echo $foo > /storage/validcodes.txt
fi
rm -rf /storage/validcodes_tmp.txt

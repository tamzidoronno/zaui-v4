#!/bin/bash
if [ -z ${CLASSNAME+x} ];
   then echo "Enter classname:"; read CLASSNAME;
fi;

if [ -z ${CODE+x} ];
   then CODE=$(<templates/dbscriptTemplate.txt);
fi;

#CLASSNAME="BBSCRIPT";
JAVAFILE="../core/src/main/java/com/thundashop/core/databasemanager/updatescripts/$CLASSNAME.java"
SCRIPT_UUID=`uuidgen`
DATE=`date +%d/%m-%Y`

CODE=$(echo "$CODE" | sed "s/{{CLASSNAME}}/$CLASSNAME/g");
CODE=$(echo "$CODE" | sed "s/{{SCRIPT_UUID}}/$SCRIPT_UUID/g");
CODE=$(echo "$CODE" | sed "s|{{DATE}}|$DATE|g");

echo "$CODE" > $JAVAFILE

echo "Done, javafile is located at: $JAVAFILE";

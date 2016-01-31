#!/bin/bash
echo "Name of app"
read name
echo "Select one of the following types:"
echo "1. MarketingApplication"
echo "2. SystemApplication"
echo "3. WebshopApplication"
echo "4. PaymentApplication"
echo "5. ReportingApplication"
echo "6. ShipmentApplication"
echo "7. ThemeApplication"
read appType

UUID2=$(cat /proc/sys/kernel/random/uuid)
UUID=${UUID2//[-]/_}
APPTNAME=MarketingApplication
JAVATYPE=Marketing
if [ $appType = "2" ]; then 
    APPTNAME=SystemApplication
    JAVATYPE=System
fi
if [ $appType = "3" ]; then 
    APPTNAME=WebshopApplication
    JAVATYPE=Webshop
fi
if [ $appType = "4" ]; then 
    APPTNAME=PaymentApplication
    JAVATYPE=Payment
fi
if [ $appType = "5" ]; then 
    APPTNAME=ReportingApplication
    JAVATYPE=Reporting
fi
if [ $appType = "6" ]; then 
    APPTNAME=ShipmentApplication
    JAVATYPE=Shipment
fi
if [ $appType = "7" ]; then 
    APPTNAME=ThemeApplication
    JAVATYPE=Theme
fi

echo "Module:"
echo "1. Other applications"
echo "2. E-commerce"
echo "3. Marketing"
echo "4. Content Management"
echo "5. Reports"
read moduleNumber

module=other
if [ $moduleNumber = "2" ]; then 
    module=WebShop
fi
if [ $moduleNumber = "3" ]; then 
    module=Marketing
fi
if [ $moduleNumber = "4" ]; then 
    module=cms
fi
if [ $moduleNumber = "5" ]; then 
    module=reporting
fi

echo "Building files and folders."
mkdir ../com.getshop.client/apps/$name
mkdir ../com.getshop.client/apps/$name/javascript
touch ../com.getshop.client/apps/$name/javascript/$name.js
mkdir ../com.getshop.client/apps/$name/skin
touch ../com.getshop.client/apps/$name/skin/$name.css
mkdir ../com.getshop.client/apps/$name/template
echo "<?php" > ../com.getshop.client/apps/$name/$name.php
echo "namespace ns_$UUID;" >> ../com.getshop.client/apps/$name/$name.php
echo "" >> ../com.getshop.client/apps/$name/$name.php
echo "class $name extends \\$APPTNAME implements \Application {" >> ../com.getshop.client/apps/$name/$name.php

echo "    public function getDescription() {" >> ../com.getshop.client/apps/$name/$name.php
echo "        " >> ../com.getshop.client/apps/$name/$name.php
echo "    }" >> ../com.getshop.client/apps/$name/$name.php
echo "" >>../com.getshop.client/apps/$name/$name.php
echo "    public function getName() {" >> ../com.getshop.client/apps/$name/$name.php
echo "        return \"$name\";" >> ../com.getshop.client/apps/$name/$name.php
echo "    }" >> ../com.getshop.client/apps/$name/$name.php
echo "" >> ../com.getshop.client/apps/$name/$name.php
echo "    public function render() {" >> ../com.getshop.client/apps/$name/$name.php
echo "        " >> ../com.getshop.client/apps/$name/$name.php
echo "    }" >> ../com.getshop.client/apps/$name/$name.php

echo "}" >> ../com.getshop.client/apps/$name/$name.php
echo "?>" >> ../com.getshop.client/apps/$name/$name.php


TEMPLATEFILE="addApplication.txt";
CODE=$(<templates/$TEMPLATEFILE);
CLASSNAME=$name;

CODE=$(echo "$CODE" | sed "s|{{UUID}}|$UUID|g");
CODE=$(echo "$CODE" | sed "s|{{MODULE}}|$module|g");
CODE=$(echo "$CODE" | sed "s|{{JAVATYPE}}|$JAVATYPE|g");

. ./createDbScript.sh

#echo "######################## AddApplicationToDatabase ############"
#echo "Application $name = createSettings(\"$name\","
#echo "\"$UUID\","
#echo "allowed2,"
#echo "\" \","
#echo "Application.Type.$JAVATYPE, true);"
#echo "$name.isPublic = true;"
#echo "$name.isFrontend = true;"
#echo "$name.moduleId = \"$module\";"
#echo "$name.defaultActivate = false;"
#echo "apps.add($name);"
#echo "######################## AddApplicationToDatabase ############"

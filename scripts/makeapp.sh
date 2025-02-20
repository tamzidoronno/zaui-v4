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
echo "6. EventBooking"
echo "7. E-commerce template pool"
echo "";
echo "In GetShop Modules";
echo "8. Property Management";
echo "9. Apac";
echo "10. Salespoint";
echo "11. Ecommerce";
echo "12. GetShop Settings";
echo "13. Accounting";
echo "14. Ticket";
echo "15. CRM";
echo "16. SRS";
echo "17. INVOICING";
echo "18. Analythics";
echo "19. GetShop Support";
echo "20. Comfort";

echo "20. Settings";

read moduleNumber

module=other
defaultActivated=false
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
if [ $moduleNumber = "6" ]; then 
    module=eventbooking
fi
if [ $moduleNumber = "7" ]; then 
    module=ecommercetemplate
fi
if [ $moduleNumber = "8" ]; then 
    module=pms
    defaultActivated=true
fi
if [ $moduleNumber = "9" ]; then 
    module=apac
    defaultActivated=true
fi
if [ $moduleNumber = "10" ]; then 
    module=salespoint
    defaultActivated=true
fi
if [ $moduleNumber = "11" ]; then 
    module=ecommerce
    defaultActivated=true
fi
if [ $moduleNumber = "12" ]; then 
    module=settings
    defaultActivated=true
fi
if [ $moduleNumber = "13" ]; then 
    module=account
    defaultActivated=true
fi
if [ $moduleNumber = "14" ]; then 
    module=ticket
    defaultActivated=true
fi
if [ $moduleNumber = "15" ]; then 
    module=crm
    defaultActivated=true
fi
if [ $moduleNumber = "16" ]; then 
    module=srs
    defaultActivated=true
fi
if [ $moduleNumber = "17" ]; then 
    module=invoice
    defaultActivated=true
fi
if [ $moduleNumber = "18" ]; then 
    module=analytics
    defaultActivated=true
fi
if [ $moduleNumber = "19" ]; then 
    module=getshopsupport
    defaultActivated=true
fi
if [ $moduleNumber = "20" ]; then 
    module=getshopsupport
    defaultActivated=true
fi
if [ $moduleNumber = "21" ]; then 
    module=comfort
    defaultActivated=true
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
CODE=$(echo "$CODE" | sed "s|{{UUID2}}|$UUID2|g");
CODE=$(echo "$CODE" | sed "s|{{MODULE}}|$module|g");
CODE=$(echo "$CODE" | sed "s|{{JAVATYPE}}|$JAVATYPE|g");
CODE=$(echo "$CODE" | sed "s|{{defaultActivated}}|$defaultActivated|g");

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

#!/bin/bash
cd com.getshop.core
#ant -f /test/thundashop/com.getshop.core/build.xml clean jar
java -jar dist/com.thundashop.core.jar 20000 &
cd ..
cd com.getshop.client/integrationtest
php runner.php alttest

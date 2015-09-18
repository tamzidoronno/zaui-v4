#!/bin/bash
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -Xmx1024m -XX:MaxPermSize=1024m -cp *:libs/* com.thundashop.core.databasemanager.AddApplicationsToDatabase;
java -Xmx1024m -XX:MaxPermSize=1024m -cp *:libs/* com.thundashop.core.start.Runner &> log.txt&

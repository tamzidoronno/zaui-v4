#!/bin/bash
java -Djsse.enableSNIExtension=false -Xmx6196m -XX:MaxPermSize=6196m -cp *:libs/* com.thundashop.core.start.UpdaterRunner
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -Djsse.enableSNIExtension=false -Xmx6196m -XX:MaxPermSize=6196m -cp *:libs/* com.thundashop.core.start.Runner >> log.txt  2>&1&

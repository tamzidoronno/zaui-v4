#!/bin/bash
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -Djsse.enableSNIExtension=false -Xmx4096m -XX:MaxPermSize=4096m -cp *:libs/* com.thundashop.core.start.Runner >> log.txt  2>&1&

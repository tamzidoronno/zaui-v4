#!/bin/bash
java -Djsse.enableSNIExtension=false -Xmx16196m -XX:MaxPermSize=6196m -cp *:libs/* com.thundashop.core.start.UpdaterRunner
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -XX:-OmitStackTraceInFastThrow -Xmx132196m -cp *:libs/* com.thundashop.core.start.Runner >> log.txt  2>&1&

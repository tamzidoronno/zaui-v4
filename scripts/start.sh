#!/bin/bash
java -Djsse.enableSNIExtension=false -Xmx16196m -XX:MaxPermSize=6196m -cp *:libs/* com.thundashop.core.start.UpdaterRunner
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=10.0.4.33 -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.rmi.port=9998 -XX:-OmitStackTraceInFastThrow -Xmx132196m -XX:MaxPermSize=142196m -cp *:libs/* com.thundashop.core.start.Runner >> log.txt  2>&1&

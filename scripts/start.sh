#!/bin/bash
java -Djsse.enableSNIExtension=false -Xmx16196m -XX:MaxPermSize=6196m -cp *:libs/* com.thundashop.core.start.UpdaterRunner

if [ -f ~/PmsRunner.pid ]; then
    echo "Killing previous Pms-Runner process if exists.."
    cat ~/PmsRunner.pid | xargs kill
fi
echo "Starting Pms-Runner.."
java -XX:-OmitStackTraceInFastThrow -Xmx132196m -cp *:libs/* com.thundashop.core.start.Runner >> log.txt  2>&1&
echo $! > ~/PmsRunner.pid
echo "Done, process $! started."

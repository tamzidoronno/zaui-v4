FILE=backend_4.1.0.tar.gz
if test -f "$FILE"; then
    echo "Found valid backend release file at $FILE"
    echo "Stopping java";
    kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'` > /dev/null 2>&1

    echo "Cleaning up old java files";
    rm -rf dist/*.jar
    rm -rf dist/libs

    tar xzvf backend_4.1.0.tar.gz > /dev/null;
    chmod +x dist/start.sh;
    cd dist;
    ./start.sh
    cd ..;
    rm -rf backend_4.1.0.tar.gz;
else
    echo "Backend release file at $FILE does not exist. No action taken."
fi

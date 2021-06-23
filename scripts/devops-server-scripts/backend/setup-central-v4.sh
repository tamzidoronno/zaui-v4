echo "Stopping java";
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'` > /dev/null 2>&1

echo "Cleaning up old java files";
rm -rf dist/core-4.1.0.jar
rm -rf dist/libs
rm -rf dist/messages-4.1.0.jar

tar xzvf backend_central_4.1.0.tar.gz > /dev/null;
chmod +x dist/start.sh;
cd dist;
./start.sh
cd ..;
rm -rf backend_central_4.1.0.tar.gz;
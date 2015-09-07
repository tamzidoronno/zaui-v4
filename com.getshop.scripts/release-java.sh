transfer file
cat << EOF > batchfile
cd dist
lcd $HOME/netbeans/1.0.0
put "GetShop Core/dist/GetShop_Core.jar" com.thundashop.core.jar
cd lib
put "GetShop Messages/dist/GetShop_Messages.jar" com.thundashop.messages.jar
EOF

cat << EOF > batchfile2
lcd $HOME/netbeans/1.0.0
put apitodb.json
EOF

cat << EOF > batchfile3
lcd /source/getshop/1.0.0/com.getshop.client/events
cd FrontEnd/events
put API2.php
EOF

echo -e "Uploading thundashop jar files"
sftp -b batchfile naxa@backendbetapromeister.getshop.com &> /dev/null
rm -rf batchfile

echo -e "uploading apitodb.json file"
sftp -b batchfile2 naxa@backendbetapromeister.getshop.com &> /dev/null
rm -rf batchfile2

echo -e "uploading API.php file"
sftp -b batchfile3 naxa@www.getshop.com &> /dev/null
rm -rf batchfile3

echo -e "Restarting java!";
ssh -T naxa@backendbetapromeister.getshop.com << EOF &> /dev/null;

cd dist; 
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -Xmx1024m -XX:MaxPermSize=1024m -cp com.thundashop.core.jar:lib/* com.thundashop.core.apigenerator.ImportApiCallsToApplications;
java -Xmx1024m -XX:MaxPermSize=1024m -cp com.thundashop.core.jar:lib/* com.thundashop.core.databasemanager.AddApplicationsToDatabase;
./start.sh
EOF
echo -e "Done!";


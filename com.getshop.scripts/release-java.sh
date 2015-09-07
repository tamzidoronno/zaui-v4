#transfer file
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

echo -e "Uploading thundashop jar files"
sftp -b batchfile naxa@backend.getshop.com #&> /dev/null
rm -rf batchfile

echo -e "Uploading api file"
sftp -b batchfile2 naxa@backend.getshop.com #&> /dev/null
rm -rf batchfile2

echo -e "Restarting java!";
ssh -T naxa@backend.getshop.com << EOF &> /dev/null;

cd dist; 
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -Xmx1024m -XX:MaxPermSize=1024m -cp com.thundashop.core.jar:lib/* com.thundashop.core.apigenerator.ImportApiCallsToApplications;
java -Xmx1024m -XX:MaxPermSize=1024m -cp com.thundashop.core.jar:lib/* com.thundashop.core.databasemanager.AddApplicationsToDatabase;
./start.sh
EOF
echo -e "Done!";


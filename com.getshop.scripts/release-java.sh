transfer file
cat << EOF > batchfile
cd dist
lcd ../
put com.getshop.core/dist/com.thundashop.core.jar
cd lib
put com.getshop.messages/dist/com.thundashop.messages.jar
EOF

cat << EOF > batchfile2
lcd ../
put apitodb.json
EOF

echo -e "Uploading thundashop jar files"
sftp -b batchfile naxa@backend20.getshop.com &> /dev/null
rm -rf batchfile

echo -e "Uploading api file"
sftp -b batchfile2 naxa@backend20.getshop.com &> /dev/null
rm -rf batchfile2

echo -e "Restarting java!";
ssh -T naxa@backend20.getshop.com << EOF &> /dev/null;

cd dist; 
kill -9 `ps aux |grep thunda |grep -v "auto" |awk '{print $2}'`
java -Xmx1024m -XX:MaxPermSize=1024m -cp com.thundashop.core.jar:lib/* com.thundashop.core.apigenerator.ImportApiCallsToApplications;
./start.sh
EOF
echo -e "Done!";

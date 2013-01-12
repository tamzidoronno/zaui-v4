#transfer file
cat << EOF > batchfile
cd dist/lib
put com.getshop.core/dist/com.thundashop.core.jar
put com.getshop.messages/dist/com.thundashop.messages.jar
EOF

echo -e "Uploading thundashop jar files"
sftp -b batchfile naxa@backend.getshop.com &> /dev/null
rm -rf batchfile

echo -e "Restarting java!";
ssh -T naxa@backend.getshop.com << EOF &> /dev/null
cd dist; 
./start.sh
EOF
echo -e "Done!";


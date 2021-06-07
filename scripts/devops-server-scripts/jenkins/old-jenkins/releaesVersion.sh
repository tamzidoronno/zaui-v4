#JAVA CODE
echo "Release java code";
cat << EOF > batcbackend
put /home/naxa/builds/3530/archive/artifacts/builds/3.0.0/backend_3.0.0.tar.gz
EOF
sftp -oPort=4223 -b batcbackend naxa@$1 > /dev/null;

ssh -oPort=4223 naxa@$1 'scripts/setup.sh';

echo "";
echo "Releasing php code";

#PHP CODE
cat << EOF > batchfile
put "/home/naxa/builds/3530/archive/artifacts/builds/3.0.0/php_3.0.0.tar.gz"
EOF
echo "Uploading file php file";
sftp -oPort=4222 -b batchfile naxa@$1 > /dev/null
ssh -oPort=4222 naxa@$1 'scripts/setup.sh'

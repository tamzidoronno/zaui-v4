#JAVA CODE
echo "Release java code";
cat << EOF > batcbackend
put /home/naxa/builds_central/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/backend_3.0.0.tar.gz
EOF
sftp -b batcbackend naxa@$1 > /dev/null;

ssh naxa@$1 'scripts/setup.sh';

echo "";
echo "Releasing php code";

#PHP CODE
cat << EOF > batchfile
put "/home/naxa/builds_central/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/php_3.0.0.tar.gz"
EOF
echo "Uploading file php file";
sftp -b batchfile naxa@$2 > /dev/null
ssh naxa@$2 'scripts/setup.sh'

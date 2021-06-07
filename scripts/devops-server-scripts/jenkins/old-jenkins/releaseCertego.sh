#JAVA CODE
echo "Release Certego";
cat << EOF > batcbackend
put /var/lib/jenkins/jobs/Certego/builds/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/backend_3.0.0.tar.gz
EOF
sftp -b batcbackend naxa@10.0.3.70 > /dev/null;
ssh naxa@10.0.3.70 'scripts/setup.sh';

echo "";
echo "Releasing php code";

#PHP CODE
cat << EOF > batchfile
put "/var/lib/jenkins/jobs/Certego/builds/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/php_3.0.0.tar.gz"
EOF
echo "Uploading file php file";
sftp -b batchfile naxa@10.0.3.71 > /dev/null
ssh naxa@10.0.3.71 'scripts/setup.sh'

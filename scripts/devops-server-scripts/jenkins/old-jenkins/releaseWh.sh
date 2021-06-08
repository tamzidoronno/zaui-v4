#JAVA CODE
echo "Release java code";
cat << EOF > batcbackend
put /var/lib/jenkins/jobs/WilhelmsenHouse/builds/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/backend_3.0.0.tar.gz
EOF
sftp -b batcbackend web@193.90.39.13 > /dev/null;
ssh web@193.90.39.13 'scripts/setupJava.sh';

echo "";
echo "Releasing php code";

#PHP CODE
cat << EOF > batchfile
put "/var/lib/jenkins/jobs/WilhelmsenHouse/builds/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/php_3.0.0.tar.gz"
EOF
echo "Uploading file php file";
sftp -b batchfile web@193.90.39.13  > /dev/null
ssh web@193.90.39.13 'scripts/setupPhp.sh'

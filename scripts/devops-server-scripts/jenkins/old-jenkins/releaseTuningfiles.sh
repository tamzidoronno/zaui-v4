#JAVA CODE
echo "Release java code";
cat << EOF > batcbackend
put /home/naxa/builds/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/backend_3.0.0.tar.gz
EOF
sftp -b batcbackend vds@backend.tuningfiles.com > /dev/null;
ssh vds@backend.tuningfiles.com 'scripts/setup.sh';

echo "";
echo "Releasing php code";

#PHP CODE
cat << EOF > batchfile
put "/home/naxa/builds/lastSuccessfulBuild/archive/artifacts/builds/3.0.0/php_3.0.0.tar.gz"
EOF
echo "Uploading file php file";
sftp -b batchfile www@frontend.tuningfiles.com > /dev/null
ssh www@frontend.tuningfiles.com 'scripts/setup.sh'

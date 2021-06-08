#!/bin/bash

#JAVA CODE
echo "Release java code to $1";
scp /home/naxa/builds/lastSuccessfulBuild/archive/artifacts/builds/4.1.0/backend_4.1.0.tar.gz naxa@$1:/home/naxa/
ssh naxa@$1 "scripts/setup-v4.sh"
#PHP Code
echo "-------------------------";
echo "Releasing php code to $2";
scp /home/naxa/builds/lastSuccessfulBuild/archive/artifacts/builds/4.1.0/php_4.1.0.tar.gz naxa@$2:/home/naxa/
ssh naxa@$2 "scripts/setup-v4.sh"
echo "DONE ################################ "

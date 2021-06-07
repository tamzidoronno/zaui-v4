#!/bin/bash

#JAVA CODE
echo "Release java code to $1";
scp /var/lib/jenkins/jobs/Central/lastSuccessful/archive/artifacts/builds/4.1.0/backend_central_4.1.0.tar.gz naxa@$1:/home/naxa/
ssh naxa@$1 "scripts/setup-central-v4.sh"

#PHP Code
echo "-------------------------";
echo "Releasing php code to $2";
scp /var/lib/jenkins/jobs/Central/lastSuccessful/archive/artifacts/builds/4.1.0/php_central_4.1.0.tar.gz naxa@$2:/home/naxa/
ssh naxa@$2 "scripts/setup-central-v4.sh"
echo "DONE ################################ "

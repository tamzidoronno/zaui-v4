#!/bin/bash

backend="10.0.9.33"
frontend="10.0.9.32"
#JAVA CODE
echo "Release dev branch java code to $backend";
scp /home/naxa/dev-builds/lastSuccessful/archive/artifacts/builds/4.1.0/backend_4.1.0.tar.gz naxa@$backend:/home/naxa/
ssh naxa@$backend "scripts/setup-v4.sh"
#PHP Code
echo "-------------------------";
echo "Releasing dev branch php code to $frontend";
scp /home/naxa/dev-builds/lastSuccessful/archive/artifacts/builds/4.1.0/php_4.1.0.tar.gz naxa@$frontend:/home/naxa/
ssh naxa@$frontend "scripts/setup-v4.sh"
echo "DONE ############## DEV RELEASE ################## "

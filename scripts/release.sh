echo "Retreiving build information... please wait...";
echo "";
ssh naxa@jenkins.getshop.com '/home/naxa/scripts/showLastSuccessfulBuild.sh'
echo "";
echo "Do you want to release this? (1 = yes / any = no)"
read releaseAnswer;

if [ $releaseAnswer = "1" ]; then
	echo "What server do you want to release to?"
	echo "3 = Server 3 ( First server ever created )";
	echo "4 = Server 4 ( Created: 8 june 2017 )";
	echo "5 = Server Kronen ( Created: 19 aug 2018 )";
	read serverQuestion;

	if [ $serverQuestion = "3" ]; then
		echo "Releasing";
		ssh naxa@jenkins.getshop.com '/home/naxa/scripts/release.sh'
	elif [ $serverQuestion = "4" ]; then
		ssh naxa@jenkins.getshop.com '/home/naxa/scripts/releaseSoftware.sh 138.201.203.177'
	elif [ $serverQuestion = "5" ]; then
		ssh naxa@jenkins.getshop.com '/home/naxa/scripts/releaseSoftware.sh 88.99.1.121'
	else 
		echo "Invalid server setup";
	fi;
else
	echo "ok, maybe next time";
fi;

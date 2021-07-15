echo "Retreiving build information... please wait...";
echo "";
ssh naxa@jenkins2021.getshop.com '/home/naxa/scripts/showLastSuccessfulBuild.sh'
echo "";
echo "Do you want to release this? (1 = yes / any = no)"
read releaseAnswer;

if [ $releaseAnswer = "1" ]; then
	echo "What server do you want to release to?"
	echo "3 = Server 3 ( First server ever created )";
	echo "4 = Server 4 ( Created: 8 june 2017 )";
	echo "5 = Server Kronen ( Created: 19 aug 2018 )";
  echo "6 = Server cluster 6 ( Created: 19 juli 2019 )";
  echo "9 = Server cluster 9 ( Created: 26 nov 2020 )";
  echo "Q = QP Server (Created 2021)"
  read serverQuestion;

  if [ $serverQuestion = "3" ]; then
		echo "Cluster 3 is nt in active use for this!";
		#ssh naxa@jenkins.getshop.com '/home/naxa/scripts/release.sh'
	elif [ $serverQuestion = "4" ]; then
		ssh naxa@jenkins2021.getshop.com '/home/naxa/scripts/releaseSoftwareV4.sh 10.0.4.33 10.0.4.32'
	elif [ $serverQuestion = "5" ]; then
		ssh naxa@jenkins2021.getshop.com '/home/naxa/scripts/releaseSoftwareV4.sh 10.0.5.33 10.0.5.32'
  elif [ $serverQuestion = "6" ]; then
    ssh naxa@jenkins2021.getshop.com '/home/naxa/scripts/releaseSoftwareV4.sh 10.0.6.33 10.0.6.32'
  elif [ $serverQuestion = "9" ]; then
    ssh naxa@jenkins2021.getshop.com '/home/naxa/scripts/releaseSoftwareV4.sh 10.0.9.33 10.0.9.32'
  elif [ $serverQuestion = "Q" ]; then
    ssh naxa@jenkins2021.getshop.com '/home/naxa/scripts/releaseSoftwareQP.sh 10.0.9.99'
	else
		echo "Invalid server answer";
	fi;
else
	echo "ok, maybe next time";
fi;

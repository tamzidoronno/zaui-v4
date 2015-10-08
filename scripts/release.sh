echo "Retreiving build information... please wait...";
echo "";
ssh naxa@jenkins.getshop.com '/home/naxa/scripts/showLastSuccessfulBuild.sh'
echo "";
echo "Do you want to release this? (1 = yes / any = no)"
read releaseAnswer;

if [ $releaseAnswer = "1" ]; then
	echo "Releasing";
	ssh naxa@jenkins.getshop.com '/home/naxa/scripts/release.sh'
else
	echo "ok, maybe next time";
fi;

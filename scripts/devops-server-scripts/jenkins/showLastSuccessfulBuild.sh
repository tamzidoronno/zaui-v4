#Show files changed
echo "=========================================================================================";
echo "";
echo "Commit information:"
cat  /var/lib/jenkins/jobs/GetShop\ v4/builds/lastSuccessfulBuild/changelog.xml |head -4|sed 's/^/\ \ \ \ /'

echo "";
echo "Comment:"
cat  /var/lib/jenkins/jobs/GetShop\ v4/builds/lastSuccessfulBuild/changelog.xml  | grep -v :  |tail -n +4 |grep  -v '^[[:space:]]*$'

echo "";
echo "Files Changed:";
cat /var/lib/jenkins/jobs/GetShop\ v4/builds/lastSuccessfulBuild/changelog.xml |grep -v commit |grep -v parent |grep -v tree |awk '    {print $6}' |grep -v : |grep  -v '^[[:space:]]*$'|sed 's/^/\ \ \ \ /'
echo "";
echo "=========================================================================================";

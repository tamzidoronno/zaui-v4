grep console.log -R com.getshop.client/* |grep -v less |grep -v jquery |grep -v sencha |grep -v .svn
grep debugger -R com.getshop.client/* |grep -v less |grep -v jquery |grep -v sencha |grep -v .svn

rm -rf release;
cp -rf com.getshop.client/ release;
cd release;
cd ROOT;
svn info |grep Revision > revision.txt;
cd ../;
rm -rf `find . |grep .svn`
tar czvf release.tar.gz *  > /dev/null;
mv release.tar.gz ../;
cd ../;

#transfer file
cat << EOF > batchfile
put release.tar.gz
EOF

sftp -b batchfile naxa@www.getshop.com
rm -rf batchfile

# RESTART application
ssh naxa@www.getshop.com << EOF
cd www
tar xzvf ../release.tar.gz > /dev/null;
cd ../
rm -rf release.tar.gz
EOF

# cleanup
rm -rf release;
rm -rf release.tar.gz

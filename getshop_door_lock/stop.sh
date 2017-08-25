kill `ps aux |grep door1.py | awk {'print $2'}`
kill `ps aux |grep door2.py | awk {'print $2'}`
kill `ps aux |grep socket | awk {'print $2'}`
kill `ps aux |grep pingwebserver | awk {'print $2'}`

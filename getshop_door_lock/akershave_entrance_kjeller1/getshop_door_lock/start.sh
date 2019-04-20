cd /root/getshop_door_lock;
./socket&
sleep 1
python door1.py > door1.log&
python door2.py > door2.log&
#python door3.py > door2.log&
#python pingwebserver.py&

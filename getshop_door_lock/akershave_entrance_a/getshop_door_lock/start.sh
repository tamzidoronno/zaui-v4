cd /root/getshop_door_lock;
./socket&
sleep 1
python door1.py&
python door2.py&
python door3.py&
#screen -d -m python pingwebserver.py

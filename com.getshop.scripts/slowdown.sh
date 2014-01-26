sudo tc qdisc add dev lo root handle 1: htb default 12
sudo tc class add dev lo parent 1:1 classid 1:12 htb rate 260kbps ceil 1280kbps
sudo tc qdisc add dev lo parent 1:12 netem delay 0ms

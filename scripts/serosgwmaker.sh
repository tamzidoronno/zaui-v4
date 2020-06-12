#!/bin/bash
if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

echo "What server do you want to release to?"
echo "1 = 1001sls6.getshop.com - 192.168.101.44 - Akers Have - SmartHub A B Building";
echo "2 = 1001sls6.getshop.com - 192.168.101.54 - Akers Have - SmartHub C Building";
echo "3 = 1002sls6.getshop.com - 10.200.1.168 - Sokndal trening - Entrance Gateway";
echo "4 = 1004sls6.getshop.com - 10.200.1.198 - Preikestolen - Lora Gateway";
echo "5 = 1005sls6.getshop.com - 10.200.1.164 - Canyon - Lora Gateway";
echo "6 = 1006sls6.getshop.com - 10.200.1.204 - HS Eiendom - Lora Gateway";
echo "7 = 1007sls6.getshop.com - 10.200.1.206 - F2 Harstad - Lora Gateway";
echo "8 = 1008sls6.getshop.com - 10.200.1.208 - GKroen - Lora Gateway";
echo "9 = 1009sls6.getshop.com - 10.200.1.210 - BA10 Oslo - Lora Gateway BA10";
echo "10 = 1010sls6.getshop.com - 10.200.1.216 - Jarvsoberg syclepark - Lora Gateway";
echo "11 = 1011sls6.getshop.com - 10.200.1.222 - Fru Skjold Hotell Mosjøen - Lora 1";
echo "12 = 1012sls6.getshop.com - 10.200.1.170 - GetShop Gamleveien 7 - Lora 1";
echo "13 = 1013sls6.getshop.com - 10.200.1.186 - Lilland Bryggerihotell - Lora Gateway";
echo "14 = 1014sls6.getshop.com - 10.200.1.226 - Torgarhaugen - Lora Gateway";
echo "15 = 1015sls6.getshop.com - 10.200.1.228 - Trysil hotell - Lora Gateway";
echo "16 = 1016sls6.getshop.com - 10.200.1.230 - Lom camping - Lora Gateway";
echo "17 = 1017sls6.getshop.com - 10.200.1.236 - Ami Hotel - Lora Gateway";
echo "18 = 1000020seros.c6.hotelautomation.tech - 10.200.1.246 - Kronen Gaard - Lora Gateway";
echo "19 = 1000024seros.c6.hotelautomation.tech - 10.200.2.10 - Gamlaskola henningsvær - Lora Gateway";
read serverQuestion;

if [ $serverQuestion = "1" ]; then
	TOKEN="8d212c02-428c-47df-9a43-2849d4e4f4e8"
	SSID="Intern-Akers Have"
	WIFIPASS="Lakk7376hagE"
	VPN="akershave_c_entrance"
elif [ $serverQuestion = "2" ]; then
	TOKEN="5529f69e-f5f3-4668-aa4e-fe545ad68793"
	SSID="Intern-Akers Have"
	WIFIPASS="Lakk7376hagE"
	VPN="akershave-common-entrance"
elif [ $serverQuestion = "3" ]; then
	TOKEN="481c9a46-4f34-41c9-bf27-f77896c8062c"
	SSID="Drageland Eiendom AS"
	WIFIPASS="Kleivan18"
	VPN="getshop_gamleveien7"
elif [ $serverQuestion = "4" ]; then
	TOKEN="cd0f1acd-17b3-49ec-beca-811839f9a08b"
	SSID="PF-Admin"
	WIFIPASS="PreikestolenFjellstueAdmin"
	VPN="preikestolen_lora_1"
elif [ $serverQuestion = "5" ]; then
	TOKEN="1023fc90-d535-45b8-8a1a-128640b4c95f"
	SSID="door"
	WIFIPASS="Door@Enterprise!"
	VPN="park_hotell_alta_entrance1"
elif [ $serverQuestion = "6" ]; then
	TOKEN="80a3d854-4f57-48b8-bb74-299d49b0b68c"
	SSID="JobZoneHybelhus"
	WIFIPASS="J0bz0ne2019"
	VPN="hs_eiendom_lora1"
elif [ $serverQuestion = "7" ]; then
	TOKEN="302b3093-c964-4e2b-877f-6004e11f95e4"
	SSID="F2"
	WIFIPASS="smarthotel"
	VPN="f2_harstad_lora1"
elif [ $serverQuestion = "8" ]; then
	TOKEN="35a225b8-e7a0-4594-8e67-15c9f66a7875"
	SSID=""
	WIFIPASS=""
	VPN="gkroen_lora_gateway1"
elif [ $serverQuestion = "9" ]; then
	TOKEN="30a99326-318a-41e8-bbe4-69bf82fc1fa8"
	SSID="BA10-adm"
	WIFIPASS="ba10admin"
	VPN="ba10_lora1"
elif [ $serverQuestion = "10" ]; then
	TOKEN="0dbc50c3-1854-45f3-b58d-a489316bfee3"
	SSID=""
	WIFIPASS=""
	VPN="jarvso_lora1"
elif [ $serverQuestion = "11" ]; then
	TOKEN="9c4ed0bc-9d0e-48df-9ce4-990c6a22306c"
	SSID="FSHint"
	WIFIPASS="privat123"
	VPN="fruskjolds_hotell_lora1"
elif [ $serverQuestion = "12" ]; then
	TOKEN="7506899d-f5a2-44b8-8c6f-44b15c9eac12"
	SSID=""
	WIFIPASS=""
	VPN="sokndal_garagaen_treningssenter"
elif [ $serverQuestion = "13" ]; then
	TOKEN="499ac281-3fdf-4c30-99b6-c4a2bc2b96a3"
	SSID="Lilland_Hotell"
	WIFIPASS=""
	VPN="lilland_hotel_lora_hoved"
elif [ $serverQuestion = "14" ]; then
	TOKEN="de5f5ef1-ad64-46a0-a3b4-1024baf907a1"
	SSID=""
	WIFIPASS=""
	VPN="torgarhaugen_lora_1"
elif [ $serverQuestion = "15" ]; then
        TOKEN="e12cd2c4-6984-4917-a24e-8266d5213776"
        SSID=""
        WIFIPASS=""
        VPN="trysil-lora"
elif [ $serverQuestion = "16" ]; then
        TOKEN="9702f629-a304-4311-8691-4c04ef5c5392"
        SSID=""
        WIFIPASS=""
        VPN="lom-camping-lora"
elif [ $serverQuestion = "17" ]; then
        TOKEN="71681873-eaa8-4d59-be66-953f84bef088"
        SSID="AmiWifi"
        WIFIPASS="AmiTromso 0520"
        VPN="ami-hotel-lora1"
elif [ $serverQuestion = "18" ]; then
        TOKEN="fed9fbab-58d0-4075-8d21-0ce1dcfe93dc"
        SSID=""
        WIFIPASS=""
        VPN="kronen_gaard_seros"
elif [ $serverQuestion = "19" ]; then
        TOKEN="18e1d7b8-056e-4754-a118-f0227acddd59"
        SSID="Gammelskola"
        WIFIPASS="gammelskola2018"
        VPN="gammelskola_apartments_lora"
else 
	echo "Invalid selection";
fi;

echo "";
VPNFILE="/source/getshop/serosvpn/$VPN.tar.gz"
if [ -f $VPNFILE ]
then
    echo "Using vpn  : $VPNFILE";
else
    echo "Did not find vpn $VPNFILE";
    exit
fi

BASEFOLDER="";
for d in /media/*/* ; do
   if test -f "$d/seros.txt"; then
       BASEFOLDER=$d
       echo "Base folder: $d";
   fi
done

if [ -z "$BASEFOLDER" ]
then
   echo "Could not find base folder, please check that you have a Seros USB Thumb drive connected";
   exit
fi;

cd $BASEFOLDER;
echo "#!/bin/bash" > sbin/startLoraSocket
echo "while :" >> sbin/startLoraSocket
echo "do" >> sbin/startLoraSocket
echo "	/storage/getshopLoraSocket 10.0.6.63 $TOKEN" >> sbin/startLoraSocket
echo "	sleep 1" >> sbin/startLoraSocket
echo "done" >> sbin/startLoraSocket

#VPN
rm -rf etc/openvpn/*
tar xC etc/openvpn -f $VPNFILE
if [ -d "etc/openvpn/$VPN" ]
then
    mv etc/openvpn/$VPN/* etc/openvpn/
    rm -rf etc/openvpn/$VPN
fi;

#WIFI
echo "country=GB" > etc/wpa_supplicant/wpa_supplicant.conf
echo "ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev" >> etc/wpa_supplicant/wpa_supplicant.conf
echo "update_config=1" >> etc/wpa_supplicant/wpa_supplicant.conf
echo "" >> etc/wpa_supplicant/wpa_supplicant.conf
echo "network={" >> etc/wpa_supplicant/wpa_supplicant.conf
echo "	ssid=\"GetShop\"" >> etc/wpa_supplicant/wpa_supplicant.conf
echo "	psk=\"aabbccddeeffgg\"" >> etc/wpa_supplicant/wpa_supplicant.conf
echo "	key_mgmt=WPA-PSK" >> etc/wpa_supplicant/wpa_supplicant.conf
echo "}" >> etc/wpa_supplicant/wpa_supplicant.conf


if [ -z "$SSID" ]
then
	echo "Skipping WIFI, not added";
else
   echo "Setting up wifi";
   
   echo "" >> etc/wpa_supplicant/wpa_supplicant.conf
   if [ -z "$WIFIPASS" ]
   then
      echo "network={" >> etc/wpa_supplicant/wpa_supplicant.conf
      echo "	ssid=\"$SSID\"" >> etc/wpa_supplicant/wpa_supplicant.conf
      echo "	key_mgmt=NONE" >> etc/wpa_supplicant/wpa_supplicant.conf
      echo "}" >> etc/wpa_supplicant/wpa_supplicant.conf
   else
      echo "network={" >> etc/wpa_supplicant/wpa_supplicant.conf
      echo "	ssid=\"$SSID\"" >> etc/wpa_supplicant/wpa_supplicant.conf
      echo "	psk=\"$WIFIPASS\"" >> etc/wpa_supplicant/wpa_supplicant.conf
      echo "	key_mgmt=WPA-PSK" >> etc/wpa_supplicant/wpa_supplicant.conf
      echo "}" >> etc/wpa_supplicant/wpa_supplicant.conf
   fi
fi

sync

echo "Thumbdrive ready to use";
cd - > /dev/null;


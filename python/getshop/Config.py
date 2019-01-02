# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.
import socket
import struct

import uuid
import pickledb
import fcntl

class config:
    def __init__(self):
        commondatabase = pickledb.load('/storage/getshop/getshop.db', True);
        self.token = commondatabase.get('token')
        self.remoteHostName = commondatabase.get('remotehostname')

        if not self.remoteHostName:
            self.remoteHostName = raw_input("Please enter your getshop address: ")
            commondatabase.set('remotehostname', str(self.remoteHostName))

        if not self.token:
            self.token = str(uuid.uuid4());
            commondatabase.set('token', self.token)
  
    def getIp(self, ifname):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        return socket.inet_ntoa(fcntl.ioctl(
            s.fileno(),
            0x8915,  # SIOCGIFADDR
            struct.pack('256s', ifname[:15])
        )[20:24])
        
    
    
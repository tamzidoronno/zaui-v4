# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.


import uuid
import pickledb
import datetime
import urllib, urllib2


class ZwaveLogHandler:
    def __init__(self, config):
        self.config = config
        self.route = "zwaveloghandler"
        self.database = pickledb.load('accesscodes.db', True);
        
    def doGet(self, query_components):
        if "action" in query_components:
            if (query_components['action'][0] == "accesscodeused"):
                self.handleUserCode(query_components)
                return "Saved Successfully"
            
            if (query_components['action'][0] == "getcodes"):
                return self.getCodes(query_components)
            
            if (query_components['action'][0] == "ackcodes"):
                return self.ackCode(query_components)
            
        return "LOG HANDLER";
    
    # /?route=zwaveloghandler&action=accesscodeused&deviceId={deviceId}&slot={slot}
    def handleUserCode(self, query_components):
        deviceId = query_components['deviceId'][0]
        slot = query_components['slot'][0]
        self.addCode(deviceId, slot)
    
    def addCode(self, deviceId, slot):
        key = str(uuid.uuid4());
        print "key: " + key
        
        data = {
            'id' : key,
            'deviceId' : deviceId,
            'slot' : slot,
            'time' : datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        }
        
        self.database.set(key, data);
        self.database.dump()
        self.pushCodes();
        
    # /?route=zwaveloghandler&action=getcodes
    def getCodes(self, query_components):
        keys = self.database.getall()
        result = [];
        
        for key in keys:
            result.append(self.database.get(key))
            
        return result;
    
    # /?route=zwaveloghandler&action=ackcodes&codeIds=id1,id2,id3
    def ackCode(self, query_components):
        codestoack = query_components['codeIds'][0].split(',');
        result = [];
        
        for key in codestoack:
            if (self.database.get(key)):
                self.database.rem(key)
            
        return "DONE";

    def pushCodes(self):
        keys = self.database.getall()
        for key in keys:
            data = self.database.get(key)
            deviceid = data['deviceId']
            slot = data['slot']
            time = urllib.quote(data['time'])
            url = "http://"+self.config.remoteHostName+"/scripts/apac/registerActivity.php?token="+self.config.token+"&deviceId="+str(deviceid)+"&slotId="+str(slot)+"&date="+time
            contents = urllib2.urlopen(url).read()
            
            print url;
            
            if (contents == "OK"):
                self.database.rem(key)
        
        
if __name__ == "__main__":
    handler = ZwaveLogHandler();
    handler.addCode(25, 1)
    print "done";
    
# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.

import uuid
import sys, traceback
import urllib, urllib2, threading
from datetime import datetime, time
from time import sleep
import json, time

class MessageHandler:
    
    def __init__(self, config):
        self.config = config
        self.listeners = [];
        threading.Thread(target=self.startPulling).start()
        
    def addListener(self, listener):
        self.listeners.append(listener);
        
    def startPulling(self):
        print "Started pulling";
        
        while(True):
            url = "http://"+self.config.remoteHostName+"/scripts/longpullgsd.php?token="+self.config.token

            try:
                contents = urllib2.urlopen(url).read()
            except: 
                time.sleep(3)
                continue
                
                
            if (contents is None) or (contents == "null") or (contents == "") or not contents:
                continue;
            
            messages = json.loads(contents);
            
            try:
                if messages['jsonDecodeErorCode'] == 4:
                    continue;
            except:
                a = "b";
            
            if (messages is None):
                continue;
            
            if (len(messages) == 1 and messages[0]['className'] == "com.thundashop.core.gsd.GdsAccessDenied"):
                print "Access denied";
                sleep(600);
                continue;
            
            print "processing message";
            for message in messages:
                for listener in self.listeners:
                    try:
                        listener.processMessage(message)
                    except Exception as ex:
                        print "Failed to process message";
                        print ex;
                        traceback.print_exc(file=sys.stdout)
            
            

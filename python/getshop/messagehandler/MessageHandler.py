# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.

import uuid
import sys, traceback
import urllib, urllib2, threading
from datetime import datetime, time
from time import sleep
import json, time
import traceback
import logging

class MessageHandler:
    
    def __init__(self, config):
        self.config = config
        self.listeners = [];
        thread = threading.Thread(target=self.startPulling);
        thread.daemon = True;
        thread.start();
        
    def addListener(self, listener):
        self.listeners.append(listener);
        
    def startPulling(self):
        logging.info("Started pulling from server")
        
        urllib2.urlopen("http://"+self.config.remoteHostName+"/scripts/setGdsConfig.php?token="+self.config.token+"&key=supportDirectPrint&value=false").read()
        
        while(True):
            url = "http://"+self.config.remoteHostName+"/scripts/longpullgsd.php?token="+self.config.token
            
            try:
                contents = urllib2.urlopen(url).read()
            except: 
                time.sleep(3)
                continue
                
            if (contents is None) or (contents == "null") or (contents == "") or not contents:
                continue;
            
            messages = False;
            
            try:
                messages = json.loads(contents);
            
                if (messages is None):
                    continue;

                if (len(messages) == 1 and messages[0]['className'] == "com.thundashop.core.gsd.GdsAccessDenied"):
                    print "Access denied";
                    sleep(600);
                    continue;
                    
            except:
                traceback.print_exc()
                time.sleep(1);
                continue
            
            if (messages == False):
                time.sleep(1);
                continue
                
            logging.info("processing message");
            for message in messages:
                for listener in self.listeners:
                    try:
                        listener.processMessage(message)
                    except Exception as ex:
                        logging.error("Failed to process message", exc_info=True)
                        traceback.print_exc(file=sys.stdout)
            
            

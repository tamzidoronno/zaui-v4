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
        
    def runPull(self):
        logging.debug("Reading content");
        url = "http://"+self.config.remoteHostName+"/scripts/longpullgsd.php?token="+self.config.token
        contents = urllib2.urlopen(url).read()
        
        logging.debug("Content found");
        if (contents is None) or (contents == "null") or (contents == "") or not contents:
            logging.debug("Not");
            time.sleep(1);
            return

        logging.debug("Decoding content");
        messages = json.loads(contents);
        
        if (messages is None):
            return;

        logging.debug("check access");
        if (len(messages) == 1 and messages[0]['className'] == "com.thundashop.core.gsd.GdsAccessDenied"):
            logging.debug("Access denied, sleeping for 600");
            sleep(600);
            return

        logging.debug("Access establised, continueing");
        if (messages == False):
            return

        logging.info("processing message");
        for message in messages:
            for listener in self.listeners:
                try:
                    listener.processMessage(message)
                except Exception as ex:
                    logging.error("Failed to process message", exc_info=True)
                    traceback.print_exc(file=sys.stdout)
                    
    def updateServerWithConfig(self):
        urllib2.urlopen("http://"+self.config.remoteHostName+"/scripts/setGdsConfig.php?token="+self.config.token+"&key=supportDirectPrint&value=false").read()
        logging.info("Updated server with new config");
        
    def startPulling(self):
        logging.info("Started pulling from server")
        
        hasUpdatedConfig = False;
            
        while(True):
            if (hasUpdatedConfig == False):
                try:
                    self.updateServerWithConfig()
                    hasUpdatedConfig = True;
                except Exception as ex:
                    print "Update failed, trying again next time";
            
            try:
                logging.debug("Started a new cycle");
                self.runPull();
            except Exception as ex:
                logging.error("Failed to process message", exc_info=True)
                time.sleep(3);
            
            

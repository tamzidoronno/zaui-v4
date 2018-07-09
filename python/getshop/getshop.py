#!/usr/bin/python
import logging

from getshop.WebServer import WebServer
from Config import config
from zwaveloghandler.ZwaveLogHandler import ZwaveLogHandler

if __name__ == "__main__":
    logging.basicConfig(format='%(asctime)s [%(levelname)s] %(message)s', level=logging.INFO)
    
    config = config();

    text_file = open("/storage/getshop/token.txt", "w")
    text_file.write("{ \"token\" : \""+config.token+"\" }")
    text_file.close()
    
    logging.info("Pushing to: %s" % config.remoteHostName)
    logging.info("Token: %s" % config.token)
    logHandler = ZwaveLogHandler(config);
    webServer = WebServer(config);
    webServer.addHandler(logHandler)
    webServer.start();
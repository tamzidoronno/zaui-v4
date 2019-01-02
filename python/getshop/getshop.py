#!/usr/bin/python
import logging
import sys

from getshop.WebServer import WebServer
from Config import config
from zwaveloghandler.ZwaveLogHandler import ZwaveLogHandler
from messagehandler.MessageHandler import MessageHandler
from printerhandler.PrinterHandler import PrinterHandler 

if __name__ == "__main__":
    logging.basicConfig(format='%(asctime)s [%(levelname)s] %(message)s', level=logging.INFO)
    
    config = config();
    
    if len(sys.argv) > 1 and sys.argv[1] == "onlyspecify":
        exit(0)

    text_file = open("/storage/getshop/token.txt", "w")
    text_file.write("{ \"token\" : \""+config.token+"\" }")
    text_file.close()
    
    logging.info("Pushing to: %s" % config.remoteHostName)
    logging.info("Token: %s" % config.token)
    logHandler = ZwaveLogHandler(config);
    
    printhandler = PrinterHandler(config)
    
    # Message Handler, this listens for getshop messages.
    messagehandler = MessageHandler(config);
    messagehandler.addListener(logHandler);
    messagehandler.addListener(printhandler);
    
    webServer = WebServer(config);
    webServer.addHandler(logHandler)
    webServer.start();
#!/usr/bin/python
import logging
from logging.handlers import RotatingFileHandler
import sys
import os

from getshop.WebServer import WebServer
from Config import config
from zwaveloghandler.ZwaveLogHandler import ZwaveLogHandler
from messagehandler.MessageHandler import MessageHandler
from printerhandler.PrinterHandler import PrinterHandler 

def my_handler(type, value, tb):
    logging.exception("Uncaught exception: {0}".format(str(value)))


if __name__ == "__main__":

    if not os.path.exists('/storage/log'):
        os.makedirs('/storage/log');
        
    logging.basicConfig(level=logging.DEBUG)
    
    
    
    logger = logging.getLogger();
    handler = RotatingFileHandler('/storage/log/getshop.log', maxBytes=20971520, backupCount=2)
    logger.addHandler(handler);
    
    default_formatter = logging.Formatter('%(asctime)s [%(levelname)s] %(message)s');
    handler.setFormatter(default_formatter);
    
    logging.info("Starting....");
    
    sys.excepthook = my_handler
    
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
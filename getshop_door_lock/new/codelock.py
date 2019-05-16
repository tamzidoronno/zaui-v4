#!/usr/bin/python
import signal
import socket
import sys
import RPi.GPIO as GPIO
import time
import threading
import subprocess
import os.path
import urllib
import contextlib
import json

from time import sleep



class DoorLock(object):

     def __init__(self, LED, OPENBUTTON, STRIKE_RELAY, DOORENGINE_RELAY, SOCKETPORT, DEVICEID, STRIKE_OPEN_WHEN, NOTIFYURL):
         self.LED = LED
         self.OPENBUTTON = OPENBUTTON
         self.STRIKE_RELAY = STRIKE_RELAY
         self.DOORENGINE_RELAY = DOORENGINE_RELAY
         self.DEVICEID = DEVICEID
         self.FORCEDOPEN = False;
         self.STRIKE_OPEN_WHEN = STRIKE_OPEN_WHEN;
         self.OPENTIME_SECONDS = 5;
         self.OPEN_IN_PROGRESS = False;
         self.FIREBUTTON = 18;

         self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
         self.client_socket.connect(('localhost', SOCKETPORT))
         GPIO.setmode(GPIO.BCM)
         GPIO.setup(self.LED, GPIO.OUT)
         GPIO.output(self.LED, True)
         
         global interrupted
         interrupted = False
         
         #Button open lock
         GPIO.setup(self.OPENBUTTON, GPIO.IN, pull_up_down=GPIO.PUD_UP)

         #Button - fire
         GPIO.setup(self.FIREBUTTON, GPIO.IN, pull_up_down=GPIO.PUD_UP)
         
         #Signal from door opener
         #GPIO.setup(5, GPIO.IN, pull_up_down=GPIO.PUD_UP)
         
         #Door opener
         GPIO.setmode(GPIO.BCM)
         GPIO.setup(self.DOORENGINE_RELAY, GPIO.OUT)
         GPIO.setup(self.STRIKE_RELAY, GPIO.OUT)
         
         #Set opening signals to default True ( this means that the lock should be locked and the door opener should not be activated during start)
         GPIO.output(self.STRIKE_RELAY, not self.STRIKE_OPEN_WHEN)
         GPIO.output(self.DOORENGINE_RELAY, not self.STRIKE_OPEN_WHEN)

         signal.signal(signal.SIGINT, self.signal_handler)
         self.setNotificationUrl()

     def setNotificationUrl(self):
         f = open('/storage/getshop/getshop.db', 'r')
         configdb = f.read()
         configdb = json.loads(configdb);
         self.NOTIFYURL = "https://" + configdb['remotehostname'] + "/scripts/apac/registerActivityEntrance.php?token=" + configdb["token"] + "&deviceid="+str(self.DEVICEID)+"&code={CODE}";
         print self.NOTIFYURL
          

     def checkOpenLockButton(self):
         global interrupted
     
         while(1): 
             if interrupted:
                 break
     
             if (GPIO.input(self.OPENBUTTON) == 0 and not self.FORCEDOPEN):
                 self.openDoor();

             if (GPIO.input(self.OPENBUTTON) == 0 and self.FORCEDOPEN):
                 self.startOpenSequence();
     
             time.sleep(0.05);

     def checkFireState(self):
         global interrupted
     
         while(1): 
             if interrupted:
                 break
     
             if (GPIO.input(self.FIREBUTTON) == 0 and not self.FORCEDOPEN):
                 self.setForceOpen();

             time.sleep(0.05);
     
     def blinkLeds(self):
         GPIO.output(self.LED, False)
         time.sleep(self.OPENTIME_SECONDS)
         GPIO.output(self.LED, True)
        
     def openDoor(self):
         blinkThread = threading.Thread(target=self.blinkLeds)
         blinkThread.start()
         
         unlockDoorThread = threading.Thread(target=self.unlockDoorStartThread)
         unlockDoorThread.start()
     
         engineRelaySequence = threading.Thread(target=self.startOpenSequence)
         engineRelaySequence.start()

     def startOpenSequence(self):
         time.sleep(0.3);
         GPIO.output(self.DOORENGINE_RELAY, self.STRIKE_OPEN_WHEN)
         time.sleep(self.OPENTIME_SECONDS)
         GPIO.output(self.DOORENGINE_RELAY, not self.STRIKE_OPEN_WHEN)

     
     def unlockDoorStartThread(self):
         self.OPEN_IN_PROGRESS = True
         GPIO.output(self.STRIKE_RELAY, self.STRIKE_OPEN_WHEN)
         time.sleep(self.OPENTIME_SECONDS)
         GPIO.output(self.STRIKE_RELAY, not self.STRIKE_OPEN_WHEN)
         self.OPEN_IN_PROGRESS = False

     def forceOpen(self):
         if self.FORCEDOPEN and GPIO.input(self.STRIKE_RELAY) == self.STRIKE_OPEN_WHEN:
             return

         GPIO.output(self.LED, False)
         GPIO.output(self.STRIKE_RELAY, self.STRIKE_OPEN_WHEN)
         print "Forced open";
         self.FORCEDOPEN = True;

     def stopForceOpen(self):
         GPIO.output(self.LED, True)
         GPIO.output(self.STRIKE_RELAY, not self.STRIKE_OPEN_WHEN)
         print "Forced stoped";
         self.FORCEDOPEN = False;
         
     
     def sendFeedBack(self, code):
         link = self.NOTIFYURL.replace("{CODE}", code); 
         print link;
         with contextlib.closing(urllib.urlopen(link)) as x:
             myfile = x.read()
          
     def checkPinCode(self, code, codeSize):
        f = open('/storage/validcodes.txt', 'r')
        validCodes = f.read()
        validCodes = json.loads(validCodes);
        validCodes = validCodes[str(self.DEVICEID)];

        if len(code) < codeSize:
             return False
     
        if int(code) in validCodes:
             if self.FORCEDOPEN:
                 return True 

             feedBackThread = threading.Thread(target=self.sendFeedBack, args=[code])
             feedBackThread.start();
             self.openDoor();
             return True 
         
        return False

     def isForcedOpen(self):
         fileName = "/root/getshop_door_lock/forceopenstate";
         fileName += str(self.DEVICEID);

	 if os.path.exists(fileName):
              with open(fileName, 'r') as myfile:
                  state=myfile.read()
                  return state == "on";

         return False;
        
     
     def signal_handler(self, signal, frame):
         global interrupted
         interrupted = True

     def checkForceState(self):
         while True:
             if interrupted:
                  self.client_socket.close();
                  break;

             if self.isForcedOpen():
                  self.forceOpen();

             if self.FORCEDOPEN and not self.isForcedOpen():
                  self.stopForceOpen();

             time.sleep(0.5);

     def setForceOpen(self):
         fileName = "/root/getshop_door_lock/forceopenstate";
         fileName += str(self.DEVICEID);

         text_file = open(fileName, "w")
         text_file.truncate()
         text_file.write("on")
         text_file.close()

         print "Setting force open";

     def setForceClose(self):
         fileName = "/root/getshop_door_lock/forceopenstate";
         fileName += str(self.DEVICEID);

         text_file = open(fileName, "w")
         text_file.truncate()
         text_file.write("off")
         text_file.close()

         print "Setting force close";

     def checkSubMenuEntry(self, code):
         if code == "0101":
             self.setForceOpen();
             return True

         if code == "0102":
             self.setForceClose();
             return True
       
     
     def main(self):
         global interrupted
         concattedPin = "";
         data = "";
         buttonThread = threading.Thread(target=self.checkOpenLockButton)
         buttonThread.start();

         forceThread = threading.Thread(target=self.checkForceState)
         forceThread.start();

         fireState = threading.Thread(target=self.checkFireState)
         fireState.start();
     
         while True:
             if interrupted:
                  self.client_socket.close();
                  break;

             try: 
                  data = self.client_socket.recv(512)
             except socket.error as (code,msg):
                  interrupted = True
     
             concattedPin = concattedPin + data
             fourDigitCode = concattedPin[-4:]
             sixDigitCode = concattedPin[-6:]

             if self.OPEN_IN_PROGRESS == True or self.isForcedOpen():
                 validCode = self.checkSubMenuEntry(fourDigitCode)

                 if validCode == True:
                     concattedPin = "";

                 continue

             validCode = self.checkPinCode(fourDigitCode, 4)
             
             if validCode == True:
                 concattedPin = "";
                 continue;

             validCode = self.checkPinCode(sixDigitCode, 6)
             
             if validCode == True:
                 concattedPin = "";




if __name__ == "__main__":
    lock = DoorLock(16, 7, 10, 27, 51717)
    lock.main()

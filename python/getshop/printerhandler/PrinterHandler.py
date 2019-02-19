import base64
import subprocess
import json
import os
import logging

class PrinterHandler:
    def __init__(self, config):
        self.config = config;
        
    def WriteContentToTmpFile(self, msg):
        text_file = open("/tmp/printcontent.txt", "w")
        text_file.write(msg)
        text_file.close()
        
    def processMessage(self, message):
# IF used network printer.
#            subprocess.call("cat /tmp/receipt2.prn |netcat -q 0 172.100.100.50 9100", shell=True)
        files = os.listdir("/dev/usb/");
        for filename in files:
            if (filename.startswith('lp')):
                if (message['className'] == "com.thundashop.core.gsd.DirectPrintMessage"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(base64.b64decode(message['content']));
                    subprocess.call("cat /tmp/printcontent.txt > /dev/usb/"+filename, shell=True)
                    logging.info("Processed direct message");
                    
                if (message['className'] == "com.thundashop.core.gsd.DevicePrintRoomCode"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/roomcode.php"], shell=False)
                    subprocess.call("cat /tmp/receipt2.prn > /dev/usb/"+filename, shell=True)
                    logging.info("Processed printing (roomcode.php)");

                if (message['className'] == "com.thundashop.core.gsd.RoomReceipt"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/roomreceipt.php"], shell=False)
                    subprocess.call("cat /tmp/receipt2.prn > /dev/usb/"+filename, shell=True)
                    logging.info("Processed printing (roomreceipt.php)");

                if (message['className'] == "com.thundashop.core.gsd.DevicePrintMessage"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/index.php"], shell=False)
                    subprocess.call("cat /tmp/receipt2.prn > /dev/usb/"+filename, shell=True)
                    logging.info("Processed printing (index.php)");

                if (message['className'] == "com.thundashop.core.gsd.KitchenPrintMessage"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/kitchenreceipt.php"], shell=False)
                    subprocess.call("cat /tmp/receipt_kitchen.prn > /dev/usb/"+filename, shell=True)
                    logging.info("Processed printing (kitchenreceipt.php)");

                if (message['className'] == "com.thundashop.core.gsd.GiftCardPrintMessage"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/giftcard.php"], shell=False)
                    subprocess.call("cat /tmp/receipt_giftcard.prn > /dev/usb/"+filename, shell=True)
                    logging.info("Processed printing (giftcard.php)");
            
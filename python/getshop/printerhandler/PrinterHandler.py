import base64
import subprocess
import json
import os

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
                if (message['className'] == "com.thundashop.core.gsd.DevicePrintRoomCode"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/roomcode.php"], shell=False)
                    subprocess.call("cat /tmp/receipt2.prn > /dev/usb/"+filename, shell=True)
                    print "Processed printing (roomcode.php)";

                if (message['className'] == "com.thundashop.core.gsd.RoomReceipt"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/roomreceipt.php"], shell=False)
                    subprocess.call("cat /tmp/receipt2.prn > /dev/usb/"+filename, shell=True)
                    print "Processed printing (roomreceipt.php)";

                if (message['className'] == "com.thundashop.core.gsd.DevicePrintMessage"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/index.php"], shell=False)
                    subprocess.call("cat /tmp/receipt2.prn > /dev/usb/"+filename, shell=True)
                    print "Processed printing (index.php)";

                if (message['className'] == "com.thundashop.core.gsd.KitchenPrintMessage"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/kitchenreceipt.php"], shell=False)
                    subprocess.call("cat /tmp/receipt_kitchen.prn > /dev/usb/"+filename, shell=True)
                    print "Processed printing (kitchenreceipt.php)";

                if (message['className'] == "com.thundashop.core.gsd.GiftCardPrintMessage"):
                    printmessage = json.dumps(message);
                    self.WriteContentToTmpFile(printmessage);
                    subprocess.call(["/usr/bin/php", "/source/getshop/3.0.0/php/escpos_receipt_generator/giftcard.php"], shell=False)
                    subprocess.call("cat /tmp/receipt_giftcard.prn > /dev/usb/"+filename, shell=True)
                    print "Processed printing (giftcard.php)";
            
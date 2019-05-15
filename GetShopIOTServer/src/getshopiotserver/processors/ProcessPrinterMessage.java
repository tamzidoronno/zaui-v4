/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver.processors;

import com.thundashop.core.gsd.DevicePrintMessage;
import com.thundashop.core.gsd.DirectPrintMessage;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import getshopiotserver.GetShopIOTCommon;
import getshopiotserver.MessageProcessorInterface;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author boggi
 */
public class ProcessPrinterMessage extends GetShopIOTCommon implements MessageProcessorInterface {
    
    @Override
    public void processMessage(GetShopDeviceMessage msg) {
        if (msg instanceof DirectPrintMessage) {
            try {
                printMessage((DirectPrintMessage)msg);
            } catch (IOException ex) {
                logPrintException(ex);
            }
        }
    }

    private void printMessage(DirectPrintMessage directPrintMessage) throws IOException {
        logPrint("Printing receipt...");
        
        File dir = new File("/dev/usb");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
          for (File child : directoryListing) {
            // Do something with child
            if (child.getName().contains("lp")) {
                Files.write(
                child.toPath(), 
                Base64.getDecoder().decode(directPrintMessage.content), 
                StandardOpenOption.APPEND);    
            }
          }
        }
        
    }
    
}

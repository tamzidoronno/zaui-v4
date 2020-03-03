/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver.processors;

import com.thundashop.core.gsd.DirectPrintMessage;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import getshopiotserver.GetShopIOTCommon;
import getshopiotserver.MessageProcessorInterface;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
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
        
        if(true) {
            logPrint("Printing socket...");
            printToSocket("192.168.10.10", 9100, directPrintMessage.content);
            logPrint("Printing socket done...");
        } else {
            logPrint("Printing usb...");
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

    private void printToSocket(String ip, int port, String message) {
        try{
            byte[] res = Base64.getDecoder().decode(message);
            message = new String(res);
            System.out.println("############### MESSAGE #################");
            System.out.println("############### MESSAGE END #################");
            Socket sock= new Socket(ip, port);
            PrintWriter writer= new PrintWriter(sock.getOutputStream());
            writer.println(message);
            writer.close();
            sock.close();
            
            // Sleep as we wait for the printer to be finished printing.
            int sleepTime = 1000 + message.length();
            Thread.sleep(sleepTime);
        } catch(IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessPrinterMessage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}

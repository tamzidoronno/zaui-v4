/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver.processors;

import com.thundashop.core.gsd.DirectPrintMessage;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import getshopiotserver.GetShopIOTCommon;
import getshopiotserver.GetShopIOTOperator;
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
    public void processMessage(GetShopIOTOperator operator, GetShopDeviceMessage msg) {
        if (msg instanceof DirectPrintMessage) {
            try {
                printMessage(operator, (DirectPrintMessage)msg);
            } catch (IOException ex) {
                logPrintException(ex);
            }
        }
    }

    private void printMessage(GetShopIOTOperator operator, DirectPrintMessage directPrintMessage) throws IOException {
        String ip = operator.getSetupMessage().printerip;
        Integer port = operator.getSetupMessage().printerPort;
        if(ip != null && !ip.isEmpty()) {
            logPrint("Printing socket ip:" + ip + ", port: " + port);
            printToSocket(ip, port, directPrintMessage.content);
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

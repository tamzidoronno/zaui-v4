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

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
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
        }else if (GetShopIOTOperator.isWindows) {
            this.printToWindows(directPrintMessage.content);
        }
        else {
            this.logPrint("Printing usb...");
            File dir = new File("/dev/usb");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                File[] var7 = directoryListing;
                int var8 = directoryListing.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    File child = var7[var9];
                    if (child.getName().contains("lp")) {
                        Files.write(child.toPath(), Base64.getDecoder().decode(directPrintMessage.content), new OpenOption[]{StandardOpenOption.APPEND});
                    }
                }
            }
        }        
    }

    private void printToSocket(String ip, int port, String message) {
        try{
            this.logPrint("Inside ProcessPrinterMessage.printToSocket");
            this.logPrint("Message:" + message);
            System.out.println("SystemOut: Inside ProcessPrinterMessage.printToSocket");
            byte[] res = Base64.getDecoder().decode(message);
            message = new String(res, StandardCharsets.UTF_8);
            Socket sock= new Socket(ip, port);
            PrintWriter writer= new PrintWriter(sock.getOutputStream());
            writer.println(message);
            writer.close();
            this.logPrint("Done and closing socket..");
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

    private void printToWindows(final String content) throws IOException {
        this.logPrint("Printing on Windows, saving temp files to C:\\GetShop");
        System.out.println("Printing message on windows...");
        File path = new File("C:\\GetShop\\tmp_print_file.txt");
        if (path.exists()) {
            path.delete();
        }

        path.createNewFile();
        byte[] res = Base64.getDecoder().decode(content);
        String message = new String(res, StandardCharsets.UTF_8);
        // strips off all non-ASCII characters
        message = message.replaceAll("[^\\x00-\\x7F]", "");

        // removes non-printable characters from Unicode
        message = message.replaceAll("\\p{C}", "");

        List<String> lines =  Arrays. asList("\uFEFF" +message);
        Files.write(path.toPath(), lines, StandardCharsets.UTF_8);


        try {
            final Process p = Runtime.getRuntime().exec("print /d:\"\\\\localhost\\POS-80-Series\" tmp_print_file.txt");
            p.waitFor();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }


}

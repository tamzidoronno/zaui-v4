/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;
import com.thundashop.core.gsd.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.Normalizer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
 
/**
 *
 * @author ktonder
 */
public class PrinterMessageGenerator {
    private String tmpContentFile = UUID.randomUUID().toString();
    private String storeId;
    
    public PrinterMessageGenerator(String storeId) {
        this.storeId = storeId;
    }
    
    public DirectPrintMessage generateEscPos(GetShopDeviceMessage msg, String printerType) {
        String type = getType(msg);
        
        if (type == null) {
            return null;
        }
        
        DirectPrintMessage direct = new DirectPrintMessage();
        
        writeContentToTmpFile(msg);
        executePhpGenerator(type, printerType);
        direct.content = getContent();
        deleteTempFiles();
        
        return direct;
    }
    
    private String getType(GetShopDeviceMessage msg) {
        if (msg instanceof DevicePrintRoomCode) {
            return "roomcode";
        }
        
        if (msg instanceof RoomReceipt) {
            return "roomreceipt";
        }
        
        if (msg instanceof DevicePrintMessage) {
            return "index";
        }
        
        if (msg instanceof KitchenPrintMessage) {
            return "kitchenreceipt";
        }
        
        if (msg instanceof GiftCardPrintMessage) {
            return "receipt_giftcard";
        }
        
        return null;
    }

    public boolean isPrintMessage(GetShopDeviceMessage msg) {
        return getType(msg) != null;
    }
    
    private void writeContentToTmpFile(GetShopDeviceMessage msg) {
        Gson gson = new Gson();
        String text = gson.toJson(msg);
        text = StringUtils.stripAccents(text);
        text = text.replace("Ø", "O");
        text = text.replace("Æ", "AE");
        text = text.replace("Å", "A");
        text = text.replace("ø", "o");
        text = text.replace("æ", "ae");
        text = text.replace("å", "a");

        try (PrintStream out = new PrintStream(new FileOutputStream("/tmp/"+tmpContentFile+".txt"))) {
            out.print(text);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrinterMessageGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteTempFiles() {
        File tmpFile = new File("/tmp/"+tmpContentFile+".txt");
        
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        
        tmpFile = new File("/tmp/"+tmpContentFile+".prn");
       
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
    }

    private void executePhpGenerator(String type, String printerType) {
        try {
            String line;
            StringBuilder output = new StringBuilder();
            Process p = Runtime.getRuntime().exec("php /source/getshop/3.0.0/php/escpos_receipt_generator/" + type + ".php " + tmpContentFile + " " + storeId + " " + printerType);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                output.append(line);
            }
            input.close();
            System.out.println(output.toString());
        } catch (Exception err) {
          err.printStackTrace();
        }
        
    }

    private String getContent() {
        byte[] contents = readBytesFromFile("/tmp/"+tmpContentFile+".prn");
        return Base64.encodeBase64String(contents);
    }
 
    private byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

    
}

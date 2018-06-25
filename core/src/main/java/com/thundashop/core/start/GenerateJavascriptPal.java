/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author ktonder
 */
public class GenerateJavascriptPal {
    public static void main(String[] args) throws Exception {
        
        boolean createForBookingProcess = true;
        
        GenerateJavascriptApi.pathToBuildClasses = "/home/boggi/netbeans/3.0.0/GetShop Core/build/classes/";
        GenerateJavascriptApi.pathToJavaSource = "/source/getshop/3.0.0/core/src/main/java/";
        GenerateJavascriptApi.storeFileIn = "/source/getshop/3.0.0/html5apps/pmsmobile/public_html/app/getshopapi.js";
        if(createForBookingProcess) {
            String pathToBookingProcessFile = "/source/getshop/3.0.0/com.getshop.client/apps/GslBooking/GslBookingInject.js";
            GenerateJavascriptApi.storeFileIn = "/tmp/getshopapi.js";
            GenerateJavascriptApi.generateJavascriptForBookingProcess();
            String content = new String(Files.readAllBytes(Paths.get(GenerateJavascriptApi.storeFileIn)));
            String bookingProcessFileContent = new String(Files.readAllBytes(Paths.get(pathToBookingProcessFile)));
            content = content.replaceAll("GetShopApiWebSocket", "GetShopApiWebSocketEmbeddedBooking");
            content = replaceMatching(bookingProcessFileContent, "/* START GetShop Websocket api */", "/* END GetShop Websocket api */", content);
            Files.write(Paths.get(pathToBookingProcessFile), content.getBytes());
        } else {
            GenerateJavascriptApi.main(args);
        }
    }
    
    public static String replaceMatching(String content, String lowerBound, String upperBound, String newText){
        String start = content.substring(0, content.indexOf(lowerBound));
        String end = content.substring(content.indexOf(upperBound));
        return start +"\n"+lowerBound+"\n"+ newText + end;
    }
    
}

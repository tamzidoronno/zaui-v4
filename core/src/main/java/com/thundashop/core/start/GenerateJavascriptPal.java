/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author ktonder
 */
public class GenerateJavascriptPal {
    public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
        GenerateJavascriptApi.pathToBuildClasses = "/home/boggi/netbeans/3.0.0/GetShop Core/build/classes/";
        GenerateJavascriptApi.pathToJavaSource = "/source/getshop/3.0.0/core/src/main/java/";
        GenerateJavascriptApi.storeFileIn = "/source/getshop/3.0.0/html5apps/pmsmobile/public_html/app/getshopapi.js";
        GenerateJavascriptApi.main(args);
    }
}

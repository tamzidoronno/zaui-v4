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
public class GenerateJavascriptHung {
    public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
        GenerateJavascriptApi.pathToBuildClasses = "/home/hung/netbeans/3.0.0/GetShop Core/build/classes/";
        GenerateJavascriptApi.pathToJavaSource = "/source/getshop/3.0.0/core/src/main/java/";
        GenerateJavascriptApi.storeFileIn = "/home/hung/getshopapi.js";
        GenerateJavascriptApi.main(args);
    }
}


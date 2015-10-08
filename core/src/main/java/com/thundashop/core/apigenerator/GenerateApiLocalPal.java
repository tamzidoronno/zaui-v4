/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.apigenerator;

import java.io.IOException;

/**
 *
 * @author ktonder
 */
public class GenerateApiLocalPal {
    public static void main(String[] args) throws ClassNotFoundException, IOException, Exception {
        String homefolder = "boggi";
        GenerateApi ga = new GenerateApi("/home/"+homefolder+"/netbeans/3.0.0/GetShop Core/", "/home/ktonder/netbeans/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.client/", "/source/getshop/3.0.0/core", false);
        ga.generate();
        ga = new GenerateApi("/home/"+homefolder+"/netbeans/3.0.0/GetShop Core/", "/home/ktonder/netbeans/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.pullserver/", "/source/getshop/3.0.0/core", false);
        ga.generate();
    }

}

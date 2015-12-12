package com.thundashop.core.apigenerator;

import java.io.IOException;

/**
 *
 * @author ktonder
 */
public class GenerateApiLocalPalHome {
    public static void main(String[] args) throws ClassNotFoundException, IOException, Exception {
        System.out.println("Generating local api");
        GenerateApi ga = new GenerateApi("/home/boggi/netbeans/3.0.0/GetShop Core/", "/home/boggi/netbeans/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.client/", "/source/getshop/3.0.0/core", false);
        ga.generate();

        ga = new GenerateApi("/home/boggi/netbeans/3.0.0/GetShop Core/", "/home/boggi/netbeans/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.pullserver/", "/source/getshop/3.0.0/core", false);
        ga.generate();
    }
}

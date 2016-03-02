 package com.thundashop.core.apigenerator;

import java.io.IOException;

/**
 *
 * @author ktonder
 */
public class GenerateApiLocalHung {
    public static void main(String[] args) throws ClassNotFoundException, IOException, Exception {
        System.out.println("Generating local api");
        GenerateApi ga = new GenerateApi("/home/hung/netbeans/3.0.0/GetShop Core/", "/home/hung/netbeans/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.client/", "/source/getshop/3.0.0/core", false);
        ga.generate();

        ga = new GenerateApi("/home/hung/netbeans/3.0.0/GetShop Core/", "/home/hung/netbeans/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.pullserver/", "/source/getshop/3.0.0/core", false);
        ga.generate();
    }
}

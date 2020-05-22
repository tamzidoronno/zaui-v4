/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.apigenerator;

import java.io.IOException;

/**
 *
 * @author ktonder
 */
public class Runner {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        GenerateApi ga = new GenerateApi("/home/ktonder/netbeans/3.0.0/GetShop Core/build/classes", "/home/ktonder/netbeans/3.0.0/GetShop Messages/build/classes", "/source/getshop/3.0.0/core", "core", "Pms", false);
        ga.generateJavaApi();
        
        GenerateApi ga2 = new GenerateApi("/source/getshop/pos/core/build/classes/", "/source/getshop/pos/messages/build/classes/", "/source/getshop/pos/core/src/", "pos", "Pos", true);
        ga2.generateJavaApi();
        
        GenerateApi ga3 = new GenerateApi("/source/getshop/apac/core/build/classes/", "/source/getshop/apac/messages/build/classes/", "/source/getshop/apac/core/src/", "seros", "Seros", true);
        ga3.generateJavaApi();
        
        GenerateApi ga4 = new GenerateApi("/source/getshop/central/core/build/classes/", "/source/getshop/central/messages/build/classes/", "/source/getshop/central/core/src/", "central", "Central", true);
        ga4.generateJavaApi();
    }
}

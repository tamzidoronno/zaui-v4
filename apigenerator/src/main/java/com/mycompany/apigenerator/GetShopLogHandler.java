/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.apigenerator;

/**
 *
 * @author ktonder
 */
public class GetShopLogHandler {

    public static void logPrintStatic(String name, Object object) {
        System.out.println(name);
    }

    public static void logStack(Exception e, String translationhandler) {
        e.printStackTrace();
    }
    
}

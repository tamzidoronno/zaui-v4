/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.combined;

import com.thundashop.core.start.Runner;

/**
 *
 * @author boggi
 */
public class ComGetshopCombined {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting core");
        Core core = new Core(args);
        core.start();
    }
}

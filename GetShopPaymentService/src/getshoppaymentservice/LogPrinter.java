/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshoppaymentservice;

import java.util.Date;

/**
 *
 * @author boggi
 */
public class LogPrinter {
    public static void println(Object toPrint) {
        System.out.println(new Date() + " : " + toPrint);
    }
    public static void print(String out) {
        System.out.print(new Date() + " : " + out);
    }
}

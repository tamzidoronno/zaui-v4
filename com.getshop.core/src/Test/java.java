/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

/**
 *
 * @author ktonder
 */
public class java {

    public static void main(String[] args) {
        int minimum = 1;
        int maximum = 9;
        while (true) {
            int x1 = minimum + (int) (Math.random() * maximum);
            int x2 = minimum + (int) (Math.random() * maximum);
            int x3 = minimum + (int) (Math.random() * maximum);
            int x4 = minimum + (int) (Math.random() * maximum);
            int x5 = minimum + (int) (Math.random() * maximum);
            int x6 = minimum + (int) (Math.random() * maximum);
            int x7 = minimum + (int) (Math.random() * maximum);
            int x8 = minimum + (int) (Math.random() * maximum);
            int x9 = minimum + (int) (Math.random() * maximum);
            int sum = x1 + 13 * x2 / x3 + x4 + 12 * x5 - x6 - 11 + x7 * x8 / x9 - 10;
            if (sum == 66) {
                System.out.println("x1=" + x1 + " x2=" + x2 + " x3=" + x3 + " x4=" + x4 + " x5=" + x5 + " x6=" + x6 + " x7=" + x7 + " x8=" + x8 + " x9=" + x9);
            }
        }
    }
}

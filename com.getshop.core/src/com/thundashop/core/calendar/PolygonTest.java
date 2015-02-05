/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendar;

import java.awt.Point;
import java.awt.Polygon;

/**
 *
 * @author ktonder
 */
public class PolygonTest {
    public static void main(String[] args) {
        Polygon polygon = new Polygon();
        polygon.addPoint(6601509, 1137105);
        polygon.addPoint(6601509, 1444723);
        polygon.addPoint(6953199, 3268453);
        polygon.addPoint(7326263, 2402730);
        polygon.addPoint(6601509, 1137105);
        
        System.out.println(polygon.contains(new Point(6377930, 1040426)));
    }
}

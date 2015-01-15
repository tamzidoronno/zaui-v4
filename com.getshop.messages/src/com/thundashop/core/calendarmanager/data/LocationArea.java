/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendarmanager.data;

import com.thundashop.core.common.DataCommon;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class LocationArea extends DataCommon {
    public String name;
    public Point northWest;
    public Point northEast;
    public Point southWest;
    public Point southEast;
    public String[] locations;

    @Transient
    public List<Entry> entries = new ArrayList();
    
    public boolean contains(Point point) {
        Polygon polyGon = new Polygon();
        polyGon.addPoint(southWest.x, southWest.y);
        polyGon.addPoint(southEast.x, southEast.y);
        polyGon.addPoint(northEast.x, northEast.y);
        polyGon.addPoint(northWest.x, northWest.y);
        polyGon.addPoint(southWest.x, southWest.y);
        
        return polyGon.contains(point);
    }
}
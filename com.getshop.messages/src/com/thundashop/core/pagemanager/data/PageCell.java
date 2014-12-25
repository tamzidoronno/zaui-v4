package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PageCell implements Serializable {

    void clear() {
        appId = "";
        cells = new ArrayList();
        styles = "";
    }

    /* Mode for the cell */
    static class CellMode {
        public static String column = "COLUMN";
        public static String row = "ROW";
        public static String rotating = "ROTATING";
        public static String tab = "TAB";
        public static String floating = "FLOATING";
        public static String init = "INIT";
    }
    
    static class CellType {
        public static String normal = "NORMAL";
        public static String floating = "FLOATING";
    }
    

    String cellName = "";
    public Integer incrementalCellId;
    public String cellId = UUID.randomUUID().toString();
    public String mode = CellMode.column;
    public String type = "normal";
    public ArrayList<PageCell> cells = new ArrayList();
    public String appId;
    public FloatingData floatingData = new FloatingData();
    public String styles = "";
    public CarouselConfig carouselConfig = new CarouselConfig();
    public Double width = -1.0;

    PageCell createCell(String before) {
        PageCell newcell = new PageCell();
        if (before == null || before.isEmpty()) {
            cells.add(newcell);
        } else {
            ArrayList newList = new ArrayList();
            boolean added = false;
            for (PageCell cell : cells) {
                if (cell.cellId.equals(before)) {
                    added = true;
                    newList.add(newcell);
                }
                newList.add(cell);
            }
            
            if(!added) {
                newList.add(0, newcell);
            }
            
            cells = newList;
        }
        return newcell;
    }

    void extractDataFrom(PageCell cell, boolean includeCells) {
//        styles = cell.styles;
        mode = cell.mode;
        appId = cell.appId;
        cell.appId = "";
        type = cell.type;
        if(includeCells) {
            cells = cell.cells;
        }
    }

    public boolean isRotating() {
        return PageCell.CellMode.rotating.equalsIgnoreCase(mode);
    }

    public boolean isTab() {   
        return PageCell.CellMode.tab.equalsIgnoreCase(mode); 
    }
    
    
    public PageCell getCell(String pageCellId) {
        if (cellId.equals(pageCellId)) {
            return this;
        }

        for (PageCell cell : cells) {
            PageCell cell2 = cell.getCell(pageCellId);
            if (cell2 != null) {
                return cell2;
            }
        }

        return null;
    }

    public List<PageCell> getCellsFlatList() {
        List<PageCell> retCells = new ArrayList();
        retCells.add(this);

        for (PageCell cell : cells) {
            retCells.addAll(cell.getCellsFlatList());
        }

        return retCells;
    }
    }

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

    static class PageMode {

        public static String column = "COLUMN";
        public static String row = "ROW";
        public static String rotating = "ROTATING";
        public static String tab = "TAB";
        public static String floating = "FLOATING";
    }

    String cellName = "";
    public Integer incrementalCellId;
    public String cellId = UUID.randomUUID().toString();
    public String mode = PageMode.column;
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
            for (PageCell cell : cells) {
                if (cell.cellId.equals(before)) {
                    newList.add(newcell);
                }
                newList.add(cell);
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
        if(includeCells) {
            cells = cell.cells;
        }
    }

    public boolean isRotating() {
        return PageCell.PageMode.rotating.equalsIgnoreCase(mode);
    }

    public boolean isTab() {   
        return PageCell.PageMode.tab.equalsIgnoreCase(mode); 
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

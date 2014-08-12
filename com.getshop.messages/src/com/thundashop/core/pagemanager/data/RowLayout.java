package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RowLayout implements Serializable {
    public int numberOfCells = 0;
    public LinkedList<PageArea> areas = new LinkedList();
    int marginBottom = 0;
    int marginTop = 0;
    List<Double> rowWidth = new ArrayList();
    public String rowId = "";
    String outercss = "";
    String innercss = "";

    public PageArea createApplicationArea() {
        PageArea newPageArea = new PageArea();
        newPageArea.type = UUID.randomUUID().toString();
        areas.add(newPageArea);
        return newPageArea;
    }
}
package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.LinkedList;

public class PageLayout implements Serializable {
    public int leftSideBar = 0;
    public int marginLeftSideBar = 10;
    public int leftSideBarWidth = 20;
    public int rightSideBarWidth = 20;
    
    public int rightSideBar = 0;
    public int marginRightSideBar = 10;
    
    public LinkedList<String> sortedRows = new LinkedList();
    public LinkedList<RowLayout> rows = new LinkedList();
}

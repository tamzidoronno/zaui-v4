package com.thundashop.core.pagemanager.data;

import java.util.LinkedList;

public class PageLayout {
    public boolean leftSideBar = false;
    public int marginLeftSideBar = 10;
    
    public boolean rightSideBar = false;
    public int marginRightSideBar = 10;
    
    public LinkedList<RowLayout> rows = new LinkedList();
}

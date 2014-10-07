package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PageCell implements Serializable {
    public String cellId = UUID.randomUUID().toString();
    public boolean vertical = true;
    public LinkedList<PageCell> cells = new LinkedList();
    public Integer appId;

    void createCell(String after) {
        if(after == null || after.isEmpty()) {
            cells.add(new PageCell());
        } else {
            LinkedList newList = new LinkedList();
            for(PageCell cell : cells) {
                newList.add(cell);
                if(cell.cellId.equals(after)) {
                    newList.add(new PageCell());
                }
            }
            cells = newList;
        }
    }
    
}

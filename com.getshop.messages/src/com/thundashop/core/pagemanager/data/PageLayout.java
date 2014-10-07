package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class PageLayout implements Serializable {
    public PageCell header;
    public PageCell footer;
    public LinkedList<PageCell> rows = new LinkedList();

    void clear() {
        rows = new LinkedList();
    }

    void createCell(String incell, String after) {
        if(incell == null || incell.isEmpty()) {
            rows.add(new PageCell());
        } else {
           PageCell cell = findCell(rows, incell);
           cell.createCell(after);
        }
    }

    private PageCell findCell(LinkedList<PageCell> rows, String id) {
        for(PageCell cell : rows) {
            if(cell.cellId.equals(id)) {
                return cell;
            }
            if(!cell.cells.isEmpty()) {
                PageCell foundcell = findCell(cell.cells, id);
                if(foundcell != null) {
                    return foundcell;
                }
            }
        }
        return null;
    }
    
}

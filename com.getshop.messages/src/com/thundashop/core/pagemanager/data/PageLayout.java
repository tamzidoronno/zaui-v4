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

    public void createCell(String incell, String after, boolean vertical) {
        if(incell == null || incell.isEmpty()) {
            PageCell newpagecell = new PageCell();
            newpagecell.vertical = vertical;
            rows.add(newpagecell);
        } else {
           PageCell cell = findCell(rows, incell);
           if(cell.cells.isEmpty()) {
               PageCell newcell = cell.createCell(after);
               newcell.vertical = vertical;
               newcell.appId = cell.appId;
               after = newcell.cellId;
           }
            PageCell newcell = cell.createCell(after);
            newcell.vertical = vertical;
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

    public void deleteCell(String cellId) {
        deleteCellRecusive(cellId, rows);
    }

    private void deleteCellRecusive(String cellId, LinkedList<PageCell> cells) {
        PageCell toRemove = null;
        for(PageCell cell : cells) {
            if(cell.cellId.equals(cellId)) {
                toRemove = cell;
            } else if(cell.cells.size() > 0) {
                deleteCellRecusive(cellId, cell.cells);
            }
        }
        if(toRemove != null) {
            cells.remove(toRemove);
        }
    }
    
}

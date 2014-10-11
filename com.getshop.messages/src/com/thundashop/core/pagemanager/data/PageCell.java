package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PageCell implements Serializable {
    public String cellId = UUID.randomUUID().toString();
    public boolean vertical = true;
    public LinkedList<PageCell> cells = new LinkedList();
    public String appId;

    PageCell createCell(String after) {
        PageCell newcell = new PageCell();
        if(after == null || after.isEmpty()) {
            cells.add(newcell);
        } else {
            LinkedList newList = new LinkedList();
            for(PageCell cell : cells) {
                newList.add(cell);
                if(cell.cellId.equals(after)) {
                    newList.add(newcell);
                }
            }
            cells = newList;
        }
        return newcell;
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
    
}

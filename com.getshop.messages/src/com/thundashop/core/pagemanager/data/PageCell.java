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
    public String styles = "";
    public String innerStyles = "";
    public Double width = -1.0;
    

    PageCell createCell(String before) {
        PageCell newcell = new PageCell();
        if(before == null || before.isEmpty()) {
            cells.add(newcell);
        } else {
            LinkedList newList = new LinkedList();
            for(PageCell cell : cells) {
                if(cell.cellId.equals(before)) {
                    newList.add(newcell);
                }
                newList.add(cell);
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

package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PageCell implements Serializable {

	
    static class PageDirection {
        public static String vertical = "VERTICAL";
        public static String horizontal = "HORIZONTAL";
        public static String rotating = "ROTATING";
    }
    
    
    public String cellId = UUID.randomUUID().toString();
    public String direction = PageDirection.vertical;
    public ArrayList<PageCell> cells = new ArrayList();
    public String appId;
    public String styles = "";
    public CarouselConfig carouselConfig = new CarouselConfig();
    public String innerStyles = "";
    public Double width = -1.0;
    

    PageCell createCell(String before) {
        PageCell newcell = new PageCell();
        if(before == null || before.isEmpty()) {
            cells.add(newcell);
        } else {
            ArrayList newList = new ArrayList();
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
    
	public List<PageCell> getCellsFlatList() {
		List<PageCell> retCells = new ArrayList();
		retCells.add(this);
		
		for (PageCell cell : cells) {
			retCells.addAll(cell.getCellsFlatList());
		}
		
		return retCells;
	}
}

package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PageCell implements Serializable {
    static class PageMode {
        public static String vertical = "VERTICAL";
        public static String horizontal = "HORIZONTAL";
        public static String rotating = "ROTATING";
        public static String tab = "TAB";
    }
    
    String cellName = "";
    public int incrementalCellId;
    public String cellId = UUID.randomUUID().toString();
    public String mode = PageMode.vertical;
    public ArrayList<PageCell> cells = new ArrayList();
    public String appId;
    public String styles = "";
    public CarouselConfig carouselConfig = new CarouselConfig();
    public Double width = -1.0;
    

    PageCell createCell(String before, Integer incrementId) {
        PageCell newcell = new PageCell();
        newcell.incrementalCellId = incrementId;
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

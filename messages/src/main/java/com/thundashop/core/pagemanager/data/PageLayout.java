package com.thundashop.core.pagemanager.data;

import com.google.gson.Gson;
import com.thundashop.core.common.ErrorException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PageLayout implements Serializable {

    HashMap<String, ArrayList<PageCell>> areas = new HashMap();
    LinkedList<String> mobileList = new LinkedList();
    private LinkedList<String> mobileTmpList;
    private boolean flatMobileList = false;
    
    void clear() {
        areas.put("body", new ArrayList());
        areas.put("left_side_bar", new ArrayList());
//        areas.get("body").add(initNewCell(PageCell.CellMode.row));
    }

    public void moveCell(String cellid, boolean moveUp) {
        boolean identical = true;
        mobileTmpList = new LinkedList();
        buildMobileList(areas.get("body"), true);
        if(mobileList.size() != mobileTmpList.size()) {
            identical = false;
        } else {
            for(int i = 0; i < mobileList.size(); i++) {
                if(!mobileList.get(i).equals(mobileTmpList.get(i))) {
                    identical = false;
                    break;
                }
            }
        }
        
        String area = findAreaForCell(cellid);
        areas.put(area, moveCellRecursive(areas.get(area), cellid, moveUp));
        
        if(identical) {
            mobileTmpList = new LinkedList();
            buildMobileList(areas.get("body"), true);
            mobileList = mobileTmpList;
        }
    }
    
    public void addToList(PageCell cell, String area) {
        if (area == null || area.isEmpty()) {
            area = "body";
        }
        if (areas.get(area) == null) {
            areas.put(area, new ArrayList());
        }

        areas.get(area).add(cell);
    }

    public void removeCellFromList(PageCell cell) {
        for (String area : areas.keySet()) {
            areas.get(area).remove(cell);
        }
    }

    public void setNewList(ArrayList<PageCell> newList, String area, boolean force) {
        if (area == null || area.isEmpty()) {
            area = "body";
        }

        if (areas.get(area) != null && !force) {
            areas.get(area).clear();
            areas.get(area).addAll(newList);
        } else {
            areas.put(area, newList);
        }
    }

    private Iterable<PageCell> getAreaList(String area) {
        ArrayList<PageCell> list = areas.get(area);
        if (list == null) {
            return new ArrayList();
        }
        return list;
    }

    private ArrayList<PageCell> getAllCells() {
        ArrayList<PageCell> cells = new ArrayList();
        for (String area : areas.keySet()) {
            ArrayList<PageCell> areastoadd = areas.get(area);
            if(areastoadd != null) {
                cells.addAll(areastoadd);
            }
        }
        return cells;
    }

    public String createCell(String incell, String before, String mode, String area) throws ErrorException {

        if (mode == null || mode.isEmpty()) {
            throw new ErrorException(1030);
        }
        String cellId = "";
        if (incell == null || incell.isEmpty()) {
            PageCell newpagecell = initNewCell(mode);
            newpagecell.mode = mode;
            if (before != null && !before.isEmpty()) {
                ArrayList<PageCell> newList = new ArrayList();
                area = findAreaForCell(before);
                for (PageCell cell : getAreaList(area)) {
                    if (cell.cellId.equals(before)) {
                        newList.add(newpagecell);
                    }
                    newList.add(cell);
                }
                setNewList(newList, area, false);
            } else {
                addToList(newpagecell, area);
            }
            cellId = newpagecell.cellId;
        } else {
            PageCell cell = findCell(getAllCells(), incell);
            double newwidth = -1;
            if (cell.cells.isEmpty() && !cell.isTab() && !cell.isRotating()) {
                if(!PageCell.CellType.floating.equals(mode)) {
                    PageCell newcell = cell.createCell(before);
                    newcell.extractDataFrom(cell, false);
                    newcell.mode = mode;
                    newcell.type = cell.type;
                }
            } else {
                if (before != null && !before.isEmpty()) {
                    mode = findCell(getAllCells(), before).mode;
                }

                    //Each cell as a subcell need to be the same.
                if (!cell.cells.isEmpty() && !cell.cells.get(0).mode.equals(mode)) {
                    PageCell newcell = initNewCell(mode);
                    newcell.extractDataFrom(cell, true);
                    cell.clear();
                    cell.mode = mode;
                    cell.cells.add(newcell);
                }
            }
            
            if (mode.equals(PageCell.CellMode.column)) {
                int count = cell.cells.size();
                double percentage = (double) ((100 / count) + 100) / 100;
                newwidth = resizeCells(cell.cells, true, percentage);
                cell.width = newwidth;
            }

            PageCell newcell = cell.createCell(before);
            if(cell.isRotating()) {
                newcell.type = PageCell.CellType.floating;
                newcell.hideOnMobile = true;
            }
            newcell.mode = mode;
            newcell.width = newwidth;
            cellId = newcell.cellId;
        }

        finalizeMobileList();
        
        return cellId;
    }

    private PageCell findCell(ArrayList<PageCell> cells, String id) {
        for (PageCell cell : cells) {
            if (cell.cellId.equals(id)) {
                return cell;
            }
            if (!cell.cells.isEmpty()) {
                PageCell foundcell = findCell(cell.cells, id);
                if (foundcell != null) {
                    return foundcell;
                }
            }
        }
        return null;
    }

    public void deleteCell(String cellId) {
        deleteCellRecusive(cellId, getAllCells());
        finalizeMobileList();
    }

    private boolean deleteCellRecusive(String cellId, ArrayList<PageCell> cells) {
        PageCell toRemove = null;
        for (PageCell cell : cells) {
            if (cell.cellId.equals(cellId)) {
                toRemove = cell;
            } else if (cell.cells.size() > 0) {
                boolean deleted = deleteCellRecusive(cellId, cell.cells);
                if (deleted) {
                    if (cell.cells.size() == 1 && !cell.type.equals(PageCell.CellType.floating) && !cell.isRotating()) {
                        String currentMode = cell.mode;
                        if(!cell.isRotating() && !cell.isTab()) {
                            cell.extractDataFrom(cell.cells.get(0), true);
                                cell.mode = currentMode;
                            }
                        }
                    if (cell.cells.isEmpty() && (cell.isTab())) {
                        cell.mode = PageCell.CellMode.row;
                    }
                }
            }
        }
        if (toRemove != null) {
            cells.remove(toRemove);
            double percentage = (double) (toRemove.width / (double) (100 - toRemove.width)) + 1;
            resizeCells(cells, false, percentage);
            removeCellFromList(toRemove);
            return true;
        }
        return false;
    }

    public PageCell getCell(String pageCellId) {
        ArrayList<PageCell> flatCellList = getCellsFlatList();
        for (PageCell cell : flatCellList) {
            PageCell cell4 = cell.getCell(pageCellId);
            if (cell4 != null) {
                return cell4;
            }
        }

        return null;
    }

    public void removeAppFromCell(String cellid) {
        ArrayList<PageCell> cells = getAllCells();
        removeAppRecursivly(cellid, cells);
    }

    private void removeAppRecursivly(String cellid, ArrayList<PageCell> cells) {
        for (PageCell cell : cells) {
            if (cell.cellId.equals(cellid)) {
                cell.appId = null;
            } else if (cell.cells.size() > 0) {
                removeAppRecursivly(cellid, cell.cells);
            }
        }
    }

    public void updateStyle(String cellId, String styles, Double width, String innerStyles) {
        PageCell cell = findCell(getAllCells(), cellId);
        if (cell == null) {
            return;
        }

        if (styles != null && !styles.equals("notset")) {
            cell.styles = styles;
        }
        if (width > 0) {
            cell.width = width;
        }
    }

    private ArrayList<PageCell> moveCellRecursive(ArrayList<PageCell> cells, String cellid, boolean moveUp) {
        for (PageCell cell : cells) {
            if (cell.cellId.equals(cellid)) {
                return moveCellInList(cells, moveUp, cellid);
            }
            if (cell.cells.size() > 0) {
                cell.cells = moveCellRecursive(cell.cells, cellid, moveUp);
            }
        }
        return cells;
    }

    private ArrayList<PageCell> moveCellInList(ArrayList<PageCell> cells, boolean moveUp, String cellId) {
        ArrayList<PageCell> newCellList = new ArrayList();
        PageCell toAdd = null;
        if (moveUp) {
            for (PageCell cell : cells) {
                if (cell.cellId.equals(cellId)) {
                    newCellList.add(cell);
                    continue;
                }
                if (toAdd != null) {
                    newCellList.add(toAdd);
                }
                toAdd = cell;
            }
            if (toAdd != null) {
                newCellList.add(toAdd);
            }
        } else {
            for (PageCell cell : cells) {
                if (cell.cellId.equals(cellId)) {
                    toAdd = cell;
                    continue;
                }
                newCellList.add(cell);
                if (toAdd != null) {
                    newCellList.add(toAdd);
                    toAdd = null;
                }
            }
            if (toAdd != null) {
                newCellList.add(toAdd);
                toAdd = null;
            }
        }

        return newCellList;
    }

    private PageCell findParent(PageCell cell) {
        return findParentRecursive(getAllCells(), cell);
    }

    private PageCell findParentRecursive(ArrayList<PageCell> allCells, PageCell cell) {
        for (PageCell tmpcell : allCells) {
            if (tmpcell.cells.contains(cell)) {
                return tmpcell;
            } else {
                if (!tmpcell.cells.isEmpty()) {
                    PageCell found = findParentRecursive(tmpcell.cells, cell);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    public void setCarouselConfig(String cellId, CarouselConfig config) {
        PageCell cell = findCell(getAllCells(), cellId);
        cell.carouselConfig = config;
    }

    private String findAreaForCell(String cellId) {
        for (String area : areas.keySet()) {
            PageCell cell = findCell(areas.get(area), cellId);
            if (cell != null) {
                return area;
            }
        }
        return null;
    }

    public LinkedList<PageCell> getCellsInBodyFlatList() {
        LinkedList<PageCell> arrayList = new LinkedList();
        List<PageCell> cells = areas.get("body");
        if (cells != null) {
            for (PageCell row : areas.get("body")) {
                arrayList.addAll(row.getCellsFlatList());
            }
        }
        return arrayList;
    }
    
    public ArrayList<PageCell> getCellsFlatList() {
        ArrayList<PageCell> arrayList = new ArrayList();
        for (String area : areas.keySet()) {
            if(areas != null && areas.get(area) != null) {
                for (PageCell row : areas.get(area)) {
                    arrayList.addAll(row.getCellsFlatList());
                    if(row.back != null) {
                        arrayList.add(row.back);
                    }
                }
            }
        }
        return arrayList;
    }

    private double resizeCells(ArrayList<PageCell> cells, boolean add, double percentage) {
        double total = 0;
        for (PageCell tmpcell : cells) {
            if (tmpcell.width > -1) {
                if (percentage == 0) {
                    tmpcell.width = -1.0;
                } else {
                    if (add) {
                        tmpcell.width = tmpcell.width / percentage;
                    } else {
                        tmpcell.width = tmpcell.width * percentage;
                    }
                    tmpcell.width = (double) Math.round(tmpcell.width);
                    total += tmpcell.width;
                }
            }
        }
        double returning = 100 - total;
        if (returning == 100) {
            return -1;
        }
        if (!add && returning > 0) {
            cells.get(cells.size() - 1).width += returning;
        }
        return returning;
    }

    public void resetMobileList() {
        flatMobileList = false;
        mobileList = new LinkedList();
        buildMobileList(areas.get("body"), false);
    }
    
    private PageCell initNewCell(String mode) {
        PageCell cell = new PageCell();
        cell.mode = mode;
        return cell;
    }

    public void setMode(String cellId, String mode) {
        PageCell cell = getCell(cellId);
        cell.mode = mode;
    }

    public void cellName(String cellId, String cellName) {
        PageCell cell = getCell(cellId);
        cell.cellName = cellName;
    }

    public void switchMode(String cellId, String mode) {
        PageCell cell = getCell(cellId);
        if (cell.mode.equals(mode)) {
            return;
        }

        if (PageCell.CellMode.rotating.equals(mode) || PageCell.CellMode.tab.equalsIgnoreCase(mode)) {
            boolean convertToSub = true;
            if (cell.isRotating() || cell.isTab()) {
                if (!cell.cells.isEmpty()) {
                    PageCell innercell = cell.cells.get(0);
                    if (!innercell.isTab() && !innercell.isRotating()) {
                        convertToSub = false;
                    }
                }
                
            }
            if (convertToSub) {
                PageCell subcell = initNewCell(PageCell.CellMode.row);
                subcell.extractDataFrom(cell, true);
                if(subcell.mode.equals(PageCell.CellMode.init)) {
                    subcell.mode = PageCell.CellMode.row;
                }
                subcell.styles = cell.styles;
                if(PageCell.CellMode.rotating.equals(mode))
                    subcell.type = PageCell.CellType.floating;
                cell.clear();
                subcell.hideOnMobile = true;
                cell.cells.add(subcell);
            }

        }

        cell.mode = mode;

    }

    public void addApplicationToFirstFreeBodyCell(String appId) {
        List<PageCell> cells =  areas.get("body");
        if (cells == null || cells.size() == 0) {
            return;
        }
        
        cells.iterator().next().appId = appId;
    }
    
    public void updateMobileList() {
        if(mobileList.isEmpty()) {
            buildMobileList(areas.get("body"), false);
        }
    }
    
    private void buildMobileList(ArrayList<PageCell> cells, Boolean tmpList) {
        for(PageCell cell : cells) {
            if(!cell.cells.isEmpty() && !cell.isRotating() && !cell.isTab() && flatMobileList) {
                buildMobileList(cell.cells, tmpList);
            } else {
                if(tmpList) {
                    mobileTmpList.add(cell.cellId);
                } else {
                    mobileList.add(cell.cellId);
                }
            }
        }
    }

    public boolean needMobileList() {
        return mobileList.isEmpty();
    }

    public void moveCellMobile(String cellId, Boolean moveUp) {
        updateMobileList();
        
        int pos = mobileList.indexOf(cellId);
        if(moveUp) {
            if(pos-1 >= 0) {
                Collections.swap(mobileList, pos, pos-1);
            }
        } else {
            if((pos+1) < mobileList.size()-1) {
                Collections.swap(mobileList, pos, pos+1);
            }
        }
    }

    public void toggleHiddenOnMobile(String cellId, boolean hidden) {
        PageCell cell = getCell(cellId);
        cell.hideOnMobile = hidden;
    }

    private void updateMobileListWithEntry(String cellId) {
       
       if(!mobileTmpList.contains(cellId)) {
           return;
       }
       
       int index = mobileTmpList.indexOf(cellId);
       if(index == 0) {
           mobileList.add(0, cellId);
           return;
       }
       
       String aboveCellId = mobileTmpList.get(index-1);
       int newIndex = mobileList.indexOf(aboveCellId);
       if(mobileList.contains(cellId)) {
           return;
       }
       mobileList.add(newIndex+1, cellId);
       
    }

    public void clearMobileList() {
        mobileList = new LinkedList();
    }

    private void finalizeMobileList() {
        mobileTmpList = new LinkedList();
        buildMobileList(areas.get("body"), true);
        
        //Any elements been removed?
        List<String> toRemove = new ArrayList();
        mobileList.stream().filter((cellId) -> (!mobileTmpList.contains(cellId))).forEach((cellId) -> {
            toRemove.add(cellId);
        });
        mobileList.removeAll(toRemove);
        
        //Any elemts forgotten?
        mobileTmpList.stream().filter((cellId) -> (!mobileList.contains(cellId))).forEach((cellId) -> {
            updateMobileListWithEntry(cellId);
        });
        
        mobileTmpList = null;
    }

    public void togglePinArea(String cellId) {
        PageCell cell = getCell(cellId);
        cell.floatingData.pinned = !cell.floatingData.pinned;
    }

    public List<PageCell> getCells(String area) {
        return areas.get(area);
    }

    PageLayout jsonClone() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), PageLayout.class);
    }

    List<PageCell> getMobileBody() {
        if(mobileList.isEmpty()) {
            return areas.get("body");
        }
        
        List<PageCell> cells = new LinkedList();
        for(String cellId : mobileList) {
            PageCell cell = getCell(cellId);
            if(cell != null) {
                cells.add(cell);
            }
        }
        return cells;
    }

    void flattenMobileLayout() {
        flatMobileList = true;
        mobileList = new LinkedList();
        buildMobileList(areas.get("body"), false);
    }

    PageCell getParent(String cellId) {
        for(ArrayList<PageCell> cell : areas.values()) {
            PageCell result = findParent(cell, cellId);
            if(result != null) {
                if(result.cellId.equals(cellId)) {
                    return null;
                }
                return result;
            }
        }
        return null;
    }

    private PageCell findParent(ArrayList<PageCell> cells, String cellId) {
        for(PageCell cell : cells) {
            if(cell.cellId.equals(cellId)) {
                return cell;
            } else {
                PageCell result = findParent(cell.cells, cellId);
                if(result != null) {
                    if(result.cellId.equals(cellId)) {
                        return cell;
                    } else {
                        return result;
                    }
                }
               
            }
        }
        return null;
    }

    /**
     * Row inside of row is not allowed and should not happen. Same with column in column.
     */
    public void checkAndFixDoubles() {
        for(ArrayList<PageCell> allCells : areas.values()) {
            checkAndFixDoublesOnCells(allCells);
        }
    }

    private void checkAndFixDoublesOnCells(ArrayList<PageCell> allCells) {
        for(PageCell cell : allCells) {
            if(cell.cells.size() > 0) {
                PageCell subcell = cell.cells.get(0);
                if(subcell.mode.equals(cell.mode)) {
                    if(subcell.mode.equals(PageCell.CellMode.column)) {
                        PageCell newCell = initNewCell(PageCell.CellMode.row);
                        newCell.cells.addAll(cell.cells);
                        cell.cells.clear();
                        cell.cells.add(newCell);
                    }
                    if(subcell.mode.equals(PageCell.CellMode.row)) {
                        PageCell newCell = initNewCell(PageCell.CellMode.column);
                        newCell.cells.addAll(cell.cells);
                        cell.cells.clear();
                        cell.cells.add(newCell);
                    }
                }
                checkAndFixDoublesOnCells(cell.cells);
            }
        }
    }

    void finalizeLayout() {
        ArrayList<PageCell> allCells = getCellsFlatList();
        for(PageCell cell : allCells) {
            cell.finalizeCell();
        }
    }

}

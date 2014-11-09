package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;

public class PageLayout implements Serializable {

    HashMap<String, ArrayList<PageCell>> areas = new HashMap();

    void clear() {
        areas.put("body", new ArrayList());
    }

    public void moveCell(String cellid, boolean moveUp) {
        String area = findAreaForCell(cellid);
        moveCellRecursive(areas.get(area), cellid, moveUp);
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
            cells.addAll(areas.get(area));
        }
        return cells;
    }

    public String createCell(String incell, String before, String direction, String area) {

        if (direction == null || direction.isEmpty()) {
            direction = PageCell.PageDirection.vertical;
        }
        String cellId = "";
        if (incell == null || incell.isEmpty()) {
            PageCell newpagecell = new PageCell();
            newpagecell.direction = direction;
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
            if (direction.equals(PageCell.PageDirection.rotating)) {
                incell = denyRotatingInsideRotating(incell);
            }
            String directionToSet = direction;
            PageCell cell = findCell(getAllCells(), incell);
            double newwidth = -1;
            if (cell.cells.isEmpty()) {
                PageCell newcell = cell.createCell(before);
                newcell.direction = directionToSet;
                newcell.innerStyles = cell.innerStyles;
                newcell.styles = cell.styles;
                cell.styles = "";
                cell.innerStyles = "";
                newcell.appId = cell.appId;
            } else {
                int count = cell.cells.size();
                double percentage = (double)((100 / count) + 100) / 100;
                newwidth = resizeCells(cell.cells, true, percentage);
                
                if (!directionToSet.equals(cell.cells.get(0).direction) && (before == null || before.isEmpty())) {
                    PageCell newpagecell = new PageCell();
                    newpagecell.direction = directionToSet;
                    newpagecell.cells.addAll(cell.cells);
                    newpagecell.styles = cell.styles;
                    newpagecell.innerStyles = cell.innerStyles;
                    newpagecell.width = newwidth;
                    cell.styles = "";
                    cell.innerStyles = "";
                    
                    cell.cells.clear();
                    cell.cells.add(newpagecell);
                } else {
                    directionToSet = cell.cells.get(0).direction;
                }
            }

            PageCell newcell = cell.createCell(before);
            newcell.direction = directionToSet;
            newcell.width = newwidth;
            cellId = newcell.cellId;
        }
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
    }

    private boolean deleteCellRecusive(String cellId, ArrayList<PageCell> cells) {
        PageCell toRemove = null;
        for (PageCell cell : cells) {
            if (cell.cellId.equals(cellId)) {
                toRemove = cell;
            } else if (cell.cells.size() > 0) {
                boolean deleted = deleteCellRecusive(cellId, cell.cells);
                if (deleted) {
                    if (cell.cells.size() == 1) {
                        cell.appId = cell.cells.get(0).appId;
                        cell.cells.remove(0);
                    }
                }
            }
        }
        if (toRemove != null) {
            cells.remove(toRemove);
            double percentage = (double)(toRemove.width / (double)(100-toRemove.width))+1;
            resizeCells(cells, false, percentage);
            removeCellFromList(toRemove);
            return true;
        }
        return false;
    }

    public PageCell getCell(String pageCellId) {
        for (PageCell cell : getAllCells()) {
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
        if (innerStyles != null && !innerStyles.equals("notset")) {
            cell.innerStyles = innerStyles;
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

    private String denyRotatingInsideRotating(String incell) {
        PageCell cell = findCell(getAllCells(), incell);
        if (cell.direction.equals(PageCell.PageDirection.rotating)) {
            PageCell parent = findParent(cell);
            if (parent == null) {
                return incell;
            }
            do {
                if (!parent.equals(PageCell.PageDirection.rotating)) {
                    return parent.cellId;
                }
                parent = findParent(parent);
            } while (parent != null);
        }
        return incell;
    }

    private PageCell findParent(PageCell cell) {
        return findParentRecursive(getAllCells(), cell);
    }

    private PageCell findParentRecursive(ArrayList<PageCell> allCells, PageCell cell) {
        for (PageCell tmpcell : allCells) {
            if (tmpcell.cells.contains(cell)) {
                return tmpcell;
            } else {
                if(!tmpcell.cells.isEmpty()) {
                    PageCell found = findParentRecursive(tmpcell.cells, cell);
                    if(found != null) {
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

    public ArrayList<PageCell> getCellsFlatList() {
        ArrayList<PageCell> arrayList = new ArrayList();
        for(String area : areas.keySet()) {
            for (PageCell row : areas.get(area)) {
                arrayList.addAll(row.getCellsFlatList());
            }
        }
        return arrayList;
    }

    private double resizeCells(ArrayList<PageCell> cells, boolean add, double percentage) {
        double total = 0;
        for(PageCell tmpcell : cells) {
            if(tmpcell.width > -1) {
                if(percentage == 0) {
                    tmpcell.width = -1.0;
                } else {
                    if(add) {
                        tmpcell.width = tmpcell.width / percentage;
                    } else {
                        tmpcell.width = tmpcell.width * percentage;
                    }
                    tmpcell.width = (double)Math.round(tmpcell.width);
                    total += tmpcell.width;
                }
            }
        }
        double returning = 100 - total;
        System.out.println("Returning: " + returning);
        System.out.println("Total: " + total);
        if(returning == 100) {
            return -1;
        }
        if(!add && returning > 0) {
            cells.get(cells.size()-1).width += returning;
        }
        return returning;
    }

}

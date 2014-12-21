package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.ErrorException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;

public class PageLayout implements Serializable {

    HashMap<String, ArrayList<PageCell>> areas = new HashMap();

    void clear() {
        areas.put("body", new ArrayList());
        areas.get("body").add(initNewCell(PageCell.PageMode.row));
    }

    public void moveCell(String cellid, boolean moveUp) {
        String area = findAreaForCell(cellid);
        areas.put(area, moveCellRecursive(areas.get(area), cellid, moveUp));
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
            if (cell.cells.isEmpty()) {
                PageCell newcell = cell.createCell(before);
                newcell.extractDataFrom(cell, false);
                newcell.mode = mode;
            } else {
                if (before != null && !before.isEmpty()) {
                    mode = findCell(getAllCells(), before).mode;
                }

                    //Each cell as a subcell need to be the same.
                if (!cell.cells.get(0).mode.equals(mode)) {
                    PageCell newcell = initNewCell(mode);
                    newcell.extractDataFrom(cell, true);
                    cell.clear();
                    
                    cell.cells.add(newcell);
                }

            }
            
            if (mode.equals(PageCell.PageMode.column)) {
                int count = cell.cells.size();
                double percentage = (double) ((100 / count) + 100) / 100;
                newwidth = resizeCells(cell.cells, true, percentage);
                cell.width = newwidth;
            }

            PageCell newcell = cell.createCell(before);
            newcell.mode = mode;
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
                        String currentMode = cell.mode;
                        cell.extractDataFrom(cell.cells.get(0), true);
                        cell.mode = currentMode;
                    }
                    if (cell.cells.isEmpty() && (cell.isRotating() || cell.isTab())) {
                        cell.mode = PageCell.PageMode.row;
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

    private String denyRotatingInsideRotating(String incell, String denymode) {
        PageCell cell = findCell(getAllCells(), incell);
        if (cell.mode.equals(denymode)) {
            PageCell parent = findParent(cell);
            if (parent == null) {
                return incell;
            }
            do {
                if (!parent.equals(denymode)) {
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

    public ArrayList<PageCell> getCellsFlatList() {
        ArrayList<PageCell> arrayList = new ArrayList();
        for (String area : areas.keySet()) {
            for (PageCell row : areas.get(area)) {
                arrayList.addAll(row.getCellsFlatList());
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

        if (PageCell.PageMode.rotating.equals(mode) || PageCell.PageMode.tab.equalsIgnoreCase(mode)) {
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
                PageCell subcell = initNewCell(PageCell.PageMode.row);
                subcell.extractDataFrom(cell, true);
                subcell.styles = cell.styles;
                cell.clear();
                cell.cells.add(subcell);
            }

        }

        cell.mode = mode;

    }

}

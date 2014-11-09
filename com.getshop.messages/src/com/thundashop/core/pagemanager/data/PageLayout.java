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
        setNewList(moveCellRecursive(areas.get(area), cellid, moveUp), area, false);
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
            if (cell.cells.isEmpty()) {
                PageCell newcell = cell.createCell(before);
                newcell.direction = directionToSet;
                newcell.appId = cell.appId;
            } else {
                cell.cells.stream().forEach((cell2) -> {
                    cell2.width = -1.0;
                });
                if (!directionToSet.equals(cell.cells.get(0).direction) && (before == null || before.isEmpty())) {
                    PageCell newpagecell = new PageCell();
                    newpagecell.direction = directionToSet;
                    newpagecell.cells.addAll(cell.cells);
                    cell.cells.clear();
                    cell.cells.add(newpagecell);
                } else {
                    directionToSet = cell.cells.get(0).direction;
                }
            }

            PageCell newcell = cell.createCell(before);
            newcell.direction = directionToSet;
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
                findParentRecursive(tmpcell.cells, cell);
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

}

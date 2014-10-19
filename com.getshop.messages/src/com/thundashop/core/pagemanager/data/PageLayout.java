package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class PageLayout implements Serializable {

    public PageCell header;
    public PageCell footer;
    public LinkedList<PageCell> rows = new LinkedList();

    void clear() {
        rows = new LinkedList();
    }

    public void moveCell(String cellid, boolean moveUp) {
        rows = moveCellRecursive(rows, cellid, moveUp);
        header.cells = moveCellRecursive(header.cells, cellid, moveUp);
        footer.cells = moveCellRecursive(footer.cells, cellid, moveUp);
    }

    public void createCell(String incell, String before, boolean vertical) {
        if (incell == null || incell.isEmpty()) {
            PageCell newpagecell = new PageCell();
            newpagecell.vertical = vertical;
            if (before != null && !before.isEmpty()) {
                LinkedList<PageCell> newList = new LinkedList();
                for (PageCell cell : rows) {
                    if (cell.cellId.equals(before)) {
                        newList.add(newpagecell);
                    }
                    newList.add(cell);
                }
                rows = newList;
            } else {
                rows.add(newpagecell);
            }
        } else {
            PageCell cell = findCell(getAllCells(), incell);
            if (cell.cells.isEmpty()) {
                PageCell newcell = cell.createCell(before);
                newcell.vertical = vertical;
                newcell.appId = cell.appId;
                before = newcell.cellId;
            }

            PageCell newcell = cell.createCell(before);
            newcell.vertical = vertical;
        }
    }

    private PageCell findCell(LinkedList<PageCell> cells, String id) {
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

    private boolean deleteCellRecusive(String cellId, LinkedList<PageCell> cells) {
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
            this.rows.remove(toRemove);
            return true;
        }
        return false;
    }

    private LinkedList<PageCell> getAllCells() {
        LinkedList<PageCell> cells = new LinkedList();
        cells.addAll(this.rows);
        cells.add(header);
        cells.add(footer);
        return cells;
    }

    public PageCell getCell(String pageCellId) {
        if (header != null && header.cellId.equals(pageCellId)) {
            return header;
        }

        if (footer != null && footer.cellId.equals(pageCellId)) {
            return footer;
        }

        if (header != null) {
            PageCell cell2 = header.getCell(pageCellId);
            if (cell2 != null) {
                return cell2;
            }
        }

        if (footer != null) {
            PageCell cell3 = footer.getCell(pageCellId);
            if (cell3 != null) {
                return cell3;
            }
        }

        for (PageCell cell : rows) {
            PageCell cell4 = cell.getCell(pageCellId);
            if (cell4 != null) {
                return cell4;
            }
        }

        return null;
    }

    public void removeAppFromCell(String cellid) {
        LinkedList<PageCell> cells = getAllCells();
        removeAppRecursivly(cellid, cells);
    }

    private void removeAppRecursivly(String cellid, LinkedList<PageCell> cells) {
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
        if(width > 0) {
            cell.width = width;
        }
    }

    private LinkedList<PageCell> moveCellRecursive(LinkedList<PageCell> cells, String cellid, boolean moveUp) {
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

    private LinkedList<PageCell> moveCellInList(LinkedList<PageCell> cells, boolean moveUp, String cellId) {
        LinkedList<PageCell> newCellList = new LinkedList();
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

}

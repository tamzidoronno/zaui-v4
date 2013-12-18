package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RowLayout implements Serializable {
    int numberOfCells = 3;
    int marginBottom = 0;
    int marginTop = 0;
    List<Double> rowWidth = new ArrayList();
    String rowId = "";
}
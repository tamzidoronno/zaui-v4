package com.powerofficego.data;

import java.util.List;

public class SalesOrderTransfer {
    boolean isPosted = false;
    public List<PowerOfficeGoSalesOrder> salesOrders;
    public List<PowerOfficeGoImportLine> importLines;
    public Integer importType = 1;
}

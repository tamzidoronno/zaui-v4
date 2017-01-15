package com.powerofficego.data;

import java.util.Date;
import java.util.List;

public class SalesOrderTransfer {
    public List<PowerOfficeGoSalesOrder> salesOrders;
    public List<PowerOfficeGoImportLine> importLines;
    public Integer type = 1;
    public Date date = new Date();
    public String description = "";
    
}


package com.powerofficego.data;

import java.util.List;

public class PowerOfficeGoSalesOrder {
    public Integer customerCode;
    public boolean mergeWithPreviousOrder = false;
    public Integer orderNo;
    public String reference;
    public String departmentCode;
    public List<PowerOfficeGoSalesOrderLines> salesOrderLines;
}

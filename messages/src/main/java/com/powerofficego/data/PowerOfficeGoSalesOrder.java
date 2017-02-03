
package com.powerofficego.data;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.util.ArrayList;
import java.util.List;

public class PowerOfficeGoSalesOrder {
    public Integer customerCode;
    public boolean mergeWithPreviousOrder = false;
    public Integer orderNo;
    public String reference;
    public List<PowerOfficeGoSalesOrderLines> salesOrderLines;
}

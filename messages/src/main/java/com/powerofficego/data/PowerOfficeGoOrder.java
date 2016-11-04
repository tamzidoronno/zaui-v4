
package com.powerofficego.data;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.util.ArrayList;
import java.util.List;

public class PowerOfficeGoOrder {
    public Integer customerCode;
    public boolean mergeWithPreviousOrder = false;
    public Integer orderNo;
    public List<PowerOfficeGoSalesOrderLines> salesOrderLines;
}

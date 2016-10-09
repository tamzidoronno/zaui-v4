
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class NewOrderFilter implements Serializable {
    public Date endInvoiceAt = null;
    public String pmsRoomId = "";
    public boolean onlyEnded = false;
    public boolean prepayment = false;
    public boolean forceInvoicing = false;
    public boolean autoGeneration = false;
    public boolean avoidOrderCreation = false;
    public Date maxAutoCreateDate = null;
    public Integer increaseUnits = -1;
    public Integer prepaymentDaysAhead = -1;
    public boolean createNewOrder = false;
    public boolean fromAdministrator = false;
    public String addToOrderId = "";
}

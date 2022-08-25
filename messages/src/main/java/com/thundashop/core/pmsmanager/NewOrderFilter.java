
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewOrderFilter implements Serializable {
    public Date endInvoiceAt = null;
    public Date startInvoiceAt = null;
    public String pmsRoomId = "";
    public List<String> pmsRoomIds = new ArrayList<>();
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
    public boolean ignoreCheckChangesInBooking = false;
    public String paymentType = "";
    public List<String> itemsToCreate = new ArrayList<>();
    public String userId = "";
    public Double totalAmount = null;
    public Date chargeCardAfter = null;
    boolean avoidClearingCart = false;
}

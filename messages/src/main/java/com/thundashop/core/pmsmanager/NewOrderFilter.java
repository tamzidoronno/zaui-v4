
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class NewOrderFilter implements Serializable {
    public Date startInvoiceFrom = null;
    public Date endInvoiceAt = null;
    public String itemId = "";
}

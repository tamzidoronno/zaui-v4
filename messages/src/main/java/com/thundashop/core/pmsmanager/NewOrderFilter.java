
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class NewOrderFilter implements Serializable {
    public Integer numberOfMonths = 1;
    public Date startInvoiceFrom = null;
}

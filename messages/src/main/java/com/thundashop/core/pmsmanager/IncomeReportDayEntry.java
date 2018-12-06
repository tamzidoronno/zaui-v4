
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 *
 * @author boggi
 */
public class IncomeReportDayEntry implements Serializable {
    public Date day;
    public LinkedHashMap<String, BigDecimal> products = new LinkedHashMap();
    public Double total;
}

package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

public class SavedOrderFile extends DataCommon {
    public List<AccountingTransaction> accountingTransactionLines = new ArrayList();
    public List<String> result;
    public String type = "accounting";
    public boolean transferred = false;
    public Date startDate;
    public Date endDate;
    public String subtype = ""; 
    public Double amountEx = 0.0;
    public Double amountInc = 0.0;
    public Double amountExDebet = 0.0;
    public Double amountIncDebet = 0.0;
    public Double sumAmountExOrderLines = 0.0;
    public Double sumAmountIncOrderLines = 0.0;
    public double onlyPositiveLinesEx = 0.0;
    public double onlyPositiveLinesInc = 0.0;
    
    public HashMap<String, Double> amountOnOrder = new HashMap();
    public List<String> tamperedOrders = new ArrayList();
    public List<String> orders = new ArrayList();
    public List<String> ordersTriedButFailed = new ArrayList();
    public String configId = "";
    public String transferId;
    public ArrayList ordersNow = new ArrayList();
    Date lastFinalized;
    public String base64Excel;
    
    public boolean needFinalize() {
        if(lastFinalized == null) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(lastFinalized);
        int dayOfYear2 = cal.get(Calendar.DAY_OF_YEAR);
        return dayOfYear2 != dayOfYear;
    }
    
    @Transient
    public Integer numberOfOrdersNow = 0;
}

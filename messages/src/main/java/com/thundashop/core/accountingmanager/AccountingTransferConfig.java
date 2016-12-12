package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;
import java.util.List;

public class AccountingTransferConfig extends DataCommon {
    public Integer delay;
    public Integer includeUsers;
    public List<AccountingTransferConfigTypes> paymentTypes;
    public TransferFtpConfig ftp;
    public String transferType;
    public String orderFilterPeriode = "";
    public String subType = "orders";
    public HashMap<String, String> paymentTypeCustomerIds = new HashMap();
    public String username;
    public String password;
    public Integer startCustomerCodeOffset;
}

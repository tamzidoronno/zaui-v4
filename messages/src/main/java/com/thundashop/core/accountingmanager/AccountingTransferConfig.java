package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.List;

public class AccountingTransferConfig extends DataCommon {
    public Integer delay;
    public Integer includeUsers;
    public List<AccountingTransferConfigTypes> paymentTypes;
    public TransferFtpConfig ftp;
    public String transferType;
    public String subType = "orders";
}

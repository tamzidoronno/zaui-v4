/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.google.gson.Gson;
import com.getshop.scope.GetShopSession;
import com.powerofficego.data.AccessToken;
import com.powerofficego.data.ApiOrderTransferResponse;
import com.powerofficego.data.PowerOfficeGoImportLine;
import com.powerofficego.data.PowerOfficeGoSalesOrder;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.ordermanager.data.Order;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.powerofficego.data.SalesOrderTransfer;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.productmanager.data.AccountingDetail;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PowerOfficeGoPrimitiveAccounting extends AccountingSystemBase {

    private String token;
    
    @Autowired
    MessageManager messageManager;

    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end) {
        return new ArrayList();
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.POWEROFFICEGOPRIMITIVE_API;
    }

    @Override
    public String getSystemName() {
        return "Power Office Go Primitive API";
    }

    @Override
    public void handleDirectTransfer(String orderId) {
        
    }
    
    @Override
    public HashMap<String, String> getConfigOptions() {
        HashMap<String, String> ret = new HashMap();
        ret.put("password", "PowerOfficeGo Application Key");
        ret.put("department", "PowerOfficeGo Department");
        return ret;
    }

    @Override
    boolean isUsingProductTaxCodes() {
        return true;
    }

    @Override
    boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean supportDirectTransfer() {
        return true;
    }

    @Override
    public void transfer(List<DayIncome> incomes) {
        createAccessToken();

        int i = 0;
        for (DayIncome income : incomes) {    
            i++;
            transferIncomeData(income, i);
        }
    }
    private String createAccessToken() {
        try {
            Gson gson = new Gson();
            String auth = "e5fdab3b-97b5-4041-bddb-5b2a48ccee1c:" + getConfig("password");
            String res = webManagerSSL.htmlPostBasicAuth("https://api.poweroffice.net/OAuth/Token", "grant_type=client_credentials", false, "UTF-8", auth);
            AccessToken atoken = gson.fromJson(res, AccessToken.class);
            token = atoken.access_token;
            return atoken.access_token;
        }catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private String transferIncomeData(DayIncome income, int number) {
        String endpoint = "http://api.poweroffice.net/Import/";
        List<PowerOfficeGoImportLine> importLinesToTransfer = new ArrayList();

        SalesOrderTransfer transferObject = new SalesOrderTransfer();
        transferObject.description = "getshop file for: " +income.start + " - " + income.end;
        transferObject.importLines = createImportLines(income, number);
        transferObject.type = 99;
        Gson gson = new Gson();
        String data = gson.toJson(transferObject);
        try {
            String result = webManagerSSL.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, "POST");
            ApiOrderTransferResponse resp = gson.fromJson(result, ApiOrderTransferResponse.class);
            if(resp.success) {
                addToLog("Sucesfully uploaded data");
            } else {
                /* @TODO HANDLE PROPER WARNING */
                addToLog("Failed to transfer customer: " + result);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<PowerOfficeGoImportLine> createImportLines(DayIncome income, int number) {
        logPrint("================ Day : " + income.start + " - " + income.end + " ============");
        BigDecimal zeroCheck = new BigDecimal(BigInteger.ZERO);

        Map<String, List<DayEntry>> groupedIncomes = income.getGroupedByAccountExTaxes();
        List<PowerOfficeGoImportLine> result = new ArrayList();
        for (String accountingNumber : groupedIncomes.keySet()) {
            Map<String, List<DayEntry>> dayEntriesGroupedByBatchId = groupedIncomes.get(accountingNumber)
                    .stream()
                    .collect(Collectors.groupingBy(o -> {
                        return o.batchId;
                    }));
            
            for (String batchId : dayEntriesGroupedByBatchId.keySet()) {
                BigDecimal total = dayEntriesGroupedByBatchId.get(batchId)
                        .stream()
                        .map(o -> o.amount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                zeroCheck = zeroCheck.add(total);

                logPrint("To be transfered on account: " + accountingNumber + ", total: " + total);
                PowerOfficeGoImportLine toAdd = new PowerOfficeGoImportLine();

                if (accountingNumber.length() == 4) {
                    toAdd.accountNumber = new Integer(accountingNumber);
                } else if(accountingNumber.equals("")){
                    logPrint("There is an already created DayIncome in db which contains empty account parameter. Transfer to accounting/central might fail. Find it in OrderManager: className:/DayIncomeReport/, 'incomes.dayEntries.accountingNumber': ''... Income.id: " + income.id);
                    toAdd.accountNumber = new Integer(accountingNumber);
                } else {
                    toAdd.customerCode = new Integer(accountingNumber);
                }

                toAdd.postingDate = income.start;
                toAdd.amount = total.doubleValue();
                toAdd.documentNumber = number;
                if (batchId != null && !batchId.isEmpty()) {
                    toAdd.description = "OCR GetStop Reference: " + batchId;
                } else {
                    toAdd.description = "getshop file for: " +income.start + " - " + income.end;
                }
                

                DayEntry dayEntry = groupedIncomes.get(accountingNumber).get(0);

                if(dayEntry.isActualIncome && !dayEntry.isOffsetRecord) {
                    AccountingDetail detail = productManager.getAccountingDetail(toAdd.accountNumber);
                    if(detail == null) {
                        logPrint("nullpointer occurded when it should not.");
                        addToLog("nullpointer occurded when it should not on account: " + toAdd.accountNumber);
                        messageManager.sendErrorNotify("Null exception, acount does not exists on account: " + toAdd.accountNumber);
                        throw new ErrorException(86, "Account number " + toAdd.accountNumber + " does not exists on the system!");
                    }
                    toAdd.vatCode = detail.taxgroup + "";
                } else {
                    toAdd.vatCode = "0";
                }

                String currency = storeManager.getStoreSettingsApplicationKey("currencycode");
                if(currency == null || currency.isEmpty()) {
                    currency = "NOK";
                }

                toAdd.currencyAmount = toAdd.amount;
                toAdd.documentDate = new Date();
                toAdd.currencyCode = currency;
                String department = getConfigOptions().get("department");
                if(department != null && !department.isEmpty()) {
                    toAdd.departmentCode = getConfig("department");
                }

                result.add(toAdd);
            }
        }
        return result;
    }
}
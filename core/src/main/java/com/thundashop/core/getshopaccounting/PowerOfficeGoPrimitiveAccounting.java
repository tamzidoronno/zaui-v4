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
import com.thundashop.core.productmanager.data.AccountingDetail;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PowerOfficeGoPrimitiveAccounting extends AccountingSystemBase {

    private String token;

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
            String res = webManager.htmlPostBasicAuth("https://go.poweroffice.net/OAuth/Token", "grant_type=client_credentials", false, "UTF-8", auth);
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
            String result = webManager.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, "POST");
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
            BigDecimal total = groupedIncomes.get(accountingNumber).stream()
                    .map(o -> o.amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            zeroCheck = zeroCheck.add(total);
            
            logPrint("To be transfered on account: " + accountingNumber + ", total: " + total);
            PowerOfficeGoImportLine toAdd = new PowerOfficeGoImportLine();
            
            if (accountingNumber.length() == 4) {
                toAdd.accountNumber = new Integer(accountingNumber);
            } else {
                toAdd.customerCode = new Integer(accountingNumber);
            }
            
            toAdd.postingDate = income.start;
            toAdd.amount = total.doubleValue();
            toAdd.documentNumber = number;
            toAdd.description = "getshop file for: " +income.start + " - " + income.end;
            
            DayEntry dayEntry = groupedIncomes.get(accountingNumber).get(0);

            if(dayEntry.isActualIncome && !dayEntry.isOffsetRecord) {
                AccountingDetail detail = productManager.getAccountingDetail(toAdd.accountNumber);
                if(detail == null) {
                    logPrint("nullpointer occurded when it should not.");
                    addToLog("nullpointer occurded when it should not on account: " + toAdd.accountNumber);
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
        return result;
    }
    
    
    
}

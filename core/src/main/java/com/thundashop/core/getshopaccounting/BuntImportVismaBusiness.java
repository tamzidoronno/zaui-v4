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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BuntImportVismaBusiness extends AccountingSystemBase {

    private String token;

    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end) {
        return new ArrayList();
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.BUNTIMPORT_VISMA_BUSINESS;
    }

    @Override
    public String getSystemName() {
        return "Bunt import visma business";
    }

    @Override
    public void handleDirectTransfer(String orderId) {
        
    }
    
    @Override
    public HashMap<String, String> getConfigOptions() {
        HashMap<String, String> ret = new HashMap();
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
    public List<String> getTransferData(List<DayIncome> incomes) {
 
        int i = 0;
        List<VismaBunt> allLines = new ArrayList();
        for (DayIncome income : incomes) {    
            i++;
            List<VismaBunt> lines = transferIncomeData(income, i);
            allLines.addAll(lines);
        }
        
        VismaBunt bnt = new VismaBunt();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String today = format.format(new Date());
        List<String> resultLines = new ArrayList();
        
        resultLines.add("@IMPORT_METHOD(3)");
        resultLines.add("");
        resultLines.add("@WaBnd (SrNo, ValDt, SrcTp)");
        resultLines.add("\"4\" \""+today+"\" \"12\"");
        resultLines.add("");
        resultLines.add(bnt.getHeader());
        for(VismaBunt line : allLines) {
            resultLines.add(line.toString());
        }
        
        return resultLines;
    }
    
    private List<VismaBunt> transferIncomeData(DayIncome income, int number) {
        String description = "getshop file for: " +income.start + " - " + income.end;
        List<VismaBunt> importLines = createImportLines(income, number);
        return importLines;
    }

    private List<VismaBunt> createImportLines(DayIncome income, int number) {
        System.out.println("================ Day : " + income.start + " - " + income.end + " ============");
        BigDecimal zeroCheck = new BigDecimal(BigInteger.ZERO);

        Map<String, List<DayEntry>> groupedIncomes = income.getGroupedByAccountExTaxes();
        List<VismaBunt> result = new ArrayList();
        int j = 0;
        for (String accountingNumber : groupedIncomes.keySet()) {
            BigDecimal total = groupedIncomes.get(accountingNumber).stream()
                    .map(o -> o.amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            zeroCheck = zeroCheck.add(total);
            
            System.out.println("To be transfered on account: " + accountingNumber + ", total: " + total);

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

            
            VismaBunt buntLine = new VismaBunt();
            buntLine.VoNo = number + "";
            buntLine.VoDt = format.format(income.start);
            buntLine.VoTp = "3";
            buntLine.Txt = "GetShop";
            
            
            DayEntry dayEntry = groupedIncomes.get(accountingNumber).get(0);

            String taxCode = "";
            if(dayEntry.isActualIncome && !dayEntry.isOffsetRecord) {
                AccountingDetail detail = productManager.getAccountingDetail(new Integer(accountingNumber));
                if(detail == null) {
                    System.out.println("nullpointer occurded when it should not.");
                    addToLog("nullpointer occurded when it should not on account: " + accountingNumber);
                }
                taxCode = detail.taxgroup + "";
            } else {
                taxCode = "";
            }
            buntLine.CrAcNo = "";
            buntLine.CrTxCd = "";
            buntLine.DbAcNo = "";
            buntLine.DbTxCd = "";
            
            if(total.doubleValue() >= 0.0) {
                buntLine.DbAcNo = accountingNumber;
                buntLine.DbTxCd = taxCode;
            } else {
                buntLine.CrAcNo = accountingNumber;
                buntLine.CrTxCd = taxCode;
            }
            
            if(total.doubleValue() < 0.0) {
                total = total.multiply(new BigDecimal(-1));
            }
            
            DecimalFormat df2 = new DecimalFormat("#.00");
            if(total.doubleValue() == 0.0) {
                buntLine.Am = "0.00";
            } else {
                buntLine.Am = df2.format(total.doubleValue()) + "";
            }
            
            result.add(buntLine);
            j++;
        }
        System.out.println("Zero check: " + zeroCheck);
        return result;
    }
    
    
    
}

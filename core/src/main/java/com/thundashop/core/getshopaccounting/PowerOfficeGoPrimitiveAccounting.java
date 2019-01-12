/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
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
        
        for (DayIncome income : incomes) {    
            System.out.println("================ Day : " + income.start + " - " + income.end + " ============");
            BigDecimal zeroCheck = new BigDecimal(BigInteger.ZERO);
            
            Map<String, List<DayEntry>> groupedIncomes = income.getGroupedByAccountExTaxes();
            
            for (String accountingNumber : groupedIncomes.keySet()) {
                
                BigDecimal total = groupedIncomes.get(accountingNumber).stream()
                        .map(o -> o.amount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                zeroCheck = zeroCheck.add(total);
                
                System.out.println("To be transfered on account: " + accountingNumber + ", total: " + total);
            }
            
            System.out.println("Zero check: " + zeroCheck);
        }
    }
}

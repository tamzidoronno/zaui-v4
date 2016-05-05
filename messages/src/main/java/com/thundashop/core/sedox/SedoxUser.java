/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxUser extends DataCommon {
    public List<SedoxOrder> orders = new ArrayList<SedoxOrder>();
    
    public SedoxCreditAccount creditAccount = new SedoxCreditAccount();
    public boolean isNorwegian = false;
    public boolean isPassiveSlave = false;
    public boolean badCustomer = false;
    public boolean canUseExternalProgram = false;
    public String magentoId;
    public List<SedoxCreditOrder> creditOrders = new ArrayList<>();
    public List<SedoxEvcCreditOrder> evcCreditOrders = new ArrayList<>();
    boolean isActiveDelevoper = false;
    public String masterUserId;
    public double slaveIncome = 0;
    public String pushoverId = "";
    public String fixedPrice = "";
    public int evcCurrentBalance = 0;

    public void addCreditOrderUpdate(SedoxCreditOrder sedoxCreditOrder, String description) {
        creditAccount.updateCredit(sedoxCreditOrder, description);
        creditOrders.add(sedoxCreditOrder);
    }

    void checkForCreatedDate() {
        for (SedoxOrder order : orders) {
            if (order.dateCreated == null) {
                order.dateCreated = new Date();
            }
        }
    }

    void addEvcCreditOrder(SedoxEvcCreditOrder creditOrder) {
        evcCreditOrders.add(creditOrder);
    }
}

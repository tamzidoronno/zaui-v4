/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;
import com.powerofficego.data.AccessToken;
import com.powerofficego.data.ApiCustomerResponse;
import com.powerofficego.data.ApiOrderTransferResponse;
import com.powerofficego.data.Customer;
import com.powerofficego.data.PowerOfficeGoImportLine;
import com.powerofficego.data.PowerOfficeGoSalesOrder;
import com.powerofficego.data.PowerOfficeGoSalesOrderLines;
import com.powerofficego.data.SalesOrderTransfer;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.getshopaccounting.fikenservice.FikenInvoiceService;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.net.URLEncoder;
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
        ret.put("department", "PowerOfficeGo Department");
        return ret;
    }

    @Override
    boolean isUsingProductTaxCodes() {
        return true;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.getshopaccounting.fikenservice.FikenInvoiceService;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
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
public class FikenAccountingSystem extends AccountingSystemBase {

    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders) {
        Map<String, List<Order>> groupedOrders = groupOrders(orders);
        
        ArrayList<SavedOrderFile> retFiles = new ArrayList();
        
        for (String subType : groupedOrders.keySet()) {
            SavedOrderFile file = new SavedOrderFile();
            file.subtype = subType;
            
            if (groupedOrders.get(subType) == null) {
                return new ArrayList();
            }

            boolean allOk = validateOrders(groupedOrders.get(subType));
            if (!allOk)
                return new ArrayList();

            FikenInvoiceService service = new FikenInvoiceService(this, productManager, userManager);

            groupedOrders.get(subType).stream().forEach(order -> {
                boolean res = service.createInvoice(order);
                if (res) {
                    file.orders.add(order.id);
                    
                }
            });
            
            retFiles.add(file);
        }
        
        return retFiles;
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.FIKEN_API;
    }

    @Override
    public String getSystemName() {
        return "Fiken Api";
    }

    private boolean validateOrders(List<Order> orders) {
        boolean allOk = true;
        
        for (Order order : orders) {
            User user = userManager.getUserById(order.userId);
            
            if (user == null) {
                addToLog("Order: " + order.incrementOrderId + " does not have a user");
                allOk = false;
                continue;
            }
            
            if (user.accountingId == null || user.accountingId.isEmpty()) {
                addToLog("Order: " + order.incrementOrderId + ", user: " + user.fullName + ", does not have an accounting Id");
                allOk = false;
                continue;
            }
            
            for (CartItem item : order.cart.getItems()) {
                Product product = productManager.getProduct(item.getProduct().id);
                
                if (product == null) {
                    addToLog("Order: " + order.incrementOrderId + ", did not find product for the order.");
                    allOk = false;
                    continue;
                }
                
                if (product.accountingSystemId == null || product.accountingSystemId.isEmpty()) {
                    addToLog("Order: " + order.incrementOrderId + ", Product: " + product.name + " does not have an accountinSystemId.");
                    allOk = false;
                    continue;
                }
            }
        }
        
        return allOk;
    }

    @Override
    public HashMap<String, String> getConfigOptions() {
        HashMap<String, String> ret = new HashMap();
        ret.put("username", "Fiken username");
        ret.put("password", "Fiken password");
        ret.put("bankaccount", "Name of the bank account");
        ret.put("company", "Name of the company");
        return ret;
    }
    
}

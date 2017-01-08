/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.printmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class ReceiptGenerator implements Receipt {

    private final Order order;
    private final User user;
    private final AccountingDetails accountingDetails;

    public ReceiptGenerator(Order order, User user, AccountingDetails accountingDetails) {
        this.order = order;
        this.user = user;
        this.accountingDetails = accountingDetails;
    }

    @Override
    public String getContent() {
        String line = getHeader();
        for (CartItem cartItem : order.cart.getItems()) {
            
            double price = cartItem.getProduct().price;
            String totalPriceForCartItem = String.format("%.2f", (cartItem.getCount() * cartItem.getProduct().price));
            String name = cartItem.getProduct().name;
            int count = cartItem.getCount();
            line += count + " x " + name + " - Kr " + totalPriceForCartItem + ",-" + "\\n";
        }
        
        line += "\\n";
        for (CartTax cartTax : order.cart.getCartTaxes()) {
            line += "Mva " + cartTax.taxGroup.taxRate + "% - Kr " + String.format("%.2f", cartTax.sum) + "\\n";
        }
        
        line += "\nTotalt: " + String.format("%.2f", order.cart.getTotal(false)) + ",-\n";
        
        line += getFooter();
        
        return line;
    }

    private String getHeader() {
        String ret = accountingDetails.companyName + "\\n\\n";
        return ret;
    }
    
    private String getFooter() {
        SimpleDateFormat smf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String date = smf.format(new Date());
        String ret = "\\n\\n";
        ret += "========================\\n";
        ret += "Orgnr: " + accountingDetails.vatNumber + "\\n";
        ret += "Email: " + accountingDetails.contactEmail + "\\n";
        ret += "Orderid: " + order.incrementOrderId + "\\n";
        ret += "Dato: " + date + "\\n";
        if (user != null) {
            ret += "Operatør: " + user.customerId + "\\n";
        }
        
        return ret;
    }
    
}

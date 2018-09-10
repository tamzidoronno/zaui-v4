package com.thundashop.core.accountingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class AccountingSystemStatistics implements Serializable {
    public Date date;
    public HashMap<String, ProductStatiticsResult> productPrices = new HashMap(); 
    double total = 0.0;

    public void addOrder(Order ord) {
        for(CartItem item : ord.cart.getItems()) {
            String productId = item.getProduct().id;
            double amountExTaxes = item.getTotalEx();
            ProductStatiticsResult res = new ProductStatiticsResult();
            res.productId = productId;
            if(productPrices.containsKey(productId)) {
                res = productPrices.get(productId);
            }
            res.ordervalues.put(ord.id, amountExTaxes);
            productPrices.put(productId, res);
            res.totalValue += amountExTaxes;
            total += amountExTaxes;
        }
    }
}

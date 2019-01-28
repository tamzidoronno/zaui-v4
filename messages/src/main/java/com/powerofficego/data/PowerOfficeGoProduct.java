
package com.powerofficego.data;

import com.thundashop.core.productmanager.data.Product;
import java.io.Serializable;

public class PowerOfficeGoProduct implements Serializable {
    Integer id;
    String code;
    String name;
    Double costPrice;
    Double salesPrice;
    Integer salesAccount;
    Integer vatExemptSalesAccount;
    String description;
    
    public void insertProduct(Product product) {
        if(product.accountingSystemId != null && !product.accountingSystemId.isEmpty()) {
            id = new Integer(product.accountingSystemId);
        }
        code = product.id;
        name = product.name;
        costPrice = product.priceExTaxes;
        salesPrice = product.priceExTaxes;
        description = product.description;
        if(product.getAccountingAccount() != null && !product.getAccountingAccount().isEmpty()) {
            salesAccount = new Integer(product.getAccountingAccount());
        }
    }
}

/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.BasicDBObject;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductAccountingInformation;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class UpdateTaxAccountingInformation extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("12/01-2019");
    }
    
    @Override
    public String getId() {
        return "6cc1e348-6a74-4186-9420-465769c96e44";
    }
    
    public static void main(String[] args) {
        new UpdateTaxAccountingInformation().runSingle();
    }
    
    @Override
    public void run() {
        List<Store> stores = storePool.getAllStores();
        int i = 0;
        for (Store store : stores) {
            i++;
            long start = System.currentTimeMillis();
            System.out.println("Updating store: " + store.id + " Store " + i + " of " + stores.size());
            
            BasicDBObject  query = new BasicDBObject();
            query.put("className", "com.thundashop.core.productmanager.data.Product");
            
            List<Product> products = database.query("ProductManager", store.id,  query).stream()
                    .filter(o -> (o instanceof Product))
                    .map(o -> (Product)o)
                    .collect(Collectors.toList());
            
            for (Product product : products) {
                product.accountingConfig.clear();
                ProductAccountingInformation pdai = new ProductAccountingInformation();
                pdai.accountingNumber = product.getAccountingAccount();
                
                if (product.taxGroupObject != null) {
                    pdai.taxGroupNumber = product.taxGroupObject.groupNumber;
                } else {
                    System.out.println("UpdateTaxAccountingInformation: NOT ABLE TO SET TAXCODE ID"); 
                }
                
                product.accountingConfig.add(pdai);
                database.save(ProductManager.class, product);
            }
            
            System.out.println("Done updating store, product: " + products.size() + " time used: " + (System.currentTimeMillis() - start));
        }
    }
}

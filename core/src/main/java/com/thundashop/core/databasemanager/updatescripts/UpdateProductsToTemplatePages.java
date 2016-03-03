/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class UpdateProductsToTemplatePages extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("03/03-2016");
    }
    
    @Override
    public String getId() {
        return "822e0dfe-f269-4631-8756-1c8ccf326226";
    }
    
    public static void main(String[] args) {
        new UpdateProductsToTemplatePages().runSingle();
    }
    
    @Override
    public void run() {
        Map<Store, List<DataCommon>> data = getAllData(ProductManager.class);
        
        for (Store store : data.keySet()) {
            if (!store.id.equals("d4d1317a-640f-4fb2-94f1-a41efd3b6b71")) {
                continue;
            }
            List<DataCommon> common = data.get(store);
            logOnStore(store);
            
            PageManager pageManger = getManager(PageManager.class, store, "");
            ProductManager productManger = getManager(ProductManager.class, store, "");
            
            for (DataCommon ob : common) {
                if (ob instanceof Product) {
                    Product product = (Product)ob;
                    product.pageId = pageManger.createPageFromTemplatePage("ecommerce_product_template_1").id;
                    productManger.saveProduct(product);
                }
            }
        }
    }


    
}

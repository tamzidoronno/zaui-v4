/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class SedoxProductManager extends ManagerBase implements ISedoxProductManager {

    private List<SedoxProduct> products = new ArrayList<>();

    @Autowired
    public SedoxProductManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
//        try {
                    Gson gson = new Gson();
                    InputStream in = SedoxProductManager.class.getResourceAsStream("sedoxProducts.json");
                    String theString = convertStreamToString(in);
            gson.fromJson(theString,  new TypeToken<List<SedoxProduct>>(){}.getType());
                    products = gson.fromJson(theString,  new TypeToken<List<SedoxProduct>>(){}.getType());
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(SedoxProductManager.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SQLException ex) {
//            java.util.logging.Logger.getLogger(SedoxProductManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public SedoxProductSearchPage search(String searchString) {
        searchString = searchString.toLowerCase();
        Set<SedoxProduct> retProducts = new TreeSet<>();
        
        for (SedoxProduct product : products) {
            if (!product.saleAble ) {
                continue;
            }
            
            if (product.userBrand != null)
                System.out.println(product.userBrand);
            
            if (product.filedesc != null && product.filedesc.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.userBrand != null && product.userBrand.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.userModel != null && product.userModel.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            for (SedoxBinaryFile file : product.binaryFiles) {
                for (SedoxProductAttribute attr : file.attribues) {
                    if (attr.value != null && attr.value.toLowerCase().contains(searchString)) {
                        retProducts.add(product);
                    }
                }
            }
        }
        
        SedoxProductSearchPage page = new SedoxProductSearchPage();
        page.pageNumber = 1;
        page.products = new ArrayList<>(retProducts);
        page.totalPages = (int) Math.ceil((double)page.products.size()/(double)10);
        
        if (page.products.size() > 10) {
            page.products = page.products.subList(0, 10);
        } 
        
        return page;
    }
}

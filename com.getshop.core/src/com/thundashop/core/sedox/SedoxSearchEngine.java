/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SedoxSearchEngine {
    private List<SedoxProduct> products = new ArrayList();
    
    public synchronized  SedoxProductSearchPage getSearchResult(List<SedoxProduct> products, String searchString) {
        this.products = products;
        
        searchString = searchString.toLowerCase();
        Set<SedoxProduct> retProducts = new TreeSet<>();
        
        for (SedoxProduct product : products) {
            if (!product.saleAble ) {
                continue;
            }
            
            if (product.filedesc != null && product.filedesc.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.brand != null && product.brand.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.model != null && product.model.toLowerCase().contains(searchString)) {
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

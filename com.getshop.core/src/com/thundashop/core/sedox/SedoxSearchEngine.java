/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.util.ArrayList;
import java.util.Collections;
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
    
    private int pageSize = 10;
    
    public synchronized  SedoxProductSearchPage getSearchResult(List<SedoxProduct> products, SedoxSearch search) {
        this.products = products;
        
        String searchString = search.searchCriteria.toLowerCase();
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
            
            if (product.originalChecksum != null && product.originalChecksum.toLowerCase().contains(searchString)) {
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
        page.pageNumber = search.page;
        page.products = new ArrayList<>(retProducts);
        Collections.sort(page.products);
        page.totalPages = (int) Math.ceil((double)page.products.size()/(double)10);
        
        int start = (search.page-1)*pageSize;
        int stop = (search.page)*pageSize;
            
        if (page.products.size() >= start && page.products.size() >= stop) {
            page.products = page.products.subList(start, stop);
            return page;
        } 
        
        if (page.products.size() >= start) {
            page.products = page.products.subList(start, page.products.size());
            return page;
        }
        
        page.pageNumber = 1;
        return page;
    }
}

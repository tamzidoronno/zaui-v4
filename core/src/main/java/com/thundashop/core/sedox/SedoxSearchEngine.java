/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
    private List<SedoxSharedProduct> products = new ArrayList();
    
    private int pageSize = 10;
    
    public synchronized  SedoxProductSearchPage getSearchResult(List<SedoxSharedProduct> products, SedoxSearch search, User currentUser) {
        this.products = products;
        
        String searchString = search.searchCriteria.toLowerCase();
        
        List<String> searchStrings = Arrays.asList(searchString.split(" "));
        
        Set<SedoxSharedProduct> retProducts = new HashSet();
        
        if (searchStrings.size() == 1 || searchStrings.isEmpty()) {
            retProducts = getProducts(searchString, currentUser, products);
        } else {
            for (String searchStringToUse : searchStrings) {
                if (retProducts.isEmpty()) {
                    retProducts = getProducts(searchStringToUse, currentUser, products);
                } else {
                    retProducts = getProducts(searchStringToUse, currentUser, new ArrayList(retProducts));
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
    
    private Set<SedoxSharedProduct> getProducts(String searchString, User currentUser, List<SedoxSharedProduct> searchTroughProducts) {
        Set<SedoxSharedProduct> retProducts = new TreeSet<>();
        
        for (SedoxSharedProduct product : searchTroughProducts) {
            if (product.id.equals(searchString) || inFileId(product, searchString)) {
                retProducts.add(product);
            }
            if (!product.saleAble  && (currentUser == null || currentUser.type < 100)) {
                continue;
            }
            
            if (!product.hasMoreThenOriginalFile()) {
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
			
            if (product.ecuBrand != null && product.ecuBrand.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
			
            if (product.ecuHardwareNumber != null && product.ecuHardwareNumber.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.ecuPartNumber != null && product.ecuPartNumber.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
			
            if (product.ecuSoftwareNumber != null && product.ecuSoftwareNumber.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.ecuSoftwareVersion != null && product.ecuSoftwareVersion.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.ecuType != null && product.ecuType.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.engineSize != null && product.engineSize.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.softwareNumber != null && product.softwareNumber.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
            
            if (product.originalChecksum != null && product.originalChecksum.toLowerCase().contains(searchString)) {
                retProducts.add(product);
            }
        }
        
        return retProducts;
    }

    private boolean inFileId(SedoxSharedProduct product, String searchString) {
        int searchId = 0;
        try {
            searchId = Integer.parseInt(searchString);
        } catch (Exception ex) {
            return false;
        }
        
        for (SedoxBinaryFile file : product.binaryFiles) {
            if (file.id == searchId) {
                return true;
            }
        }
        
        return false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.sedox;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author ktonder
 */
class Sorters {
    public final Map<String, SedoxProduct> products;
    public final Map<String, SedoxSharedProduct> sharedProducts;

    public Sorters(Map<String, SedoxProduct> products, Map<String, SedoxSharedProduct> sharedProducts) {
        this.products = products;
        this.sharedProducts = sharedProducts;
    }
    
    public Comparator getSorter(FilterData filterData) {
        if (filterData.sortBy.equals("sedoxorder_sedoxid")) {
            return sedoxIdComperator(filterData);
        }
        
        if (filterData.sortBy.equals("sedoxorder_date")) {
            return sedoxDateComperator(filterData);
        }
        
        if (filterData.sortBy.equals("sedoxorder_product_name")) {
            return sedoxProductName(filterData);
        }
        
        return null;
    }

    private Comparator sedoxIdComperator(FilterData filterData) {
        return (Comparator<SedoxOrder>) (SedoxOrder o1, SedoxOrder o2) -> {
            if (filterData.ascending) {
                return new Integer(o2.productId).compareTo(Integer.parseInt(o1.productId));
            } else {
                return new Integer(o1.productId).compareTo(Integer.parseInt(o2.productId));
            }
        };
    }
    
    private Comparator sedoxDateComperator(FilterData filterData) {
        return (Comparator<SedoxOrder>) (SedoxOrder o1, SedoxOrder o2) -> {
            if (filterData.ascending) {
                return o2.dateCreated.compareTo(o1.dateCreated);
            } else {
                return o1.dateCreated.compareTo(o2.dateCreated);
            }
        };
    }

    private Comparator sedoxProductName(FilterData filterData) {
        return (Comparator<SedoxOrder>) (SedoxOrder o1, SedoxOrder o2) -> {
            SedoxSharedProduct product1 = null;
            SedoxSharedProduct product2 = null;
            
            try {
                product1 = sharedProducts.get(products.get(o1.productId).sharedProductId);
                product2 = sharedProducts.get(products.get(o2.productId).sharedProductId);
            } catch (NullPointerException ex) {
                return -1;
            }
            
            if (product1 == null || product2 == null) {
                return -1;
            }
            
            if (filterData.ascending) {
                return product1.getName().compareTo(product2.getName());
            } else {
                return product2.getName().compareTo(product1.getName());
            }
        };
    }
    
}

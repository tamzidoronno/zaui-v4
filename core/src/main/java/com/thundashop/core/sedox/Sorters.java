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
        
        if (filterData.sortBy.equals("sedoxproduct_sedoxid")) {
            return sortBySedoxProduct(filterData);
        }
        
        if (filterData.sortBy.equals("sedoxproduct_name")) {
            return sortBySedoxProductName(filterData);
        }
        
        if (filterData.sortBy.equals("sedoxproduct_date")) {
            return sortBySedoxProductDate(filterData);
        }
        
        if (filterData.sortBy.equals("sedoxcredithist_sedoxid")) {
            return sortSedoxCreditHistoryBy_sedoxid(filterData);
        }
        
        if (filterData.sortBy.equals("sedoxcredithist_date")) {
            return sortSedoxCreditHistoryBy_date(filterData);
        }
        
        if (filterData.sortBy.equals("sedoxcredithist_name")) {
            return sortSedoxCreditHistoryBy_description(filterData);
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

    private Comparator sortBySedoxProduct(FilterData filterData) {
        return (Comparator<SedoxProduct>) (SedoxProduct o1, SedoxProduct o2) -> {
            
            Integer int1 = null;
            Integer int2 = null;
            
            try {
                int1 = new Integer(o1.id);
                int2 = new Integer(o2.id);
            } catch (Exception ex) {
                return -1;
            }
            
            if (filterData.ascending) {
                return int1.compareTo(int2);
            } else {
                return int2.compareTo(int1);
            }
        };
    }

    private Comparator sortBySedoxProductName(FilterData filterData) {
        return (Comparator<SedoxProduct>) (SedoxProduct o1, SedoxProduct o2) -> {
            SedoxSharedProduct product1 = sharedProducts.get(o1.sharedProductId);
            SedoxSharedProduct product2 = sharedProducts.get(o2.sharedProductId);
            
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

    private Comparator sortBySedoxProductDate(FilterData filterData) {
        return (Comparator<SedoxProduct>) (SedoxProduct o1, SedoxProduct o2) -> {
            if (filterData.ascending) {
                return o1.rowCreatedDate.compareTo(o2.rowCreatedDate);
            } else {
                return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
            }
        };
    }

    private Comparator sortSedoxCreditHistoryBy_sedoxid(FilterData filterData) {
        return (Comparator<SedoxCreditHistory>) (SedoxCreditHistory o1, SedoxCreditHistory o2) -> {
            if (filterData.ascending) {
                return new Integer(o1.transactionReference).compareTo(new Integer(o2.transactionReference));
            } else {
                return new Integer(o2.transactionReference).compareTo(new Integer(o1.transactionReference));
            }
        };
    }
    
    private Comparator sortSedoxCreditHistoryBy_date(FilterData filterData) {
        return (Comparator<SedoxCreditHistory>) (SedoxCreditHistory o1, SedoxCreditHistory o2) -> {
            if (filterData.ascending) {
                return o2.dateCreated.compareTo(o1.dateCreated);
            } else {
                return o1.dateCreated.compareTo(o2.dateCreated);
            }
        };
    }
    
    private Comparator sortSedoxCreditHistoryBy_description(FilterData filterData) {
        return (Comparator<SedoxCreditHistory>) (SedoxCreditHistory o1, SedoxCreditHistory o2) -> {
            if (filterData.ascending) {
                return o1.description.compareTo(o2.description);
            } else {
                return o2.description.compareTo(o1.description);
            }
        };
    }
    
}

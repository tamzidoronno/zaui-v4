/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.util.Comparator;

/**
 *
 * @author ktonder
 */
public class SortByDateSedoxProductComperable implements Comparator<SedoxProduct>{

    @Override
    public int compare(SedoxProduct o1, SedoxProduct o2) {
        if (o1.rowCreatedDate.after(o2.rowCreatedDate)) {
            return -1;
        } else if (o1.rowCreatedDate.before(o2.rowCreatedDate)) {
            return 1;
        } else {
            return 0;
        }  
    }
    
}

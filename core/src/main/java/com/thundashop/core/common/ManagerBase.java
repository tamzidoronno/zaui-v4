/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */ 
package com.thundashop.core.common;

import com.thundashop.core.appmanager.data.Application;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class ManagerBase extends ManagerSubBase {
    protected FilteredData pageIt(List data, FilterOptions filterOptions) {
        FilteredData retData = new FilteredData();
        retData.datas = chopit(filterOptions, data);
        retData.filterOptions = filterOptions;
        retData.totalPages = (int) Math.ceil((double)data.size()/(double)filterOptions.pageSize);
        return retData;
    }
    
    protected List chopit(FilterOptions filterData, List data) {
        int end = (filterData.pageNumber*filterData.pageSize) ;
        int start = ((filterData.pageNumber-1)*filterData.pageSize);
        
        List objects = null;
        
        if (data.size() >= end) {
            objects = new ArrayList(data.subList(start, end));
        } 
        
        if (objects == null && data.size() >= start) {
            objects = new ArrayList(data.subList(start, data.size()));
        }
        
        if (objects == null) {
            objects = new ArrayList(data);
        }
        
        Collections.sort(objects);
        Collections.reverse(objects);
        
        return objects;
    }

}

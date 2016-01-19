/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class FilterOptions {
    public String searchWord = "";
    public int pageNumber = 1;
    public int pageSize = 20;
    public Date startDate = null;
    public Date endDate = null;
    public Map<String, String> extra = new HashMap();
}

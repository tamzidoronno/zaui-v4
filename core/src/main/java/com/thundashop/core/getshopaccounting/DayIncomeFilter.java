/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DayIncomeFilter {
    public Date start;
    public Date end;
    public boolean ignoreConfig = false;
    public List<String> departmentIds = new ArrayList();
}

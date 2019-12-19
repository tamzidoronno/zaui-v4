/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class PmsCustomerReportFilter implements Serializable {
    public Date start;
    public Date end;
    public boolean includeTaxex = false;
}

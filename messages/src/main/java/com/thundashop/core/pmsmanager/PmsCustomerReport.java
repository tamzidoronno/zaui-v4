/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsCustomerReport implements Serializable {
    public List<PmsCustomerReportEntry> customers = new ArrayList();

    void sortCustomers() {
        customers.sort(Comparator.comparing(PmsCustomerReportEntry::getNumberSlept));
        Collections.reverse(customers);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class OrderFilter {
    public Date start;
    public Date end;
    public String paymentMethod;
    public Integer state;
    public String type;
    public String searchWord;
    public List<String> customer = new ArrayList();

}

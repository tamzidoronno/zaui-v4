/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsAddonFilter {
    public Date start;
    public Date end;
    public String productId;
    public Boolean deleteAddons = false;
    public boolean singleDay;
    public List<String> rooms;
}

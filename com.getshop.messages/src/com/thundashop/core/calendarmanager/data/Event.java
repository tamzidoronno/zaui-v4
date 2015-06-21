/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendarmanager.data;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class Event extends DataCommon {
    public String title;
    public double priceAdults;
    public double priceChild;
    public String imageId;
    public String iconImageId;
    public String description = "";
    public int capacity = 0;
}

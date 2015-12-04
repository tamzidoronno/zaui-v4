/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine.data;

import com.google.gson.Gson;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class BookingRequiredConfirmationList extends DataCommon {
    public String bookingItemTypeId = "";
    public List<String> bookings = new ArrayList();

    public BookingRequiredConfirmationList jsonClone() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), BookingRequiredConfirmationList.class);
    }
}

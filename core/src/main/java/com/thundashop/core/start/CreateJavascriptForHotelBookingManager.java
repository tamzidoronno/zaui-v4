/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author ktonder
 */
public class CreateJavascriptForHotelBookingManager {
    public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
        new GenerateJavascriptApi().start("/home/ktonder/source/getshop/3.0.0/com.getshop.hotelmanagment/public_html/js/getshopapi.js");
    }
}

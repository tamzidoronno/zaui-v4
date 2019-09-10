/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.stripe;

import java.util.HashMap;

/**
 *
 * @author boggi
 */
public class WebhookCallback {
    HashMap<String, String> result = new HashMap();
    String validationKey = "";
    String orderId = "";
    String payload = "";
    Integer amount = 0;
}

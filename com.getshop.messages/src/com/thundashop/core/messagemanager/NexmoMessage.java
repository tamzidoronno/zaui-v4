/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author boggi
 */
public class NexmoMessage {
    public String to;
    @SerializedName("message-id")
    public String msgId;
    public String status;
    @SerializedName("remaining-balance")
    public String remainingBalance;
    @SerializedName("message-price")
    public String messagePrice;
    public String network;
}

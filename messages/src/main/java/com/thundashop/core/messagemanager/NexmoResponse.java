/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 *
 * @author boggi
 */
public class NexmoResponse {
    @SerializedName("message-count")
    public String msgCount;
    public List<NexmoMessage> messages;
}

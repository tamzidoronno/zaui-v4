/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;

/**
 *
 * @author boggi
 */
@PermenantlyDeleteData
public class UnreadTickets extends DataCommon {
    public String ticketId;
    public String tokenId;
    public String title;
    public String belongsToStore;
}

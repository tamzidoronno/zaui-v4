/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pga;

import com.thundashop.core.common.DataCommon;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class LoginDetails extends DataCommon {
    public String bookingId;
    public String pmsBookingRoomId;
    public String code;
    public String tokenId = UUID.randomUUID().toString();
    
}
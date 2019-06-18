/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsbookingprocess;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class BookingProcessRoomStatus {
    public Date checkin;
    public Date checkout;
    public boolean checkinDateOk = false;
    public boolean roomIsClean = false;
    public boolean paymentCompleted = false;    
    public String roomId = "";
}

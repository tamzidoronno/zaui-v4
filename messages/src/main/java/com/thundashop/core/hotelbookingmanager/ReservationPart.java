/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.hotelbookingmanager;

import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class ReservationPart {
    public String partId = UUID.randomUUID().toString();
    public Date startDate;
    public Date endDate;
}

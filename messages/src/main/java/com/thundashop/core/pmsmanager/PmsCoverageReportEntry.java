/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsCoverageReportEntry {
    public Date date;
    public Integer roomsAvailable = 0;
    public Integer roomsTaken = 0;
    public Integer roomsArriving = 0;
    public Integer roomsDeparting = 0;
    public Integer guestsStaying = 0;
    public Integer guestsArriving = 0;
    public Integer guestsDeparting = 0;
    public Integer roomsSold = 0;
    public Integer groupRoomsSold = 0;
    public Integer groupRoomsArriving = 0;
    public Integer groupRoomsDeparting = 0;
    public Integer groupRoomsTaken = 0;
    public Double avgPrice = 0.0;
    public Double totalPrice = 0.0;
    public Double occupancy = 0.0;
}


package com.thundashop.core.wubook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WubookBooking implements Serializable {
    public String channelId;
    public String reservationCode;
    public String channel_reservation_code;
    public boolean delete;
    public String customerNotes;
    public String phone;
    public Integer wasModified;
    public String email;
    public String city;
    public String postCode;
    public String address;
    public String name;
    public Date depDate;
    public Date arrivalDate;
    public boolean breakfast;
    public List<WubookBookedRoom> rooms = new ArrayList();
    public String countryCode;
}

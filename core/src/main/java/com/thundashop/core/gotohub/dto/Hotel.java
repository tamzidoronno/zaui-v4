package com.thundashop.core.gotohub.dto;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    public String name;
    public String hotel_code;
    public String description;
    public String about;
    public String address;
    public double latitude;
    public double longitude;
    public String checkin_time;
    public String checkout_time;
    public Contact contact_details;
    public String status;
    public List<String> images = new ArrayList<String>();


}

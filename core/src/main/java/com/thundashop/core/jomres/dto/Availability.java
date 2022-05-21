package com.thundashop.core.jomres.dto;

public class Availability {
    String date_from;
    String date_to;
    boolean available;

    public Availability(String date_from, String date_to, boolean available) {
        this.date_from = date_from;
        this.date_to = date_to;
        this.available = available;
    }


}

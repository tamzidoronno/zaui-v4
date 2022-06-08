package com.thundashop.core.jomres.dto;

public class Availability {
    // TODO: will use json_property annotation and camel form of variable name

    //Camel format is not used for Gson convertion of Jomres response. Please don't change the variable names
    String date_from;
    String date_to;
    boolean available;

    public Availability(String date_from, String date_to, boolean available) {
        this.date_from = date_from;
        this.date_to = date_to;
        this.available = available;
    }


}

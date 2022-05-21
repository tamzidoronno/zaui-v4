package com.thundashop.core.jomres.dto;

import com.google.gson.internal.LinkedTreeMap;

public class JomresGuest {
    public String name;
    public String house, street, city, region, country, postcode;
    public String address;
    public String countryCode;
    public String telLandline, telMobile, email;
    public String vatNumber;

    public JomresGuest() {
    }
    public JomresGuest(LinkedTreeMap<String, ?> guest) {
        String firstName = guest.get("enc_firstname").toString();
        String surName = guest.get("enc_surname").toString();
        this.name = firstName + " " + surName;

        this.telLandline = guest.get("enc_tel_landline").toString();
        this.telMobile = guest.get("enc_tel_mobile").toString();
        this.email = guest.get("enc_email").toString();

        this.house = guest.get("enc_house").toString();
        this.street = guest.get("enc_street").toString();
        this.city = guest.get("enc_city").toString();
        this.region = guest.get("enc_region").toString();
        this.country = guest.get("country").toString();
        this.postcode = guest.get("enc_postcode").toString();
    }

}

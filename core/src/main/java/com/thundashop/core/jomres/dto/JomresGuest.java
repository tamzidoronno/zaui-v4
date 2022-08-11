package com.thundashop.core.jomres.dto;

import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Optional;

public class JomresGuest implements Serializable {
    public String name;
    public String house, street, city, region, country, postcode;
    public String address;
    public String countryCode;
    public String telLandline, telMobile, email;
    public String vatNumber;

    public JomresGuest() {
    }
    public JomresGuest(LinkedTreeMap guest) {
        String firstName = guest.get("enc_firstname").toString();
        String surName = guest.get("enc_surname").toString();
        this.name = firstName + " " + surName;

        String rawLandLine = Optional.ofNullable(guest.get("enc_tel_landline").toString()).orElse("");
        String rawMobile = Optional.ofNullable(guest.get("enc_tel_mobile").toString()).orElse("");
        String landLine = StringUtils.substringAfter(rawLandLine,";");
        String mobile = StringUtils.substringAfterLast(rawMobile,";");
        this.telLandline = landLine.isEmpty()? rawLandLine:landLine;
        this.telMobile = mobile.isEmpty()? rawMobile:mobile;
        this.email = guest.get("enc_email").toString();

        this.house = guest.get("enc_house").toString();
        this.street = guest.get("enc_street").toString();
        this.city = guest.get("enc_city").toString();
        this.region = guest.get("enc_region").toString();
        this.country = guest.get("country").toString();
        this.postcode = guest.get("enc_postcode").toString();
        this.address= this.house + ", "+this.street + ", " + this.city + ", " + this.region + ", " + this.country+".";
    }

}

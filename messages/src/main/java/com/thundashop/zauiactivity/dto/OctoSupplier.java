package com.thundashop.zauiactivity.dto;

import lombok.Data;

@Data
public class OctoSupplier {
    private String id;
    private String name;
    public String endpoint;
    public String documentation;
    public OctoSupplierContact contact;
    public String country;
}

@Data
class OctoSupplierContact {
    public String website;
    public String email;
    public String otherEmail;
    public String telephone;
    public String address;
}
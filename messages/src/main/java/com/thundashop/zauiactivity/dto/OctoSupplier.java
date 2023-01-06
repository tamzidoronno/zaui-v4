package com.thundashop.zauiactivity.dto;

import lombok.Data;

@Data
public class OctoSupplier {
    private int id;
    private String name;
    private String endpoint;
    private String documentation;
    private OctoSupplierContact contact;
    private String country;
}

@Data
class OctoSupplierContact {
    private String website;
    private String email;
    private String otherEmail;
    private String telephone;
    private String address;
}
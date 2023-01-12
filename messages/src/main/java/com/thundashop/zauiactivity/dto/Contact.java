package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.List;

@Data
public class Contact {
    private String fullName;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private List<String> locales = null;
    private String country;
    private String notes;
}


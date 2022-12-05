package com.thundashop.zauiactivity.dto;

import lombok.Data;

@Data
public class OctoBookingConfirmRequest {
    private String resellerReference;
    private Contact contact;
    private Boolean emailConfirmation;
}

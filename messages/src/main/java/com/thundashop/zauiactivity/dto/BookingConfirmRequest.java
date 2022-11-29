package com.thundashop.zauiactivity.dto;

import lombok.Data;

@Data
public class BookingConfirmRequest {
    private String resellerReference;
    private Contact contact;
    private Boolean emailConfirmation;
}

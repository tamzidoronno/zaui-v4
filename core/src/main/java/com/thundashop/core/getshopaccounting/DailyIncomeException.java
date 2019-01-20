/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

/**
 *
 * @author ktonder
 */
public class DailyIncomeException extends RuntimeException {
    private String msg = "";
    
    public DailyIncomeException(String message) {
        super(message);
        this.msg = message;
    }

    @Override
    public String getMessage() {
        return msg;
    }
    
    
}
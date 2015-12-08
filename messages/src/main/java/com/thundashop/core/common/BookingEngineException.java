/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

/**
 *
 * @author ktonder
 */
public class BookingEngineException extends RuntimeException {
    private String message;

    public BookingEngineException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem.zwavejobs;

/**
 *
 * @author ktonder
 */
public class ZwaveThreadExecption extends RuntimeException {
    private String message; 
    private int errorcode;

    public ZwaveThreadExecption(String message, int errorcode) {
        this.message = message;
        this.errorcode = errorcode;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean isErrorBecauseCodeIsAlreadSet() {
        return errorcode == 1;
    }
    
    public boolean isErrorBecauseQueueDidNotEmpty() {
        return errorcode == 2;
    }
}

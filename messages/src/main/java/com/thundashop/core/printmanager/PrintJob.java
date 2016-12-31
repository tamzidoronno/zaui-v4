/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.printmanager;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class PrintJob implements Serializable {
    public String printerId;
    
    /**
     * This should be base64 encoded content that can be decoded and sent to printer.
     */
    public String content;
    
    public Date createdDate = new Date();
}

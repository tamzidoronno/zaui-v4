/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

/**
 *
 * @author ktonder
 */
public class ErrorMessage {
    public String additionalInformation;
    public int errorCode;
    
    public ErrorMessage(ErrorException execption) {
        errorCode = execption.code;
        additionalInformation = execption.additionalInformation;
    }
}

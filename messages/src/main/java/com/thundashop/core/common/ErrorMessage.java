/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class ErrorMessage implements Serializable {
    public String additionalInformation;
    public int errorCode;

    public String errorMessage;
    
    public ErrorMessage(ErrorException execption) {
        errorCode = execption.code;
        additionalInformation = execption.additionalInformation;
        if(StringUtils.isNotBlank(execption.errorMessage)) errorMessage = execption.errorMessage;
    }
}

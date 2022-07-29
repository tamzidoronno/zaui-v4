/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

/**
 * @author hjemme
 */
public class ErrorException extends RuntimeException {
    public String additionalInformation;
    public int code;
    public String errorMessage;

    public ErrorException(int code) {
        this.code = code;
    }

    public ErrorException(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }

}

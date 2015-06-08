/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.meca.data;

import java.io.Serializable;

/**
 *
 * @author emil
 */
public class RPCResult implements Serializable {
    
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    
    public RPCResult() {}
    public RPCResult(String message) {
        this.message = message;
    }
    public RPCResult(int status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public int status;
    public String message;
    
}

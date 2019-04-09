/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

/**
 *
 * @author boggi
 */
public class GetShopBeanException extends RuntimeException {
    public GetShopBeanException(String errorMessage) {
        super(errorMessage);
    }
    
    public GetShopBeanException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}

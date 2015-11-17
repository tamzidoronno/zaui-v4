/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IInvoiceManager {
    public void createInvoice(String orderId) throws ErrorException;
    @Administrator 
    public String getBase64EncodedInvoice(String orderId) throws ErrorException;
}

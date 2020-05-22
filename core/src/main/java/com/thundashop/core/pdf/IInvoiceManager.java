/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.ordermanager.data.InvoiceListFilter;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IInvoiceManager {
    public void createInvoice(String orderId) throws ErrorException;
    
    @Administrator
    public String getBase64EncodedInvoice(String orderId);
    
    @Administrator
    public void sendReceiptToCashRegisterPoint(String deviceId, String orderId);
    
    @Autowired
    public AccountingDetails getAccountingDetails() throws ErrorException;
    
    @Administrator
    public List<Order> getAllInvoices();
    
    @Administrator
    public List<Order> getAllInvoicesByFilter(InvoiceListFilter filter);
    
}

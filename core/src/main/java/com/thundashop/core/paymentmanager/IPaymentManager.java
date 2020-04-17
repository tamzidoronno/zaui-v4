/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IPaymentManager {
    @Administrator
    public void savePaymentConfiguration(PaymentConfiguration config);
    
    public PaymentConfiguration getConfig(String id);
    
    @Administrator
    public void saveStorePaymentConfiguration(StorePaymentConfig config);
    
    @Administrator
    public StorePaymentConfig getStorePaymentConfiguration(String paymentAppId);
    
    @Administrator
    public List<StorePaymentConfig> getStorePaymentConfigurations(String paymentAppId);
    
    @Administrator
    public GeneralPaymentConfig getGeneralPaymentConfig();
    
    @Administrator
    public void saveGeneralPaymentConfig(GeneralPaymentConfig config);
    
    @Administrator
    public void resetAllAccountingConfigurationForUsersAndOrders(String password);
    
    @Administrator
    public void saveMultiplePaymentMethods(List<String> ids);
    
    public List<String> getMultiplePaymentMethods();
}

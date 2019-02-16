/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.director;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.ordermanager.data.Order;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IDirectorManager {
    @ForceAsync
    @Administrator
    public void syncFromOld();
    
    @ForceAsync
    public DailyUsage getDailyUsage(String password, Date date);
    
    @ForceAsync
    public Date getCreatedDate(String password);
//    
//    @Administrator
//    public void createOrder(String companyId, int month, int year);
    
    @Administrator
    public List<Order> createVirtualOrders();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.bigstock;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;

/**
 * Used for interfacing with the bigstock api.
 * you can use this to purchase pictures.
 * 
 * @author ktonder
 */
@GetShopApi
public interface IBigStock {
    
    /**
     * Purchases a picture from the bigstock library.
     * 
     * @param imageId
     * @param sizeCode
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public String purchaseImage(String imageId, String sizeCode) throws ErrorException;
}

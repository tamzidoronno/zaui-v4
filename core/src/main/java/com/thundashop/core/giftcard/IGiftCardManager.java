/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.giftcard;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGiftCardManager {
    
    @Editor
    public List<GiftCard> getAllCards();
    
    @Administrator
    public void deleteGiftCard(String id);
    
    @Administrator
    public void saveGiftCard(GiftCard card);

    public GiftCard getGiftCard(String giftCardCode);
    
    @Administrator
    public Integer payOrderWithGiftCard(String code, String orderId);
    
}

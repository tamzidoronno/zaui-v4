package com.thundashop.core.getshop;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopNotSynchronized;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.getshop.data.GetshopStore;
import com.thundashop.core.getshop.data.PartnerData;
import com.thundashop.core.getshop.data.SmsResponse;
import com.thundashop.core.getshop.data.StartData;
import com.thundashop.core.getshop.data.StoreCreatedData;
import com.thundashop.core.getshop.data.WebPageData;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGetShop {
    
    /**
     * 
     * @param code
     * @return 
     */
    public List<GetshopStore> getStores(String code);
    
    /**
     * 
     * @param ids
     * @throws ErrorException 
     */
    public void setApplicationList(List<String> ids, String partnerId, String password) throws ErrorException;
    
    /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException 
     */
    public PartnerData getPartnerData(String partnerId, String password) throws ErrorException;

    /**
     * 
     * @param userId
     * @param partner
     * @param password
     * @throws ErrorException 
     */
    public void addUserToPartner(String userId, String partner, String password) throws ErrorException;
    
        
    /**
     * Need to figure out what address is connected to a specific uuid?
     * Remember this is query is quite slow. so cache the result.
     * @param uuid
     * @return
     * @throws ErrorException 
     */
    public String findAddressForUUID(String uuid) throws ErrorException;

    /**
     * Find the store address for a given application.
     * @param uuid The appid.
     * @return
     * @throws ErrorException 
     */
    public String findAddressForApplication(String uuid) throws ErrorException;
    
    
    /**
     * Create a new webpage
     * @return 
     */
    public Store createWebPage(WebPageData webpageData) throws ErrorException;
    
    public void saveSmsCallback(SmsResponse smsResponses);
    
    public String startStoreFromStore(StartData startData);
    
    @GetShopNotSynchronized
    public String getBase64EncodedPDFWebPage(String urlToPage);
}

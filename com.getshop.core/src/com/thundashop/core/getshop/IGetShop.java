package com.thundashop.core.getshop;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.getshop.data.GetshopStore;
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
     * @param userId
     * @param partner
     * @param password
     * @throws ErrorException 
     */
    public void addUserToPartner(String userId, String partner, String password) throws ErrorException;
    
    public List<GetshopStore> getStoresConnectedToMe() throws ErrorException;
    
    /**
     * When an administrator has logged on, it can call on this call to connect its store to a partner.
     */
    public void connectStoreToPartner(String partner) throws ErrorException;
    
    /**
     * Need to figure out what address is connected to a specific uuid?
     * Remember this is query is quite slow. so cache the result.
     * @param uuid
     * @return
     * @throws ErrorException 
     */
    public String findAddressForUUID(String uuid) throws ErrorException;
    
    /**
     * Get the partner id attached to this user.
     * @return
     * @throws ErrorException 
     */
    public String getPartnerId() throws ErrorException;
}

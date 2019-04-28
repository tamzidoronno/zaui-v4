/**
 * This class is ment as a thread simply in the GetShop Framework.
 * 
 * Usage:
 * 
 * Create a class that extends this class. Then you can start the thread by 
 * using the .start() function.
 * 
 */
package com.thundashop.core.common;

import com.thundashop.core.usermanager.data.User;

/**
 *
 * @author ktonder
 */
public abstract class GetShopThread implements Runnable {
    private String storeId;
    private String multiLevelName;
    private User userToRunIn;

    public GetShopThread(String storeId, String multiLevelName, User userToRunIn) {
        this.storeId = storeId;
        this.multiLevelName = multiLevelName;
        this.userToRunIn = userToRunIn;
    }

    public void start() {
        Thread td = new Thread(this);
        td.setName("GetShopThread: " + this + " | storeid: " + storeId);
        td.start(); 
    }
    
    @Override
    public void run() {
        StorePool storePool = AppContext.storePool;
        storePool.executeGetShopThread(this);
    }
    
    public abstract void execute();

    public String getStoreId() {
        return storeId;
    }

    public String getMultiLevelName() {
        return this.multiLevelName;
    }

    public String getUserId() {
        if (userToRunIn != null) {
            return userToRunIn.id;
        }
        
        return null;
    }
}

<?php

class StoreManager extends TestBase {
    var $notinitialized = false;
    
    public function StoreManager($api) {
        $this->api = $api;
    }
    
    /**
     * Create your first store.
     */
    public function test_createStore() {
        $storeManager = $this->getApi()->getStoreManager();
        
        //Just inizitalize your store for the fun of it, if it not 
        //already initialized.
        if($this->notinitialized) {
            $store = $storeManager->createStore("http://www.getshop.com", 
                    "test@getshop.com", 
                    "testpassword");
            $storeManager->initializeStore("webshopname.getshop.com", 
                    session_id());
        }
    }
    
    /**
     * Enable extended mode.
     */
    public function test_enableExtendedMode() {
        //Sorry, this function is restricted to immortals only.
        //This is for internal usage only!
    }
    
    /**
     * Enable SMS access.
     */
    public function test_enableSMSAccess() {
        //Sorry, this function is restricted to immortals only.
        //This is for internal usage only!
    }
    
    /**
     * Need your Store object?
     */
    public function test_getMyStore() {
        $storeManager = $this->getApi()->getStoreManager();
        $store = $storeManager->getMyStore();
    }
    
    /**
     * initializeStore, this has to be done to use this api.
     */
    public function test_initializeStore() {
        $storeManager = $this->getApi()->getStoreManager();
        
//        now identify this api to the given webshop address if not 
//        alraedy done.
        if($this->notinitialized) {
            $storeManager->initializeStore("webshopname.getshop.com", 
                    session_id());
        }
    }
    
    /**
     * Want to change the address for your webshop?
     */
    public function test_setPrimaryDomainName() {
        $storeManager = $this->getApi()->getStoreManager();
        $newDomainName = "mynewdomain.com";
        $storeManager->setPrimaryDomainName($newDomainName);
    }
    

    /**
     * Want to remove an already added address to this webshop?
     */
    public function test_removeDomainName() {
        $storeManager = $this->getApi()->getStoreManager();
        
        //First add one you can remove.
        $newDomainName = "toberemoved.com";
        $storeManager->setPrimaryDomainName($newDomainName);
        
        //And now, remove it.
        $storeManager->removeDomainName($newDomainName);
    }
    
    /**
     * Need to fetch your current webshop id?
     * 
     * That is simple.
     */
    public function test_getStoreId() {
        $storeManager = $this->getApi()->getStoreManager();
        $storeId = $storeManager->getStoreId();
    }
    
    /**
     * Do some changes to your current store?
     */
    public function test_saveStore() {
        $storeManager = $this->getApi()->getStoreManager();
        
        //First just fetch it.
        $store = $storeManager->getMyStore();
        
        //Do some changes.
        $store->readIntroduction = true;
        $store->configuration->phoneNumber = "1234123";

        $storeManager->saveStore($store->configuration);
    }
    
    /**
     * The introduction field in the store helps you make sure the introduction
     * has been read.
     */
    public function test_setIntroductionRead() {
        $storeManager = $this->getApi()->getStoreManager();
        $storeManager->setIntroductionRead();
    }
}

?>

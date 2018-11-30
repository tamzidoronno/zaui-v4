<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PageFactory
 *
 * @author ktonder
 */
class PageFactory {
    private static $api;
    
    public static $moduleId;
    
    private $pages = array();
    
    private $productionMode = false;
    
    function __construct($moduleId=false) {
        if (!$moduleId) {
            $moduleId = "pms";
        }
        PageFactory::$moduleId = $moduleId;
        if ($moduleId == "pms") {
            $this->createPmsPages();
        }
        if ($moduleId == "srs") {
            $this->createSrsPages();
        }
        if ($moduleId == "salespoint") {
            $this->createSalesPointPages();
        }
        if ($moduleId == "getshopsupport") {
            $this->createGetShopSupportPages();
        }
        $this->productionMode = $this->getApi()->getStoreManager()->isProductMode();
    }

    /**
     * @return \ModulePage
     */
    public function getPage($pageId) {
        if (!$pageId) {
            $pageId = "home";
        }
        
        
        $page = $this->pages[$pageId];
        $page->createApplicationInstances();
        $page->setModuleId();
        return $page;
    }
    
    /**
     * 
     * @return GetShopApi
     */
    public static function getApi() {
        if (PageFactory::$api == null) {
            $config = new ConfigReader();
            $port = $config->getConfig("port");
            $host = $config->getConfig("backenddb");
            PageFactory::$api = new GetShopApi($port, $host, session_id());    
        }
        
        return PageFactory::$api;
    }
    
    public static function getGetShopModule() {
       
        if (isset($_GET['gs_getshopmodule'])) {
            return $_GET['gs_getshopmodule'];
        }
        
        if (isset($_POST['gs_getshopmodule'])) {
            return $_POST['gs_getshopmodule'];
        }
        
        return null;
    }

    public function createPmsPages() {
        // Bookings
        $page = new \ModulePage("a90a9031-b67d-4d98-b034-f8c201a8f496", "pms");
            
        $row = $page->createRow();
        $row->addText("Booking overview");
        $row->addColumn("1ba01a11-1b79-4d80-8fdd-c7c2e286f94c", "a84dedd4-3aa3-4927-a817-2e026564639f");
        
        $row = $page->createRow();
        $row->addColumn("961efe75-e13b-4c9a-a0ce-8d3906b4bd73", "5a1d4939-65d6-4d18-a60a-b2cc616e03ec");
        $this->pages['a90a9031-b67d-4d98-b034-f8c201a8f496'] = $page;
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        
        // HOME
        $page = new \ModulePage("home", "pms");
        $row = $page->createRow();
        $row->addText("Overview");
        $row = $page->createRow();
        $row->addColumn("f8d72daf-97d8-4be2-84dc-7bec90ad8462", "2e43f480-636a-4842-9769-8a326ee09cce");
        $this->pages['home'] = $page;   
        
        // AVAILABILITY
        $page = new \ModulePage("0da68de9-da08-4b60-9652-3ac456da2627", "pms");
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $row = $page->createRow();
        $row->addText("Availability");
        $row->addColumn("a5599ed1-60be-43f4-85a6-a09d5318638f", "cf33e9b4-9fb8-42f8-b831-f6ba0e2a67ac");

        $row = $page->createRow();
        $row->addColumn("28886d7d-91d6-409a-a455-9351a426bed5", "84e615f5-5524-48a8-b913-b4a35a321383");
        $this->pages['0da68de9-da08-4b60-9652-3ac456da2627'] = $page;   
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    
        
        
        // NEW
        $page = new \ModulePage("4d89b5cf-5a00-46ea-9dcf-46ea0cde32e8", "pms");
        $row = $page->createRow();
        $row->addText("Create a new booking");
        
        $row = $page->createRow();
        $row->addColumn("74220775-43f4-41de-9d6e-64a189d17e35", "bd2b31ad-816f-4b96-89a1-e664a58d0d2a");
        $this->pages['4d89b5cf-5a00-46ea-9dcf-46ea0cde32e8'] = $page;   
        
        // REPORTS
        $page = new \ModulePage("afe687b7-219e-4396-9e7b-2848f5ed034d", "pms");
        
        //Booking apps.
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        //End booking apps
        
        $row = $page->createRow();
        $row->addText("Reports");
        
        $row = $page->createRow();
        $row->addColumn("39fd9a07-94ea-4297-b6e8-01e052e3b8b9", "283bc7cf-207c-4c71-97da-f71d628bef3f");
        $this->pages['afe687b7-219e-4396-9e7b-2848f5ed034d'] = $page;   
        
        // PRICING
        $page = new \ModulePage("394bb905-8448-45c1-8910-e9a60f8aebc5", "pms");
        $row = $page->createRow();
        $row->addText("Pricing");
        
        $row = $page->createRow();
        $row->addColumn("4c8e3fe7-3c81-4a74-b5f6-442f841a0cb1", "d9ceed66-33af-4163-850d-a11e10a01def");
        $this->pages['394bb905-8448-45c1-8910-e9a60f8aebc5'] = $page;   
        
        // CLEANING
        $page = new \ModulePage("e03b19de-d1bf-4d1c-ac40-8c100ef53366", "pms");
        $row = $page->createRow();
        $row->addText("Cleaning");
        
        $row = $page->createRow();
        $row->addColumn("c9a0671d-2eef-4a8e-8e69-523bcfc263e1", "1014ab15-00d1-4d7d-b1a1-aa0f58f34ead");
        $this->pages['e03b19de-d1bf-4d1c-ac40-8c100ef53366'] = $page;   
        
        // GROUP BOOKING
        $page = new \ModulePage("groupbooking", "pms");
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe'); 
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73'); 
        
        $page->createRow()->addText('Group booking');
        
        $row = $page->createRow();
        $row->addColumn("cbcf3e53-c035-43c2-a1ca-c267b4a8180f", "7406cbfc-b046-4b3e-bc5b-b41fd54088cc");
        $this->pages['groupbooking'] = $page;   
                
        // CRM
        $page = new \ModulePage("4f66aad0-08a0-466c-9b4c-71337c1e00b7", "pms");
        
        $page->addExtraApplications('acb219a1-4a76-4ead-b0dd-6f3ba3776421');
//        $page->addExtraApplications('7e828cd0-8b44-4125-ae4f-f61983b01e0a');
        
        $row = $page->createRow();
        $row->addText("CRM");
        
        $row = $page->createRow();
        $row->addColumn("9f8483b1-eed4-4da8-b24b-0f48b71512b9", "cb9ab262-4fe3-489f-a8a0-c4652c1730e0");
        
        $row = $page->createRow();
        $row->addColumn("dcc56763-43cf-470f-87c3-ee305a5a517b", "afa2d7e7-594a-4db4-8242-6253e0a78f1c");
        $this->pages['4f66aad0-08a0-466c-9b4c-71337c1e00b7'] = $page;   
        
        $page = new \ModulePage("checklist", "pms");
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        
        $row = $page->createRow();
        $row->addText("Checklist");
        
        $row = $page->createRow();
        $row->addColumn("24206ea4-45f2-4a08-ac57-ed2c6c8b22f5", "21a90037-1fca-40fb-9ad5-6db53ec65846");
        $this->pages['checklist'] = $page;   
        
        // Settings
        $page = new \ModulePage("messages", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("320ada5b-a53a-46d2-99b2-9b0b26a7105a", "ba5c2f35-b52c-40d8-9ca8-613edd3d2c6b");
        $page->createRow()->addColumn("624fa4ac-9b27-4166-9fc3-5c1d1831b56b", "d8310a2e-bf2e-4088-b25e-fea708babf33");
        $this->pages['messages'] = $page;   
        
        $page = new \ModulePage("paymentmethods", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("d0c34aa6-36c7-40dd-9ef2-4fa8844f442d", "8d6b91c8-50bf-4aa1-9cef-2bc0940a93b8");
        $page->createRow()->addColumn("f474e3f0-7ef6-4611-9202-9332302a5e38", "60ad9666-7f12-4ae6-b896-b0b752db0070");
        $this->pages['paymentmethods'] = $page;   
        
        $page = new \ModulePage("cleaningconfig", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("9e0bbaa4-961b-4235-8a1f-47eb4414ed91", "3d549429-fe6b-42d9-b21d-f7feb28794af");
        $this->pages['cleaningconfig'] = $page;   
        
        $page = new \ModulePage("selfcheckingterminals", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("4b1dc2e3-a4a7-4874-9be7-3ff66e5e5a6c", "0de598a4-2069-496f-a977-131f779c8d6c");
        $this->pages['selfcheckingterminals'] = $page;   
        
        $page = new \ModulePage("productsandaddonsconfig");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("c5a4b5bf-365c-48d1-aeef-480c62edd897", "33710e57-5616-49d5-8e32-a6f95868b3b8");
        $this->pages['productsandaddonsconfig'] = $page;   
        
        $page = new \ModulePage("termsconfig", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("b87159c1-2e10-4be0-a2c8-e702ec92c8eb", "b6b3a4ed-beed-4d39-b7e0-a9c7976d6b26");
        $this->pages['termsconfig'] = $page;   
        
        $page = new \ModulePage("instructionconfig", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("78b6ce1d-95df-4972-b530-e08a89e09c46", "9b19e506-ddd1-4e75-a8b6-2804b0fec0a1");
        $this->pages['instructionconfig'] = $page;   
        
        $page = new \ModulePage("roomsconfig", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("a22747ef-10b1-4f63-bef8-41c02193edd8", "e65b8b59-ebbb-49e7-ba4f-6cfea9f65efc");
        $this->pages['roomsconfig'] = $page;   
        
        $page = new \ModulePage("notifications", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("f29c2731-2574-4ddf-80d1-0bb9ecee3979", "708381fd-063c-4645-9a06-8f6ef3ac3203");
        $this->pages['notifications'] = $page;   
        
        $page = new \ModulePage("budget", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("bb158014-c205-4d6a-a191-3315bdaa78dc", "3b7d5f90-35ad-425b-a9f1-bbdabe54e550");
        $this->pages['budget'] = $page;   
        
        $page = new \ModulePage("triptease", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("e80c6ab9-fd20-44f5-8dd8-0b7bef4d3d8d", "5ddd1316-ea8e-4b56-855e-4fbff98e1e21");
        $page->createRow()->addText("TripTease");
        $page->createRow()->addColumn("c844b3fe-84b0-4d26-a8e2-8aa361ed82c4", "0974cf56-97d7-469e-a7f6-246cafc88ee6");
        $this->pages['triptease'] = $page;   
        
        $page = new \ModulePage("getshopexpressconfig", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("4a282640-6f2c-4f64-acf2-a92f9c06e2d9", "4cb16d52-8b0f-45bc-b81a-97ba527eb1a9");
        $this->pages['getshopexpressconfig'] = $page;   
        
        $page = new \ModulePage("restrictionsconfig");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("7db21d0e-6636-4dd3-a767-48b06932416c", "b8db9451-0e1a-4ec0-8e17-5340cee92270");
        $this->pages['restrictionsconfig'] = $page;   
        
        $page = new \ModulePage("channelmanagerconfig", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("2b4a865c-6aed-416e-bf52-ab6e2428bd1f", "ccb41912-1a87-4407-9b1a-494f5d5e2f94");
        $this->pages['channelmanagerconfig'] = $page;   
        
        $page = new \ModulePage("lockconfig", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("4b10210b-5da3-4b01-9bd9-1e6f0a2c7cfc", "1aef53b3-601c-4af9-8093-a90c2d490fe0");
        $this->pages['lockconfig'] = $page;   
        
        $page = new \ModulePage("globalsettings");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("9cea7eba-7807-4e4c-8d60-e7d58fbad13a", "25400e33-27b4-4763-a329-de8e6173b727");
        $this->pages['globalsettings'] = $page; 
        
        $page = new \ModulePage("pga", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("972b6269-44fb-4dd6-b6f1-c3ecf2d27dd2", "ddfc9204-263e-4858-978a-6737011e82d7");
        $this->pages['pga'] = $page;   
        
        // MODAL
        $page = new \ModulePage("booking_room_view", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("f8cc5247-85bf-4504-b4f3-b39937bd9955", "0a7bd783-97d7-4e4f-a092-4023d94e4f02");
        $this->pages['booking_room_view'] = $page;   
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
      
    }
//
    public function createSrsPages() {
        $page = new \ModulePage("home", "srs");
        $row = $page->createRow();
        $row->addColumn("f8d72daf-97d8-4be2-84dc-7bec90ad8462", "2e43f480-636a-4842-9769-8a326ee09cce");
        $this->pages['home'] = $page;   
        
        $page = new \ModulePage("3a0bc113-d800-4658-a68e-a0086973eb80", "srs");
        $row = $page->createRow();
        $row->addText("Reservations");
        
        $row = $page->createRow();
        $row->addColumn("916aff23-c765-4b8c-9d8f-8783f1b7bd16", "6e38af16-6028-4ec7-a94a-a1be31287705");
        $this->pages['3a0bc113-d800-4658-a68e-a0086973eb80'] = $page;   
        $page->addExtraApplications("e8fedc44-b227-400b-8f4d-52d52e58ecfe");
        
        $page = new \ModulePage("9c87fd8c-e44a-467a-a65b-1734f974a553", "srs");
        $row = $page->createRow();
        $row->addText("Reservations");
        
        $row = $page->createRow();
        $row->addColumn("480bdbdd-4da9-44ca-95c9-2fcb044eaf22", "f807d085-13f3-4421-a94a-5be4ae0148ca");
        $this->pages['9c87fd8c-e44a-467a-a65b-1734f974a553'] = $page;   
    }
    public function createGetShopSupportPages() {
        $page = new \ModulePage("home", "getshopsupport");
        $row = $page->createRow();
        $row->addColumn("84268253-6c1e-4859-86e3-66c7fb157ea1", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("getshopdevcenter", "getshopsupport");
        $row = $page->createRow();
        $row->addColumn("84268253-6c1e-4859-86e3-66c7fb157ea1", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['getshopdevcenter'] = $page;   
        
        $page = new \ModulePage("getshopbillinghistory", "getshopsupport");
        $row = $page->createRow();
        $row->addColumn("84268253-6c1e-4859-86e3-66c7fb157ea1", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['getshopbillinghistory'] = $page;   
    }

    public function createSalesPointPages() {
    
        // Bookings
        $page = new \ModulePage("home", "salespoint");
        $row = $page->createRow();
        $row->addText("DashBoard - Salespoint");
        $this->pages['home'] = $page;
        $row = $page->createRow();
        $row->addColumn("bf312f0d-d204-45e9-9519-a139064ee2a7", "7ec14053-df36-4924-a9c1-6911b018ce74");
        
        $page = new \ModulePage("new", "salespoint");
        $row = $page->createRow();
        $row->addColumn("57db782b-5fe7-478f-956a-ab9eb3575855", "");
        $page->addExtraApplications("11234b3f-452e-42ce-ab52-88426fc48f8d");
        $this->pages['new'] = $page;
        
        $page = new \ModulePage("products", "salespoint");
        $row = $page->createRow();
        $row->addText("Products");
        $row->addColumn("c282cfba-2873-46fd-876b-c44269eb0dfb", "144776d1-6d0f-47a8-a31c-aa8c3c40799f");
        $row = $page->createRow();
        $row->addColumn("0c6398b0-c301-481a-b4e7-faea0376e822", "90bc3ac3-a260-41e6-9831-68ce0eabc28e");
        $page->addExtraApplications("4404dc7d-e68a-4fd5-bd98-39813974a606");
        $this->pages['products'] = $page;
        
        $page = new \ModulePage("paymentmodal", "salespoint");
        $row = $page->createRow();
        $row->addColumn("11234b3f-452e-42ce-ab52-88426fc48f8d", "693a528e-2364-40df-a7a0-dcc765aa46dd");
        $this->pages['paymentmodal'] = $page;
        
        $page = new \ModulePage("reports", "salespoint");
        $row = $page->createRow();
        $row->addColumn("c20ea6e2-bc0b-4fe1-b92a-0c73b67aead7", "c565d07f-51bb-46ee-b006-6bf42940fcaf");
        $this->pages['reports'] = $page;
        
        $page = new \ModulePage("settings", "salespoint");
        $page->setLeftMenu(\ModulePageMenu::getSalesPointSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("26958edf-e2a1-4e76-aca0-38e7edfe8c80", "e4a9014e-10d1-4ebf-9c00-6b901d64e6f0");
        $this->pages['settings'] = $page;
        
        $page = new \ModulePage("devices", "salespoint");
        $page->setLeftMenu(\ModulePageMenu::getSalesPointSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("26958edf-e2a1-4e76-aca0-38e7edfe8c80", "e4a9014e-10d1-4ebf-9c00-6b901d64e6f0");
        $this->pages['devices'] = $page;
        
        $page = new \ModulePage("cashpoints", "salespoint");
        $page->setLeftMenu(\ModulePageMenu::getSalesPointSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("5532e18a-3e3d-4804-8ded-30bbb33e5bd5", "1e379f14-c842-405e-a45b-9a0ebef4e6b1");
        $this->pages['cashpoints'] = $page;
        
    }
}
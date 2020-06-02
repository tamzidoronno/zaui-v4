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
        if ($moduleId == "pmsconference") { $this->createPmsConferencePages(); }
        if ($moduleId == "comfort") {
            $this->createComfortPages();
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
        if ($moduleId == "intranet") {
            $this->createIntranetPages();
        }
        if ($moduleId == "apac") {
            $this->createApacPages();
        }
        if ($moduleId == "invoicing") {
            $this->createInvoicingPages();
        }
        if ($moduleId == "settings") {
            $this->createSettingsPages();
        }
        if ($moduleId == "getshop") {
            $this->createGetShopPages();
        }
        if ($moduleId == "pga") {
            $this->createPgaPages();
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
    
    
    public function createComfortPages() {
        $page = new \ModulePage("home", "comfort");
        $row = $page->createRow();
        $row->addColumn("8f7198af-bd49-415a-8c39-9d6762ef1440", "f68d5145-e4c9-4430-9b25-69df5c8458d2");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("gateways", "comfort");
        $row = $page->createRow();
        $row->addColumn("d40f9875-95e5-4ee2-a041-989efa0f4f5c", "2f3a799d-db60-420f-9c90-f0b2d2cfd8a1");
        $this->pages['gateways'] = $page;   
        
        $page = new \ModulePage("configuration", "comfort");
        $page->setLeftMenu(\ModulePageMenu::getComfortLeftMenu());
        $row = $page->createRow();
        $row->addColumn("1cd5d045-04d8-4d37-83a8-47f8fcc6b49c", "4d09439a-f918-44c3-aec0-b4b4bae16c52");
        $this->pages['configuration'] = $page;   
        
        
        
        
        $page = new \ModulePage("comfortunits", "comfort");
        $page->setLeftMenu(\ModulePageMenu::getComfortLeftMenu());
        $row = $page->createRow();
        $row->addColumn("f462df2e-2a2e-4e3b-9bfc-13ff2cf03f4f", "1a058488-58b8-46e8-b886-a5eac815537b");
        $this->pages['comfortunits'] = $page;   
        
        $page = new \ModulePage("roomconfiguration", "comfort");
        $page->setLeftMenu(\ModulePageMenu::getComfortLeftMenu());
        $row = $page->createRow();
        $row->addColumn("aed99d87-1cc0-4b26-bddc-5583ea260392", "30b8d7f2-0bfb-4ca7-830a-54bdad0aa1ca");
        $this->pages['roomconfiguration'] = $page;   
        
        
        $page = new \ModulePage("statesconfig", "comfort");
        $page->setLeftMenu(\ModulePageMenu::getComfortLeftMenu());
        $row = $page->createRow();
        $row->addColumn("39f77b93-59f5-4178-9963-34ba254aea42", "aa5852ea-bcad-4421-9699-ea56f2328547");
        $this->pages['statesconfig'] = $page;   
        
        
        
        
    }

    public function createPmsPages() {
        // Bookings
        $this->createOrderViewPage("pms");
        
        $page = new \ModulePage("a90a9031-b67d-4d98-b034-f8c201a8f496", "pms");
            
        $row = $page->createRow();
        $row->addText("Booking overview");
        $row->addColumn("1ba01a11-1b79-4d80-8fdd-c7c2e286f94c", "a84dedd4-3aa3-4927-a817-2e026564639f");
        
        $row = $page->createRow();
        $row->addColumn("961efe75-e13b-4c9a-a0ce-8d3906b4bd73", "5a1d4939-65d6-4d18-a60a-b2cc616e03ec");
        $this->pages['a90a9031-b67d-4d98-b034-f8c201a8f496'] = $page;
        
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        
        // HOME
        $page = new \ModulePage("home", "pms");
        $row = $page->createRow();
        $row->addText("Overview");
        $row = $page->createRow();
        $row->addColumn("f8d72daf-97d8-4be2-84dc-7bec90ad8462", "2e43f480-636a-4842-9769-8a326ee09cce");
        $this->pages['home'] = $page;   
        
        // Monthly payment link
        $page = new \ModulePage("monthlypaymentlinks", "pms");
        $row = $page->createRow();
        $row->addText("Monthly payment requests");
        $row = $page->createRow();
        $row->addColumn("9ab6923e-3d6b-4b7c-b94e-c14e5ebe5364", "fd90cc1d-327c-42e6-8b53-9dfa0c7ddf2a");
        $this->pages['monthlypaymentlinks'] = $page;
        
        // AVAILABILITY
        $page = new \ModulePage("0da68de9-da08-4b60-9652-3ac456da2627", "pms");
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        $row = $page->createRow();
        $row->addText("Availability");
        $row->addColumn("a5599ed1-60be-43f4-85a6-a09d5318638f", "cf33e9b4-9fb8-42f8-b831-f6ba0e2a67ac");

        $row = $page->createRow();
        $row->addColumn("28886d7d-91d6-409a-a455-9351a426bed5", "84e615f5-5524-48a8-b913-b4a35a321383");
        $this->pages['0da68de9-da08-4b60-9652-3ac456da2627'] = $page;   
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    
        
        // endofdayreport
        $page = new \ModulePage("endofdayreport", "pms");
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        $row = $page->createRow(true);
        
        $row->addColumn("c20ea6e2-bc0b-4fe1-b92a-0c73b67aead7", "c20ea6e2-bc0b-4fe1-b92a-0c73b67aead7");

        $this->pages['endofdayreport'] = $page;   
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    
        
        // ConferenceList
        $page = new \ModulePage("conferencelist", "pms");
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        
        $row = $page->createRow(true);
        
        $row->addColumn("dbe8930f-05d9-44f7-b399-4e683389f5cc", "d3e168b2-c10e-4750-a330-527d98906aa8");
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    
        
        $this->pages['conferencelist'] = $page;   
        
        // timelinereport
        $page = new \ModulePage("timelinereport", "pms");
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        
        $row = $page->createRow(true);
        
        $row->addColumn("dbe8930f-05d9-44f7-b399-4e683389f5cc", "d3e168b2-c10e-4750-a330-527d98906aa8");
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    
        
        $this->pages['timelinereport'] = $page;   
        
        
        // ConferenceList
        $page = new \ModulePage("conferencereports", "pms");
        $page->addExtraApplications('961efe75-e13b-4c9a-a0ce-8d3906b4bd73');    
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        
        $row = $page->createRow(true);
        
        $row->addColumn("02b94bcd-39b9-41aa-b40c-348a27ca5d9d", "47c8c64e-6066-11ea-bc55-0242ac130003");
        
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    
        
        $this->pages['conferencereports'] = $page;   
        
        
        // NEW
        $page = new \ModulePage("4d89b5cf-5a00-46ea-9dcf-46ea0cde32e8", "pms");
        $row = $page->createRow();
//        $row->addText("Create a new booking");
        
        $row = $page->createRow();
        $row->addColumn("74220775-43f4-41de-9d6e-64a189d17e35", "bd2b31ad-816f-4b96-89a1-e664a58d0d2a");
        $this->pages['4d89b5cf-5a00-46ea-9dcf-46ea0cde32e8'] = $page;   
        
        // NEW
        $page = new \ModulePage("048e2e10-1be3-4d77-a235-4b47e3ebfaab", "pms");
        $row = $page->createRow();
//        $row->addText("Create a new booking");
        
        $row = $page->createRow();
        $row->addColumn("bf644a39-c932-4e3b-a6c7-f6fd16baa34d", "21a412a6-b15e-4524-85bf-a36f83cdf11a");
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $this->pages['048e2e10-1be3-4d77-a235-4b47e3ebfaab'] = $page;   
        
        // REPORTS
        $page = new \ModulePage("afe687b7-219e-4396-9e7b-2848f5ed034d", "pms");
        
        //Booking apps.
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
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
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
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
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
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
        
        // Conference
        $page = new \ModulePage("conference", "pms");
        $page->createRow()->addColumn("02b94bcd-39b9-41aa-b40c-348a27ca5d9d", "63c60be1-3d89-4a33-bbdd-8146af81b5f7");
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $this->pages['conference'] = $page;   
        
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
        
        $page = new \ModulePage("segmentation", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("2399034c-bdc3-4dd6-87c4-df297d55bb2d", "4f1bfa5b-8f5a-4cbc-acad-979362fadb0c");
        $this->pages['segmentation'] = $page;   
        
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
        
        // MODAL
        $page = new \ModulePage("booking_room_view_new", "pms");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01", "0a7bd783-97d7-4e4f-a092-4023d94e4f04");
        $this->pages['booking_room_view_new'] = $page;   
        
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
        $row = $page->createRow(true);
        $row->addColumn("a5175115-187a-4721-90e5-4752fa52ca7a", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("getshopdevcenter", "getshopsupport");
        $row = $page->createRow(true);
        $row->addColumn("13c0bc5f-ce62-45c5-be76-90237d16de91", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['getshopdevcenter'] = $page;   
        
        $page = new \ModulePage("supportreport", "getshopsupport");
        $row = $page->createRow(true);
        $row->addColumn("b372a413-dbbe-44c0-b473-13fd48e2d1ff", "07f22a21-091d-470d-ac42-5c0b34519db7");
        $this->pages['supportreport'] = $page;   
        
        $page = new \ModulePage("ticketview", "getshopsupport");
        $row = $page->createRow(true);
        $row->addColumn("f5e525cc-f11e-4611-93bb-1afacd9aade5", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['ticketview'] = $page;   
        
        $page = new \ModulePage("systemsetup", "getshopsupport");
        $row = $page->createRow(true);
        $row->addColumn("13c0bc5f-ce62-45c5-be76-90237d16de91", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['systemsetup'] = $page;   
    }

    public function createSalesPointPages() {
        $this->createOrderViewPage("salespoint");
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
        $page->addExtraApplications("b5e9370e-121f-414d-bda2-74df44010c3b");
        $page->addExtraApplications("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        $this->pages['new'] = $page;
        
        $this->createProductsPage("salespoint");
        
        $page = new \ModulePage("paymentmodal", "salespoint");
        $page->addExtraApplications("b5e9370e-121f-414d-bda2-74df44010c3b");
        
        $row = $page->createRow();
        $row->addColumn("11234b3f-452e-42ce-ab52-88426fc48f8d", "693a528e-2364-40df-a7a0-dcc765aa46dd");
        $this->pages['paymentmodal'] = $page;
        
        $page = new \ModulePage("reports", "salespoint");
        $row = $page->createRow();
        $row->addColumn("c20ea6e2-bc0b-4fe1-b92a-0c73b67aead7", "c565d07f-51bb-46ee-b006-6bf42940fcaf");
        
        $page->addExtraApplications('3e2bc00a-4d7c-44f4-a1ea-4b1b953d8c01');    
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    

        
        $this->pages['reports'] = $page;
        
        // MODAL
        $page = new \ModulePage("booking_room_view", "settings");
        $page->createRow()->addColumn("f8cc5247-85bf-4504-b4f3-b39937bd9955", "0a7bd783-97d7-4e4f-a092-4023d94e4f02");
        $page->addExtraApplications('f8cc5247-85bf-4504-b4f3-b39937bd9955');
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $page->addExtraApplications('28886d7d-91d6-409a-a455-9351a426bed5');
        $page->addExtraApplications('b72ec093-caa2-4bd8-9f32-e826e335894e');
        $page->addExtraApplications('9a6ea395-8dc9-4f27-99c5-87ccc6b5793d');
        $page->addExtraApplications('2e51d163-8ed2-4c9a-a420-02c47b1f7d67');
        $page->addExtraApplications('bce90759-5488-442b-b46c-a6585f353cfe');    
        $page->addExtraApplications('e8fedc44-b227-400b-8f4d-52d52e58ecfe');    
        $this->pages['booking_room_view'] = $page;   
        
        $page = new \ModulePage("invoicing", "salespoint");
        $row = $page->createRow();
        $row->addColumn("cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba", "9d534e1b-d856-4a81-97d8-970d4d79a226");
        $this->pages['invoicing'] = $page;
        
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
        
        $page = new \ModulePage("views", "salespoint");
        $page->setLeftMenu(\ModulePageMenu::getSalesPointSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("7a273876-7390-45b3-95c3-79d3c0cec4d3", "1c1ab07d-2efa-443c-93b6-b691d191094c");
        $this->pages['views'] = $page;
        
        $page = new \ModulePage("tables", "salespoint");
        $page->setLeftMenu(\ModulePageMenu::getSalesPointSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("300e2528-8518-411b-b343-28ad83eced77", "259188e6-3cba-4829-baa5-4e3eec71caf0");
        $this->pages['tables'] = $page;
        
        $page = new \ModulePage("othersettings", "salespoint");
        $page->setLeftMenu(\ModulePageMenu::getSalesPointSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("47dec929-fc34-4d7d-9356-a9600d04797e", "98cb6ffa-ad8e-4aa0-8837-5abdc0e2610c");
        $this->pages['othersettings'] = $page;
        
        $page = new \ModulePage("warehouses", "salespoint");
        $page->setLeftMenu(\ModulePageMenu::getSalesPointSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("1c81b9d3-1d7c-47e3-a428-9183804f4549", "18cb6ffa-ad8e-4aa0-8837-5abdc0e2610c");
        $this->pages['warehouses'] = $page;
        
        $page = new \ModulePage("giftcards", "salespoint");
        $row = $page->createRow();
        $row->addColumn("8a98611e-bfb4-437e-af0d-561a882b0777", "1dabc933-bfa4-4ccd-baf9-2cb6e91d849d");
        $this->pages['giftcards'] = $page;
        
        $page = new \ModulePage("warehouse", "salespoint");
        $row = $page->createRow(true);
        $row->addColumn("2fa3ce10-3637-43d5-a2c7-8e9152fab41a", "1dabc933-bfa4-4ccd-baf9-2c16e91d849d");
        $this->pages['warehouse'] = $page;
    }

    public function createIntranetPages() {
        $page = new \ModulePage("home", "intranet");
        $row = $page->createRow();
        $row->addColumn("84268253-6c1e-4859-86e3-66c7fb157ea1", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("getshopdevcenter", "intranet");
        $row = $page->createRow();
        $row->addColumn("84268253-6c1e-4859-86e3-66c7fb157ea1", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['getshopdevcenter'] = $page;   
        
        $page = new \ModulePage("serverstatus", "intranet");
        $row = $page->createRow();
        $row->addColumn("89a38877-4b75-456c-9dc8-a55ff0e7dfef", "7773ec7f-671d-45cc-b8e5-1367084ea01f");
        $this->pages['serverstatus'] = $page;   
        
        $page = new \ModulePage("getshopbillinghistory", "intranet");
        $row = $page->createRow();
        $row->addColumn("84268253-6c1e-4859-86e3-66c7fb157ea1", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['getshopbillinghistory'] = $page;   
        
        $page = new \ModulePage("getshopusermanual", "intranet");
        $row = $page->createRow();
        $row->addColumn("84268253-6c1e-4859-86e3-66c7fb157ea1", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['getshopusermanual'] = $page;
    }

    public function createApacPages() {
        $page = new \ModulePage("home", "apac");
        $page->addExtraApplications('0cf90108-6e9f-49fd-abfe-7541d1526ba2');
        $row = $page->createRow();
        $row->addText("Access List");
        $row = $page->createRow();
        $row->addColumn("93a55f7a-07ef-4199-8ab2-1a3019c160cd", "44879835-95ab-42b5-a531-f480a7688c45");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("newaccess", "apac");
        $page->addExtraApplications('0cf90108-6e9f-49fd-abfe-7541d1526ba2');
        $row = $page->createRow();
        $row->addColumn("b698ac77-15f3-45b7-a412-47186a2defb6", "666ac2e7-d72c-4806-944e-5dd25ce20ee6");
        $this->pages['newaccess'] = $page;
        
        $page = new \ModulePage("apac_access_user_view", "apac");
        $row = $page->createRow();
        $row->addText("Acces Code");
        $row = $page->createRow();
        $row->addColumn("0cf90108-6e9f-49fd-abfe-7541d1526ba2", "663ac2e7-d72c-4806-944e-5dd25ce20ee6");
        $this->pages['apac_access_user_view'] = $page;
        
        $page = new \ModulePage("accessgroups", "apac");
        $page->setLeftMenu(\ModulePageMenu::getApacLeftMenu());
        $row = $page->createRow();
        $row->addColumn("25c15968-4b9b-4c23-9e44-dc5cdb83244c", "a6472054-66eb-46e2-90fb-ea34783ea001");
        $this->pages['accessgroups'] = $page;
        
        $page = new \ModulePage("lockssetting", "apac");
        $page->setLeftMenu(\ModulePageMenu::getApacLeftMenu());
        $row = $page->createRow();
        $row->addColumn("01b8bd0c-0375-42fa-a44e-d177485db704", "401be417-a9db-455f-9233-bcd93b9260b7");
        $this->pages['lockssetting'] = $page;
        
        $page = new \ModulePage("gensettings", "apac");
        $page->setLeftMenu(\ModulePageMenu::getApacLeftMenu());
        $row = $page->createRow();
        $row->addColumn("df05feab-f657-49ee-a338-82d5f8c14ed5", "3c17718a-76f5-4e56-b02a-3b8c7e61c28b");
        $this->pages['gensettings'] = $page;
        
        $page = new \ModulePage("doors", "apac");
        $row = $page->createRow();
        $row = $page->createRow();
        $row->addColumn("4bab2f13-491b-4c34-973c-e776ca2d88d6", "741f98aa-e8ed-4423-b41f-1ba63d1bc526");
        $this->pages['doors'] = $page;
        
        $page = new \ModulePage("configuration", "apac");
        $page->setLeftMenu(\ModulePageMenu::getApacLeftMenu());
        $row = $page->createRow();
        $row->addText("Gateways");
        
        $row = $page->createRow();
        $row->addColumn("2d6a27b9-b238-4406-9f03-c4ca8184f590", "d6529811-8d13-4771-8631-b4fab9fbfed7");
        $this->pages['configuration'] = $page;
    }
    
    private function createProductsPage($modulename) {
        $page = new \ModulePage("products", $modulename);
        $row = $page->createRow();
        $row->addText("Products");
        $row->addColumn("c282cfba-2873-46fd-876b-c44269eb0dfb", "144776d1-6d0f-47a8-a31c-aa8c3c40799f");
        $row = $page->createRow();
        $row->addColumn("0c6398b0-c301-481a-b4e7-faea0376e822", "90bc3ac3-a260-41e6-9831-68ce0eabc28e");
        $page->addExtraApplications("4404dc7d-e68a-4fd5-bd98-39813974a606");
        $this->pages['products'] = $page;
    }

    private function createOrderViewPage($modulename) {
        $page = new \ModulePage("orderviewpage", $modulename);
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row = $page->createRow(true);
        $row->addColumn("4be8e427-bead-491e-8d9f-7dd16356d8eb", "ab0b52a7-8745-4c69-8024-f3890a2849c0");
        $this->pages['orderviewpage'] = $page;
    }
    
    public function createInvoicingPages() {
        $page = new \ModulePage("home", "invoicing");
        $row = $page->createRow();
        $row->addText("Dashboard");
        $row = $page->createRow();
        $row->addColumn("f9842d40-5f0a-4b48-86b2-84f314f5f025", "f2aaea3f-9bf7-444e-b502-374af6d504bd");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("overduelist", "invoicing");
        $row = $page->createRow();
        $row->addText("Overdue invoices");
        $row = $page->createRow();
        $row->addColumn("b7fb195b-8cea-4d7b-922e-dee665940de2", "c709209b-4765-4cde-82e9-7e372302b560");
        $this->pages['overduelist'] = $page;
        
        $page = new \ModulePage("allinvoices", "invoicing");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("0775b147-b913-43cd-b9f4-a2a721ad3277", "2888fc8f-de8f-45c8-970d-cf74ec3b88e2");
        $this->pages['allinvoices'] = $page;
        
        $page = new \ModulePage("incomes", "invoicing");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("6c5268a0-26ea-4905-8f23-79f5410912a8", "dd11ceff-c204-41c7-8d14-bf3cbff7245b");
        $this->pages['incomes'] = $page;
        
        $page = new \ModulePage("pms", "invoicing");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("6f51a352-a5ee-45ca-a8e2-e187ad1c02a5", "dd11cegf-c204-41c7-8c14-b13cbff7245b");
        $this->pages['pms'] = $page;
        
        $page = new \ModulePage("booking_room_view", "invoicing");
        $page->setLeftMenu(\ModulePageMenu::getPmsLeftMenu());
        $page->createRow()->addColumn("f8cc5247-85bf-4504-b4f3-b39937bd9955", "0a7bd783-97d7-4e4f-a092-4023d94e4f02");
        $this->pages['booking_room_view'] = $page;   
        
        $page = new \ModulePage("invoicing", "invoicing");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba", "9d534e1b-d856-4a81-97d8-970d4d79a226");
        $this->pages['invoicing'] = $page;
        
        $this->createOrderViewPage('invoicing');
        $this->createProductsPage("invoicing");
    }

    public function createSettingsPages() {
        $page = new \ModulePage("home", "settings");
        $row = $page->createRow();
        $row->addText("Dashboard");
        $row = $page->createRow();
        $row->addColumn("80ddbee0-09ee-4b05-8e2d-01c7055b9ab3", "50357490-7764-42d1-8438-77d34928a718");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("othersettings", "settings");
        $page->setLeftMenu(\ModulePageMenu::getSettingsLeftMenu());
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("afbe1ef5-6c62-45c7-a5a0-fd16d380d7cb", "c34a7143-711b-4c0f-ad4b-95e689330fa4");
        $this->pages[$page->getId()] = $page;
        
        $page = new \ModulePage("departments", "settings");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("d3bd5a9e-2e8d-4992-b6c4-aacec6ae284e", "4db0d49a-b041-4016-a8ec-b9852d40e40b");
        $this->pages[$page->getId()] = $page;
        
        $page = new \ModulePage("segments", "settings");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("45779056-ad4a-44ce-8c9a-1a4adc75477b", "a4af40d2-6efd-46f1-85e1-a1b5a3b47435");
        $this->pages[$page->getId()] = $page;
        
        $page = new \ModulePage("useraccounts", "settings");
        $page->setLeftMenu(\ModulePageMenu::getSettingsLeftMenu());
        $page->addExtraApplications('acb219a1-4a76-4ead-b0dd-6f3ba3776421');
        $row = $page->createRow();
        $row->addColumn("27656859-aeed-41f7-9941-f01d0f860212", "f05c190e-2ba5-4604-b8cc-ffe93647e46c");
        $this->pages[$page->getId()] = $page;
        
        
        $page = new \ModulePage("mailsettings", "settings");
        $page->setLeftMenu(\ModulePageMenu::getSettingsLeftMenu());
        $row = $page->createRow();
        $row->addColumn("8ad8243c-b9c1-48d4-96d5-7382fa2e24cd", "478ba23d-1587-4e59-a749-936148b2c2b7");
        $this->pages[$page->getId()] = $page;
        
    }
    
    public function createGetShopPages() {
        $page = new \ModulePage("home", "getshop");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("80ddbee0-09ee-4b05-8e2d-01c7055b9ab3", "50357490-7764-42d1-8438-77d34928a718");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("customers", "getshop");
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("2f62f832-5adb-407f-a88e-208248117017", "50357490-7764-42d1-8438-72d34928a718");
        $row->addColumn("a22fa681-6882-4869-8add-b1cc9c7b661b", "40357490-7764-42d1-8438-72d34928a718");
        $this->pages['customers'] = $page;
        
        $page = new \ModulePage("inbox", "getshop");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn('f1706b4c-f779-4eb7-aec3-ee08f182e090', "40357190-7764-42d1-8436-72d34928a718");
        $this->pages['inbox'] = $page;
        
        $page = new \ModulePage("invoicing", "getshop");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn('339af689-1617-4d67-ade9-ca26cf55bf44', "40357190-7764-42d1-1436-72d34928a718");
        $this->pages['invoicing'] = $page;
        
        
        $page = new \ModulePage("iotdevices", "getshop");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn('ca4162a4-b26b-4920-8d51-80b809546167', "c2189ee9-ae84-473a-8df5-32534f74d0bc");
        $this->pages['iotdevices'] = $page;
        
        $page = new \ModulePage("inventory", "getshop");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn('2a608d02-15d8-422b-9089-3082dc7e9123', "3bf0215f-9ff3-468a-ac1e-20019c8c0190");
        $this->pages['inventory'] = $page;
        
        $page = new \ModulePage("sales", "getshop");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn('514d1d25-ea8b-4872-b010-e282c3d3db3e', "a271f180-1cf6-4a41-b87b-ee38d753d344");
        $this->pages['sales'] = $page;
        
        $page = new \ModulePage("ticketview", "getshop");
        $row = $page->createRow(true);
        $row->addColumn("f5e525cc-f11e-4611-93bb-1afacd9aade5", "c4eb8022-f405-11e8-8eb2-f2801f1b9fd1");
        $this->pages['ticketview'] = $page;   
    }

    public function addExtraApplicationsNoneInstance($id) {
        foreach ($this->pages as $page) {
            $page->addExtraApplicationsNoneInstance($id);
        }
    }

    public function createPmsConferencePages() {
        $page = new \ModulePage("home", "pmsconference");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row->addColumn("02b94bcd-39b9-41aa-b40c-348a27ca5d9d", "c2bc0427-6182-45d4-b61d-78f192d2b1d5");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("eventreport", "pmsconference");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row->addColumn("02b94bcd-39b9-41aa-b40c-348a27ca5d9d", "c2bc0427-6182-45d4-b61d-78f192d2b1d5");
        $this->pages['eventreport'] = $page;
        
        $page = new \ModulePage("report", "pmsconference");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row->addColumn("02b94bcd-39b9-41aa-b40c-348a27ca5d9d", "c2bc0427-6182-45d4-b61d-78f192d2b1d5");
        $this->pages['report'] = $page;
        
        $page = new \ModulePage("list", "pmsconference");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row->addColumn("d3e168b2-c10e-4750-a330-527d98906aa8", "c2bc0427-6182-45d2-b11d-78f192d2b1d5");
        $this->pages['list'] = $page;
        
        $page = new \ModulePage("conference", "pmsconference");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row->addColumn("28befd67-e4ea-412b-a67a-23b1aa10781c", "32acx427-6182-45d2-b11d-78f192d2b1d1");
        $this->pages['conference'] = $page;
        
        $page = new \ModulePage("new", "pmsconference");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $page->addExtraApplications('b5e9370e-121f-414d-bda2-74df44010c3b');
        $row->addColumn("66e0c307-5a6d-4c16-8ed6-5204d63d5675", "z2a1x427-6182-45d2-b11d-18f152d2b1d1");
        $this->pages['new'] = $page;
        
    }

    public function createPgaPages() {
        $page = new \ModulePage("home", "pga");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("2f998ecd-72e2-4b44-8529-cc8d6e5b2d15", "1f998ecd-72e2-4b44-8529-cc8d6e5b2d15");
        $this->pages['home'] = $page;
        
        $page = new \ModulePage("conference", "pga");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("b01782d0-5181-4b12-bec8-ee2e844bcae5", "1f998ecd-72e2-4b44-8529-cc8d6e5b2d12");
        $this->pages['conference'] = $page;
        
        $page = new \ModulePage("rooms", "pga");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("96ee60e7-4f5d-4084-a2c7-ac6aa7e53bc0", "1f998ecd-72e2-4b44-8529-cc8d6e5b2d13");
        $this->pages['rooms'] = $page;
        
        
        $page = new \ModulePage("billing", "pga");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("d049425e-1718-48a0-b61b-950492638a14", "1f998ecd-72e2-4b44-8529-cc8d6e5b2d14");
        $this->pages['billing'] = $page;
        
        $page = new \ModulePage("updateguestinformation", "pga");
        $row = $page->createRow();
        $row->ignoreTopRow = true;
        $row->addColumn("d8ac717e-8e03-4b59-a2c3-e61b064a21c2", "1f998ecd-72e2-4b44-8529-cc8d6e5b2d19");
        $this->pages['updateguestinformation'] = $page;
        
    }

}
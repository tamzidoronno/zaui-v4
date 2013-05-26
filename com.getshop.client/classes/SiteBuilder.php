<?php

class SiteBuilder extends ApplicationBase {
    /*  @var $api GetShopApi */

    var $api;

    function __construct() {
        $this->api = $this->getApi();
    }

    /**
     * Add a banner slider
     * @param type $pageId The page you would like to add it to
     * @param type $path The path where to find the images.
     */
    public function addBannerSlider($pageId, $path, $height) {
        $bannerSlider = $this->api->getPageManager()->addApplicationToPage($pageId, "d612904c-8e44-4ec0-abf9-c03b62159ce4", "middle");
        $id = $bannerSlider->id;
        $this->api->getPageManager()->reorderApplication("home", $id, true);
        $data = file_get_contents($path . "banner1.png");
        $fileId = \FileUpload::storeFile($data);
        $this->api->getBannerManager()->addImage($id, $fileId);

        $data2 = file_get_contents($path . "banner2.png");
        $fileId2 = \FileUpload::storeFile($data2);
        $this->api->getBannerManager()->addImage($id, $fileId2);

        $data3 = file_get_contents($path . "banner3.png");
        $fileId3 = \FileUpload::storeFile($data3);
        $this->api->getBannerManager()->addImage($id, $fileId3);        
        
        $set = $this->api->getBannerManager()->getSet($id);
        $set->height = $height;
        $set->width = 998;
        $this->api->getBannerManager()->saveSet($set);
    }

    public function addFooterContent($content) {
        $config = $this->api->getFooterManager()->getConfiguration();
        $id = $config->columnIds->{0};
        $this->api->getFooterManager()->setLayout(1);
        $this->api->getContentManager()->saveContent($id, $content);
    }
    
    /**
     * Add a view
     * @param type $pageId
     * @param type $type 1 = New list, 2 = latest products
     * @param type $view listview / boxview / rowview
     */
    public function addProductList($pageId, $type, $view) {
        /* @var $appConfig core_applicationmanager_ApplicationSettings */
        $appConfig = $this->api->getPageManager()->addApplicationToPage($pageId, "8402f800-1e7e-43b5-b3f7-6c7cabbf8942", "middle");
        $app = new ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942\ProductList();
        $app->setConfiguration($appConfig);
        $_POST['data']['type' . $type] = "true";
        $_POST['data'][$view] = 1;
        $app->applicationAdded();
        unset($_POST['data']['type' . $type]);
        unset($_POST['data'][$view]);
    }

    public function addContactForm($pageId) {
        $appConfig = $this->api->getPageManager()->addApplicationToPage($pageId, "96de3d91-41f2-4236-a469-cd1015b233fc", "middle");
    }
    
    public function addContentManager($pageId, $content, $where = "middle") {
        $appConfig = $this->api->getPageManager()->addApplicationToPage($pageId, "320ada5b-a53a-46d2-99b2-9b0b26a7105a", $where);
        if ($content) {
            $this->api->getContentManager()->saveContent($appConfig->id, $content);
        } else {
            $app = new ns_320ada5b_a53a_46d2_99b2_9b0b26a7105a\ContentManager();
            $app->setConfiguration($appConfig);
            $app->applicationAdded();
        }
        return $appConfig;
    }

    public function clearTopMenu() {
        $topmenu = $this->getTopMenu();
        $listId = $topmenu->getConfiguration()->id;
        $list = $this->getApi()->getListManager()->getList($listId);
        foreach ($list as $entry) {
            if ($entry->pageId != "home") {
                $this->getApi()->getListManager()->deleteEntry($entry->id, $listId);
            }
        }
    }
    

    /**
     * Create a new page.
     * @param type $name The name of the page
     * @return String the id of the page
     */
    public function createPage($name, $layout=4) {
        $page = $this->api->getPageManager()->createPage($layout, "");
        $topmenu = $this->getTopMenu();
        $entry = new core_listmanager_data_Entry();
        $entry->name = $name;
        $entry->pageId = $page->id;
        $this->api->getListManager()->addEntry($topmenu->getConfiguration()->id, $entry, "");
        return $page->id;
    }

    public function clearPage($id) {
        $this->api->getPageManager()->createPageWithId(1, null, $id);
        $this->api->getPageManager()->clearPageArea($id, "left");
        $this->api->getPageManager()->clearPageArea($id, "middle");
        $this->api->getPageManager()->clearPageArea($id, "right");
        $this->api->getPageManager()->clearPageArea($id, "bottom");
        $this->api->getPageManager()->changePageLayout($id, 4);
    }

   public function getTopMenu() {
        $added = $this->getFactory()->getApplicationPool()->getAllAddedInstances();
        foreach ($added as $instance) {
            if ($instance->applicationSettings->id == "1051b4cf-6e9f-475d-aa12-fc83a89d2fd4") {
                return $instance;
            }
        }
    }
    
    public function prepopulateData() {
        //Set default color to blue.
        $this->getFactory()->setConfigurationFlag("color", "blue");
        
        $store = $this->getApi()->getStoreManager()->getMyStore();
        $store->configuration->colors->textColor = "000000";
        $store->configuration->colors->buttonBackgroundColor = "BBBBBB";
        $store->configuration->colors->buttonTextColor = "FFFFFF";
        $store->configuration->colors->backgroundColor = "FFFFFF";
        $this->getApi()->getStoreManager()->saveStore($store->configuration);
         
        $this->clearTopMenu();
        $this->clearPage('home');
        $this->addBannerSlider("home", "designimages/banners/", 400);
        $this->addProductList('home', 2, "rowview");
        $productPage = $this->createPage("My products");
        $this->addProductList($productPage, 1, "listview");
        $aboutus = $this->createPage("About us");
        $this->addContentManager($aboutus, null);
        $contact = $this->createPage("Contact us");
        $this->addContactForm($contact);
        $this->addFooterContent('<table border="0" cellpadding="0" cellspacing="0" style="width: 100%;" class=" cke_show_border"><tbody><tr><td style="vertical-align: top;"><br><span><span style="font-size:12px;">My web shop name<br>My street 1<br>My country</span></span></td><td style="text-align: right;"><br><span><span style="font-size:12px;">Some other information<br>Conact information<br>1234567<br>post@mail.com</span></span><br><br></td></tr></tbody></table>');
    }
}

?>

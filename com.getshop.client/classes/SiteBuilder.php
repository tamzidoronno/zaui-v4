<?php

class SiteBuilder extends ApplicationBase {
    /*  @var $api GetShopApi */

    var $api;
    var $counterYoutube = 0;
    private $rowSize;
    private $imageCount = 0;
    private $rowImageCount = -1;
    private $productWidgetCount = 0;
    private $page;
    private $imageIds;
    private $textIndex = -1;
    
    function __construct($page = null) {
        if($page) {
            $this->page = $page;
        } else {
            $this->page = $this->getPage()->backendPage;
        }
        $this->api = $this->getApi();
    }

    public function getText() {
        $themeApp = $this->getFactory()->getApplicationPool()->getSelectedThemeApp();
        
        if (!$themeApp) {
            return null;
        }
        
        $this->textIndex++;
        $text = $themeApp->getText($this->page->pageTag,$this->textIndex , $this->page->pageTagGroup);
        return $text;
    }
    
    public function addContactForm($where) {
        $appConfig = $this->api->getPageManager()->addApplicationToRow($this->page->id, "96de3d91-41f2-4236-a469-cd1015b233fc", $where);
    }

    public function addImageDisplayer($imageId, $where, $type = false) {
        if(is_array($this->imageIds)) {
            $imageId = $this->imageIds[$this->imageCount];
            $this->imageCount++;
        } else {
            if (($this->rowSize == 1)) {
                    $imageId = $this->getRowImage();
            } else {
                switch($this->imageCount) {
                    case 0:
                    case 4:
                        $imageId = "cf3a0036-7dc3-4860-9bff-e240daa80960";
                        break;
                    case 1:
                        $imageId = "ad582f3f-a82b-46a5-9249-e45a9a1e57fb";
                        break;
                    case 5:
                    case 2:
                        $imageId = "c517e0ce-b8ef-4e97-ac62-cee1a2a193e3";
                        break;
                    case 3:
                        $imageId = "aa614a2e-8668-4a06-8cea-8113851e08b6";
                        break;
                    $this->rowImageCount++;
                }
                $this->imageCount++;
            }
            if ($type == "contact") {
                if (($this->rowSize == 1)) {
                    $imageId = $this->getRowImage();
                } else {
                    $imageId = "8d566b68-6b2c-4816-82e9-56189b9b1c9a";
                }
            }
        }
        
        
        $appConfig = $this->api->getPageManager()->addApplicationToRow($this->page->id, "831647b5-6a63-4c46-a3a3-1b4a7c36710a", $where);
        $app = new ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer();
        $app->setConfiguration($appConfig);
        $app->attachImageIdToApp($imageId);
    }

    public function addContentManager($content, $where, $type = false) {
        if ($type) {
            $content = $this->getText();
        }
        $appConfig = $this->api->getPageManager()->addApplicationToRow($this->page->id, "320ada5b-a53a-46d2-99b2-9b0b26a7105a", $where);
        if ($content) {
            $this->api->getContentManager()->saveContent($appConfig->id, $content);
        } else {
            $app = new ns_320ada5b_a53a_46d2_99b2_9b0b26a7105a\ContentManager();
            $app->setConfiguration($appConfig);
            $app->applicationAdded();
        }
        return $appConfig;
    }

    public function clearPage() {
        $this->getApi()->getPageManager()->clearPage($this->page->id);
    }

    public function setRowSize($size) {
        $this->rowSize = $size;
    }
    
    public function addMap($where) {
        $this->api->getPageManager()->addApplicationToRow($this->page->id, "17c48891-6f7a-47a0-849d-b50de9af218f", $where);
    }

    public function addProductData($where, $productid) {
        if (!$productid) {
            $themeApp = $this->getFactory()->getApplicationPool()->getSelectedThemeApp();

            if (!$themeApp) {
                return null;
            }
       
            $productid = $themeApp->getProductId($this->page->pageTag, $this->page->pageTagGroup, $this->productWidgetCount);
        }
        
        if ($productid) {
            $appconfig = $this->getApi()->getPageManager()->addApplicationToPage($this->page->id, "b741283d-920d-460b-8c08-fad5ef4294cb", $where);

            $app = new \ns_b741283d_920d_460b_8c08_fad5ef4294cb\ProductWidget();
            $app->setConfiguration($appconfig);
            $app->setConfigurationSetting("productid", $productid);
            $this->productWidgetCount++;
        }
    }
    
    public function addYouTube($movieid, $where, $type) {
        if (!$movieid) {
            switch ($this->counterYoutube) {
                case 0:
                    $movieid = "mbyzgeee2mg";
                    break;
                case 1:
                    $movieid = "CevxZvSJLk8";
                    break;
                case 2:
                    $movieid = "pco91kroVgQ";
                    break;
                case 3:
                    $movieid = "9bZkp7q19f0";
                    break;
                case 4:
                    $movieid = "7EXXFlygmQY";
                    break;
                case 5:
                    $movieid = "Y8dIIRCBuOI";
                    break;
                case 6:
                    $movieid = "e4QGnppJ-ys";
                    break;
            }
        }
        $this->counterYoutube++;
        $appconf = $this->api->getPageManager()->addApplicationToRow($this->page->id, "8e239f3d-2244-471e-a64d-3241b167b7d2", $where);
        $app = new ns_8e239f3d_2244_471e_a64d_3241b167b7d2\YouTube();
        $app->setConfiguration($appconf);
        $_POST['data']['id'] = $movieid;
        $app->setYoutubeId();
    }

    public function addProductList($area, $cell, $type, $viewtype) {
        $appconf = $this->api->getPageManager()->addApplicationToRow($this->page->id, "962ce2bb-1684-41e4-8896-54b5d24392bf", $area);
        $products = $this->getApi()->getProductManager()->getLatestProducts(4);
        if(!$products) {
            $products = array();
        }
        $newlist = array();
        foreach($products as $product) {
            $entry = new \core_listmanager_data_Entry();
            $entry->productId = $product->id;
            $entry->name = "Product";
            $newlist[] = $entry;
        }
        $this->getApi()->getListManager()->setEntries($appconf->id, $newlist);
        $app = new \ns_962ce2bb_1684_41e4_8896_54b5d24392bf\ProductLister();
        $app->setConfiguration($appconf);
        $_POST['data']['view'] = $viewtype;
        $app->setView();
        $_POST['data']['count'] = 2;
        if($viewtype == "boxview") {
            $app->updateColumnCount();
        }
    }

    public function addBannerSlider($area, $cell, $type) {
        $themeApp = $this->getFactory()->getApplicationPool()->getSelectedThemeApp();
        
        if (!$themeApp) {
            return null;
        }
        
        return $themeApp->addBannerSlider($this->page->pageTag, $this->textIndex , $this->page->pageTagGroup, $this->page->id,$area);
    }

    public function getRowImage() {
        $this->rowImageCount++;
        switch($this->rowImageCount) {
            case 0:
                return "4dfd5fb0-6c0f-4342-bbb2-a1a0ccd03dbd";
            case 1:
            default:
                return "87a62af8-372c-4e94-8d94-9a8ab61af278";
        }
    }

    public function addProduct() {
        $app = $this->api->getPageManager()->addApplicationToPage($this->page->id, "06f9d235-9dd3-4971-9b91-88231ae0436b", "product");
        return $app;
    }
    
    public function createProduct($layoutIndex, $title, $imageIds, $price) {
        $this->imageCount = 0;
        $this->setImageArray($imageIds);
        
        //Predefined pages.
        $predefined = new PredefinedPagesConfig();
        $layouts = $predefined->getProductPages();
        $this->page = $this->getApi()->getPageManager()->createPage(-1, "");
        $this->page->pageType = 2;
        
        //Page builder
        $pageBuilder = new PageBuilder(-1, "product", $this->page);
        $pageBuilder->setSiteBuilder($this);
        $this->page->layout = $pageBuilder->buildPredefinedPage($layouts[$layoutIndex]);
        $this->page->title = $title;
        $this->getApi()->getPageManager()->savePage($this->page);
        
        $pageBuilder->addPredefinedContent("product", $layouts[$layoutIndex]);
        
        
        /* @var $product core_productmanager_data_Product */
        $product = $this->getApi()->getProductManager()->getProductByPage($this->page->id);
        $product->name = $title;
        $product->price = $price;
        
        $this->getApi()->getProductManager()->saveProduct($product);
        
		$this->addProduct();
		
        return $this->page->id;
    }

    public function setImageArray($imageIds) {
        $this->imageIds = $imageIds;
    }
}



?>

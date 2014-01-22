<?php

class SiteBuilder extends ApplicationBase {
    /*  @var $api GetShopApi */

    var $api;
    var $counterYoutube = 0;
    private $rowSize;
    private $imageCount = 0;

    function __construct() {
        $this->api = $this->getApi();
    }

    public function addContactForm($where) {
        $appConfig = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "96de3d91-41f2-4236-a469-cd1015b233fc", $where);
    }

    public function addImageDisplayer($imageId, $where, $type = false) {
        if (($this->rowSize == 1)) {
            $imageId = "7cfb35c2-f43e-45fa-9c61-3f6d67b5c8f2";
        } else {
            switch($this->imageCount) {
                case 0:
                case 4:
                    $imageId = "5f6dbee1-adad-42a1-9333-82fd9ef1a13f";
                    break;
                case 1:
                    $imageId = "57f53e07-80db-4165-96aa-de15ad01a1f4";
                    break;
                case 5:
                case 2:
                    $imageId = "4eb3a16a-31d7-4284-b0f0-9c33e04a60cf";
                    break;
                case 3:
                    $imageId = "4c085721-bd13-4692-9438-18146b80174d";
                    break;
            }
            $this->imageCount++;
        }
        if ($type == "contact") {
            if (($this->rowSize == 1)) {
                $imageId = "7cfb35c2-f43e-45fa-9c61-3f6d67b5c8f2";
            } else {
                $imageId = "8d566b68-6b2c-4816-82e9-56189b9b1c9a";
            }
        }
        
        
        
        $appConfig = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "831647b5-6a63-4c46-a3a3-1b4a7c36710a", $where);
        $app = new ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer();
        $app->setConfiguration($appConfig);
        $app->attachImageIdToApp($imageId);
    }

    public function addContentManager($content, $where, $type = false) {
        if ($type) {
            $content = "<h1>" . $this->__f("Edit this text by clicking the gear while mouse is over this text") . "</h1><br><br>";
            $content .= "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        }
        $appConfig = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "320ada5b-a53a-46d2-99b2-9b0b26a7105a", $where);
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
        $this->getApi()->getPageManager()->clearPage($this->getPage()->id);
    }

    public function setRowSize($size) {
        $this->rowSize = $size;
    }
    
    public function addMap($where) {
        $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "17c48891-6f7a-47a0-849d-b50de9af218f", $where);
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
        $appconf = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "8e239f3d-2244-471e-a64d-3241b167b7d2", $where);
        $app = new ns_8e239f3d_2244_471e_a64d_3241b167b7d2\YouTube();
        $app->setConfiguration($appconf);
        $_POST['data']['id'] = $movieid;
        $app->setYoutubeId();
    }

    public function addProductList($area, $cell, $type, $viewtype) {
        $appconf = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "8402f800-1e7e-43b5-b3f7-6c7cabbf8942", $area);
        $products = $this->getApi()->getProductManager()->getLatestProducts(4);
        $newlist = array();
        foreach($products as $product) {
            $entry = new \core_listmanager_data_Entry();
            $entry->productId = $product->id;
            $entry->name = "Product";
            $newlist[] = $entry;
        }
        $this->getApi()->getListManager()->setEntries($appconf->id, $newlist);
        $app = new \ns_8402f800_1e7e_43b5_b3f7_6c7cabbf8942\ProductList();
        $app->setConfiguration($appconf);
        $app->setView($viewtype);
        
    }

}



?>

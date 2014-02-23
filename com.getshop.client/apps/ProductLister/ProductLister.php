<?php

namespace ns_962ce2bb_1684_41e4_8896_54b5d24392bf;

class ProductLister extends \WebshopApplication implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $products;

    function __construct() {
        
    }

    public function getDescription() {
        return "ProductLister";
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return "ProductLister";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function removeProduct() {
        $id = $_POST['data']['productid'];
        $list = $this->getApi()->getListManager()->getList($this->getConfiguration()->id);
        $newList = array();
        foreach($list as $index => $entry) {
            if($entry->productId != $id) {
                $newList[] = $entry;
            }
        }
        $this->getApi()->getListManager()->setEntries($this->getConfiguration()->id, $newList);
    }

    public function addProduct() {
        $list = $this->getApi()->getListManager()->getList($this->getConfiguration()->id);
        
        $entry = new \core_listmanager_data_Entry();
        $entry->productId = $_POST['data']['productid'];
        $entry->name = "Product";
        $list[] = $entry;
        
        $this->getApi()->getListManager()->setEntries($this->getConfiguration()->id, $list);
    }

    public function addFilter() {
        $id = $_POST['data']['value'];

        if (!isset($_SESSION['ProductList']['filter'][$this->getConfiguration()->id])) {
            $_SESSION['ProductList']['filter'][$this->getConfiguration()->id] = array();
        }

        if (isset($_SESSION['ProductList']['filter'][$this->getConfiguration()->id][$id])) {
            unset($_SESSION['ProductList']['filter'][$this->getConfiguration()->id][$id]);
        } else {
            $_SESSION['ProductList']['filter'][$this->getConfiguration()->id][$id] = true;
        }
    }

    public function setView() {
        if ($_POST['data']['view'] == "rowview") {
            $this->setConfigurationSetting("column", 1);
        }
        $this->setConfigurationSetting("view", $_POST['data']['view']);
    }

    public function getStarted() {
        $this->loadProductList();
    }

    public function getAttributeGroups() {
        if (!isset($this->attributeGroups)) {
            return array();
        }

        return $this->attributeGroups;
    }
    
    public function updateTitle() {
        $title = $_POST['data']['title'];
        $title = trim($title);
        $this->setConfigurationSetting('title', $title);
    }

    public function getTitle() {
        $title = $this->getConfigurationSetting('title');
        if(!$title) {
            $title = $this->__f("My products");
        }
        return $title;
    }

    public function getColumnCount() {
        $count = $this->getConfigurationSetting("column");
        if (!$count) {
            $count = 1;
        }
        
        return $count;
    }

    public function updateColumnCount() {
        $this->setConfigurationSetting("view", "listview");
        $this->setConfigurationSetting("column", $_POST['data']['count']);
    }

    public function getProducts() {
        if (!$this->products) {
            $this->loadProductList();
        }
        return $this->products;
    }

    public function getProductLink($product, $seoOptimzed = true) {
        if (!$seoOptimzed || !$product->name)
            return "?page=" . $product->page->id;

        return \GetShopHelper::makeSeoUrl($product->name);
    }

    public function getFilter() {
        if (isset($_SESSION['ProductList']['filter'][$this->getConfiguration()->id])) {
            return $_SESSION['ProductList']['filter'][$this->getConfiguration()->id];
        }
        return array();
    }
    
    public function setProductFromProductPicker() {
        $list = array();
        if(isset($_POST['data']['productids'])) {
            $list = $_POST['data']['productids'];
        }
        $newlist = array();
        foreach($list as $productid) {
            $entry = new \core_listmanager_data_Entry();
            $entry->productId = $productid;
            $entry->name = "Product";
            $newlist[] = $entry;
        }
        $this->getApi()->getListManager()->setEntries($this->getConfiguration()->id, $newlist);
        $this->loadProductList();
    }

    public function getView() {
        return $this->getConfigurationSetting("view");
    }

    public function addProductToCart() {
        $productId = $_POST['data']['productid'];

        $variations = array();
        if (isset($_POST['data']['variations'])) {
            $variations = $_POST['data']['variations'];
        }

        $this->getApi()->getCartManager()->addProduct($productId, 1, $variations);
    }

    public function loadProductList() {
        $criteria = $this->getApiObject()->core_productmanager_data_ProductCriteria();
        $criteria->listId = $this->getConfiguration()->id;
        $criteria->attributeFilter = $this->getFilter();
        $this->products = $this->getApi()->getProductManager()->getProducts($criteria);
        $this->attributeGroups = $this->getApi()->getProductManager()->getAttributeSummary()->attributeCount;
    }

    public function render() {
        $this->includefile("productlist");
    }

}

?>
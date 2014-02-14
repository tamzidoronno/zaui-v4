<?php

namespace ns_b741283d_920d_460b_8c08_fad5ef4294cb;

class ProductWidget extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $product;
    
    function __construct() {
        
    }

    public function getDescription() {
        return "ProductWidget";
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return "ProductWidget";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function getStarted() {
        
    }
    
    public function setProductFromProductPicker() {
        $productId = $_POST['data']['productids'][0];
        $this->setConfigurationSetting("productid", $productId);
    }
    
    public function getProductLink($product, $seoOptimzed=true) {
        if (!$seoOptimzed || ! $product->name)
            return "?page=".$product->page->id;
        
        return \GetShopHelper::makeSeoUrl($product->name);
    }
    
    
    public function getProduct() {
        return $this->product;
    }
    
    public function setProductId() {
        $value = $_POST['data']['productid'];
        $this->setConfigurationSetting("productid", $value);
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productid'];

        $variations = array();
        if (isset($_POST['data']['variations'])) {
            $variations = $_POST['data']['variations'];
        }

        $this->getApi()->getCartManager()->addProduct($productId, 1, $variations);
    }

    public function loadProduct() {
        if (@$_POST['data']['productid'] && $this->getConfigurationSetting("productid") != $_POST['data']['productid']) {
            $productId = $_POST['data']['productid'];
            $_POST['data']['productid'] = null;
        } else {
            $productId = $this->getConfigurationSetting("productid");
        }
        if(!$productId) {
            $this->product = null;
        } else {
            $this->product = $this->getApi()->getProductManager()->getProduct($productId);        
        }
    }

    public function render() {
        $this->loadProduct();
        $this->includefile("productview");
    }

}

?>

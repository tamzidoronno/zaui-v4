<?php

namespace ns_f245b8ae_f3ba_454e_beb4_ecff5ec328d6;

class ProductLists extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("Display product lists in your webshop");
    }

    public function getName() {
        return $this->__w("Product List");
    }
    
    private function showLists() {
        if (!$this->getConfigurationSetting("productlist")) {
            $this->includefile("showLists");
            return;
        } 
        
        $this->includefile("showProducts");
    }
    
    public function getAllProducts() {
        $listId = $this->getConfigurationSetting("productlist");
        $productList = $this->getApi()->getProductManager()->getProductList($listId);
        $criteria = new \core_productmanager_data_ProductCriteria();
        $criteria->ids = $productList->productIds;
        $products = $this->getApi()->getProductManager()->getProducts($criteria);
        return $products;
    }

    public function render() {
        $this->showLists();
    }
    
    public function renderConfig() {
        $this->includefile("overview");
    }

    public function createList() {
        $this->getApi()->getProductManager()->createProductList($_POST['name']);
    }
    
    public function setList() {
        $this->setConfigurationSetting("productlist", $_POST['data']);
    }
}
?>
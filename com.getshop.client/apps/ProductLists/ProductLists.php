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
    
    public function getProductList($id) {
        return $this->getApi()->getProductManager()->getProductList($id);
    }
    
    public function search() {
        
    }
    
    public function removeFromProductList() {
        $listId = $_POST['listId'];
        $productId = $_POST['productId'];
        
        $list = $this->getApi()->getProductManager()->getProductList($listId);
        $keep = array();
        foreach ($list->productIds as $id) {
            if ($id != $productId) {
                $keep[] = $id;
            }
        }
        
        $list->productIds = $keep;
        $this->getApi()->getProductManager()->saveProductList($list);    
    }

    public function addProductToList() {
        $listId = $_POST['listId'];
        $productId = $_POST['productId'];
        $list = $this->getApi()->getProductManager()->getProductList($listId);
        if (!in_array($productId, $list->productIds)) {
            $list->productIds[] = $productId;
            $this->getApi()->getProductManager()->saveProductList($list);
        }
    }
    
    public function getProductsFromList($productList) {
        $search = new \core_productmanager_data_ProductCriteria();
        $search->ids = $productList->productIds;
        $products = $this->getApi()->getProductManager()->getProducts($search);
        return $products;
    }
    
    public function searchForProducts() {
        if (!isset($_POST['searchword']) || $_POST['searchword'] == "") {
            return $this->getApi()->getProductManager()->getLatestProducts(10);
        }
        
        $search = new \core_productmanager_data_ProductCriteria();
        $search->search = $_POST['searchword'];
        $products = $this->getApi()->getProductManager()->getProducts($search);
        return $products;
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getCartManager()->addProduct($productId, 1, []);
    }
}
?>
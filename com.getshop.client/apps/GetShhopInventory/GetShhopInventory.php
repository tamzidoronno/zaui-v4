<?php
namespace ns_2a608d02_15d8_422b_9089_3082dc7e9123;

class GetShhopInventory extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShhopInventory";
    }

    public function getWareHouseId() {
        return $_SESSION['warehouseid'];
    }
    
    public function disableProduct() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        $product->hideFromStorageList = !$product->hideFromStorageList;
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function updateStockPrice() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        $product->stockValue = $_POST['data']['prompt'];
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function updateInventoryCount() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        $this->getApi()->getWareHouseManager()->adjustStockQuantity($product->id,  $_POST['data']['count'], $this->getWareHouseId(), $_POST['data']['comment']);
    }
    
    public function renameProduct() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        $product->name = $_POST['data']['prompt'];
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function render() {
        $this->includefile("allproducts");
    }
}
?>

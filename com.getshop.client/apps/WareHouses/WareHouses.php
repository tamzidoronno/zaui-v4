<?php
namespace ns_1c81b9d3_1d7c_47e3_a428_9183804f4549;

class WareHouses extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "WareHouses";
    }

    public function render() {
        if (!$this->getApi()->getOrderManager()->isStockManagementActive()) {
            echo $this->__f("This feature has not been activated, please contact GetShop if you need stock management to discuss the possibilities");
            return;
        }
        
        $this->includefile("warehouses");
    }
    
    public function createWareHouse() {
        $this->getApi()->getWareHouseManager()->createWareHouse($_POST['data']['name']);
    }
    
    public function setAsDefult() {
        $this->getApi()->getWareHouseManager()->setAsDefaultWareHosue($_POST['data']['warehouseid']);
    }
}
?>

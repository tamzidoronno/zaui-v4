<?php
namespace ns_339af689_1617_4d67_ade9_ca26cf55bf44;

class GetShopInvoicing extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopInvoicing";
    }

    public function render() {
        $this->includefile("monthselector");
        $this->includefile("monthview");
    }
    
    public function createRealOrders() {
        $this->getApi()->getDirectorManager()->createOrders();
    }
}
?>

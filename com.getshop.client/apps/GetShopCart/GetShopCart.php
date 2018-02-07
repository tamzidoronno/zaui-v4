<?php
namespace ns_478a4c37_5163_4aa8_be30_5d63f67a5cb9;

class GetShopCart extends \MarketingApplication implements \Application {
    private $order = null;
    
    public function getDescription() {
        
    }
    
    public function getOrder() {
        return $this->order;
    }

    public function getName() {
        return "GetShopCart";
    }

    public function render() {
        $this->setData();
        $this->includefile("overview");
    }
    

    public function setData() {
        if ($this->getModalVariable("orderUnderConstrcutionId")) {
            $_SESSION['OrderUnderConstruction_id'] = $this->getModalVariable("orderUnderConstrcutionId");
        }
        
        if (!$this->order) {
            $this->order = $this->getApi()->getOrderManager()->getOrder($_SESSION['OrderUnderConstruction_id']);
        }
    }

    public function clearOrderUnderConstruction() {
        $this->getApi()->getOrderManager()->deleteOrderUnderConstruction($_SESSION['OrderUnderConstruction_id']);
    }
    
}
?>

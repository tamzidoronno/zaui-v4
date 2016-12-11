<?php
namespace ns_f1c8301d_9900_420a_ad71_98adb44d7475;

class Vipps extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }

    public function getSavedCards($userId) {
    }
    
    public function getName() {
        return "Vipps";
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Vipps / Faktura", "");
    }
    
    public function render() {
        
    }
}
?>

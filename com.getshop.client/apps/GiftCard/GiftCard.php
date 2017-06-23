<?php
namespace ns_8650475d_ebc6_4dfb_86c3_eba4a8aba979;

class GiftCard extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }

    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod($this->getName(), "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/invoice.png", "Invoice");
    }
    
    
    public function getName() {
        return "GiftCard";
    }

    public function render() {
        
    }
}
?>

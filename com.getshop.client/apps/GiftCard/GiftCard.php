<?php
namespace ns_8650475d_ebc6_4dfb_86c3_eba4a8aba979;

class GiftCard extends \PaymentApplication implements \Application {
    public function getDescription() {
        return $this->__f("Used for collecting payments after you have sold a Gift Card");
    }

    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod($this->getName(), "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/invoice.png", "Invoice");
    }
    
    public function getIcon() {
        return "invoice.svg";
    }

    
    public function getName() {
        return "GiftCard";
    }

    public function render() {
        
    }
    
    public function isPublicPaymentApp() {
        return true;
    }
}
?>

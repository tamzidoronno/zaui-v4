<?php
namespace ns_e6cf029b_0b96_4cf8_9a88_8db2755c0062;

class NetsPaymentTerminal extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod($this->getName(), "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/invoice.png", "Invoice");
    }
    
    public function simplePayment() {
        echo "<script>";
        echo "thundashop.common.goToPageLink('?page=payment_success');";
        echo "</script>";
    }

    public function getName() {
        return "NetsPaymentTerminal";
    }

    public function render() {
        
    }
}
?>

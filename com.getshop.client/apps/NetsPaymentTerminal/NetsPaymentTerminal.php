<?php
namespace ns_e6cf029b_0b96_4cf8_9a88_8db2755c0062;

class NetsPaymentTerminal extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "If you have a payment terminal that is not connected to our booking system. This application can be used to keep the orders apart.";
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
        return "Payment terminal";
    }

    public function getIcon() {
        return "terminal.svg";
    }

    
    
    public function render() {
        
    }
    
    public function renderPaymentOption() {
        $this->includefile("paymentoptions");
    }
    
    public function canCreateOrderAfterStay() {
        return false;
    }
}
?>

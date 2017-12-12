<?php
namespace ns_4fc1033f_7380_4c0f_83d2_f3b4ad8bbb18;

class IZettlePayments extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Using izettle? Activate and use this payment method to keep track on izettle payments. This payment method does not have any integration to our booking system.";
    }

    public function getName() {
        return "IZettlePayments";
    }

    public function render() {
        
    }
}
?>

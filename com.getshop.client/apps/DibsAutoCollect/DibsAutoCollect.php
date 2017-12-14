<?php
namespace ns_688c382f_5995_456e_9385_9a724b9b9351;

class DibsAutoCollect extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Dibs autocollect is a special payment method to automatically charge cards saved at the OTA's. Be aware this payment method needs special clearance from the card provider."; 
    }

    public function getName() {
        return "Dibs autocollect";
    }

    public function render() {
        
    }
}
?>

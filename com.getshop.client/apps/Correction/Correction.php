<?php
namespace ns_eeab4306_3221_47ba_853b_2847057f3453;

class Correction extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "A payment method used in case a correction in billing details (name, contact details) is needed.";
    }

    public function getName() {
        return "Correction";
    }

    public function render() {
        
    }
}
?>

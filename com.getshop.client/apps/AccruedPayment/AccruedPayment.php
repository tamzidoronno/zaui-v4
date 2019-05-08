<?php
namespace ns_60f2f24e_ad41_4054_ba65_3a8a02ce0190;

class AccruedPayment extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Used for autopayments when zreport locks the financial data";
    }

    public function getName() {
        return "AccruedPayment";
    }

    public function render() {
        
    }
}
?>

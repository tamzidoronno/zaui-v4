<?php
namespace ns_6f51a352_a5ee_45ca_a8e2_e187ad1c02a5;

class PmsInvoicing extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsInvoicing";
    }

    public function render() {
        $this->includefile("invoicepayment");
    }
}
?>

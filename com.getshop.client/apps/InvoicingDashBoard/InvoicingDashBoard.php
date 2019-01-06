<?php
namespace ns_f9842d40_5f0a_4b48_86b2_84f314f5f025;

class InvoicingDashBoard extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "InvoicingDashBoard";
    }

    public function render() {
        $this->includefile("dashboard");
    }
}
?>

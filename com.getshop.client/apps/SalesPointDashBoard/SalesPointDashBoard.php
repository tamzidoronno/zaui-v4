<?php
namespace ns_bf312f0d_d204_45e9_9519_a139064ee2a7;

class SalesPointDashBoard extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointDashBoard";
    }

    public function render() {
        $this->includefile("dashboard");
    }
}
?>

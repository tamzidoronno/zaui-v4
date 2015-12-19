<?php
namespace ns_665c6c9d_5212_45e5_8c6b_aa0cf85dc6a2;

class SedoxCreditViewSmall extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxCreditViewSmall";
    }

    public function render() {
        $this->includefile("creditview");
    }
}
?>

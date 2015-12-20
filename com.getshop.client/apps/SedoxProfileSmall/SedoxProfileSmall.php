<?php
namespace ns_f64275fa_bae6_47c4_a988_0a0f99f0dfd6;

class SedoxProfileSmall extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxProfileSmall";
    }

    public function render() {
        $this->includefile("profilesmall");
    }
}
?>

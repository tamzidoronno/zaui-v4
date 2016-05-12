<?php
namespace ns_d93ea837_7de2_4a6b_84ba_b403d2f84a33;

class SedoxPerformanceEvcCredit extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxPerformanceEvcCredit";
    }

    public function render() {
        $this->includefile("evccreditlist");
    }
    
    public function refreshCredit() {
        $this->getApi()->getSedoxProductManager()->refreshEvcCredit();
    }
}
?>

<?php
namespace ns_a2d81cfe_eaec_4936_8c5a_167ed555d384;

class SedoxHistoryGraph extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxHistoryGraph";
    }

    public function render() {
        $this->includefile("chart");
    }
}
?>

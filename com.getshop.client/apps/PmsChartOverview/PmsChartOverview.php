<?php
namespace ns_f8d72daf_97d8_4be2_84dc_7bec90ad8462;

class PmsChartOverview extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsChartOverview";
    }

    public function render() {
        $this->includeFile("overview");
    }
}
?>

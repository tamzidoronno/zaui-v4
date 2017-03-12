<?php
namespace ns_b3f563f6_2408_4989_8499_beabaad36574;

class ScormList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ScormList";
    }

    public function render() {
        $this->includefile("scorms");
    }
}
?>

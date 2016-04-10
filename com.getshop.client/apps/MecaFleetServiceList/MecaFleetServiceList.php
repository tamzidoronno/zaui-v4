<?php
namespace ns_e4a506de_4702_4d82_8224_f30e5fdb1d2e;

class MecaFleetServiceList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetServiceList";
    }

    public function render() {
        $this->includefile("servicelist");
    }
}
?>

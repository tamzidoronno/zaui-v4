<?php
namespace ns_4efffb46_74a9_404f_8be0_2ac856bad401;

class MecaFleetGroupList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetGroupList";
    }

    public function render() {
        $this->includefile("fleetlist");
    }
}
?>

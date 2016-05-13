<?php
namespace ns_b48c3e14_676d_4c9e_acfc_60591c711c57;

class MecaFleetPKKList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetPKKList";
    }

    public function render() {
        $this->includefile("pkklist");
    }
    
    public function controlCompleted() {
        $this->getApi()->getMecaManager()->markControlAsCompleted($_POST['data']['carid']);
    }
    
    public function noShow() {
        $this->getApi()->getMecaManager()->noShowPkk($_POST['data']['carid']);
    }
}
?>

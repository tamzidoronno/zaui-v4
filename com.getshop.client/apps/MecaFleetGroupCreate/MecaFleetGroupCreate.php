<?php
namespace ns_cb4f201f_9ae5_4988_a277_95ec2850332d;

class MecaFleetGroupCreate extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetGroup";
    }

    public function render() {
        $this->includefile("createnewgroup");
    }
    
    public function createFleet() {
        $fleet = new \core_mecamanager_MecaFleet();
        $fleet->name = $_POST['data']['fleetName'];
        $this->getApi()->getMecaManager()->createFleet($fleet);
    }
}
?>

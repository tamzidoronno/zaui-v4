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
        if (isset($_POST['data']['fleetName']) && $_POST['data']['fleetName']) {
            $fleet = new \core_mecamanager_MecaFleet();
            $fleet->name = $_POST['data']['fleetName'];
            $fleet->followup = $_POST['data']['fleetFollowup'];
            $fleet->contactName = $_POST['data']['contactName'];
            $fleet->contactEmail = $_POST['data']['contactEmail'];
            $fleet->contactPhone = $_POST['data']['contactPhone'];
            if(isset($fleet->contactDetails)){
               $fleet->contactDetails = $_POST['data']['contactDetails']; 
            }
            $this->getApi()->getMecaManager()->createFleet($fleet);
        }
    }
}
?>

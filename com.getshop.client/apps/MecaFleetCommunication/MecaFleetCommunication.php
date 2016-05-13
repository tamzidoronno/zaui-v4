<?php
namespace ns_99e7671e_1f11_4902_8459_faa3aaeeb885;

class MecaFleetCommunication extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetCommunication";
    }

    public function render() {
        $this->includefile("status");
    }
    
    public function sendInvitation() {
        $this->getApi()->getMecaManager()->sendInvite($_POST['data']['carid']); 
    }
}
?>

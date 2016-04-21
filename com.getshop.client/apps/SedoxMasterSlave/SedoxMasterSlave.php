<?php
namespace ns_2e9edff6_b2f1_4aba_9b55_9813e58b1214;

class SedoxMasterSlave extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxMasterSlave";
    }

    public function render() {
        $this->includefile("masterslave");
    }
    
    public function listUserHierarchy() {
        echo json_encode($this->getApi()->getSedoxProductManager()->getAllUsersAsTreeNodes());
    }
    
    public function getUserFromTree() {
        $this->includefile("userinformation");
    }
    
    public function saveInformation() {
        $this->getApi()->getSedoxProductManager()->togglePassiveSlaveMode($_POST['data']['slaveid'], filter_var($_POST['data']['passiveslave'], FILTER_VALIDATE_BOOLEAN));
        $this->getApi()->getSedoxProductManager()->addCreditToSlave($_POST['data']['slaveid'], filter_var($_POST['data']['income'], FILTER_VALIDATE_FLOAT));
        if($_POST['data']['masterid']) {
            $this->getApi()->getSedoxProductManager()->addSlaveToUser($_POST['data']['masterid'], $_POST['data']['slaveid']);
        }
    }
}
?>

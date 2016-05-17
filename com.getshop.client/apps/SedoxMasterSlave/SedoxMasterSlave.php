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
        $this->getApi()->getSedoxProductManager()->addCommentToUser($_POST['data']['userid'], $_POST['data']['comment']);
        
        foreach($_POST['data']['slavedatas'] as $slaveData) {
            $this->getApi()->getSedoxProductManager()->addCreditToSlave($slaveData['slaveid'], filter_var($slaveData['income'], FILTER_VALIDATE_FLOAT));
            $this->getApi()->getSedoxProductManager()->togglePassiveSlaveMode($slaveData['slaveid'], filter_var($slaveData['passiveslave'], FILTER_VALIDATE_BOOLEAN));  
        }
    }
    
    public function removeSlaveFromMaster() {
        $this->getApi()->getSedoxProductManager()->removeSlaveFromMaster($_POST['data']['slaveid']);
    }
    
    public function addSlaveToMaster() {
        $this->getApi()->getSedoxProductManager()->addSlaveToUser($_POST['data']['masterid'], $_POST['data']['slaveid']);
        
        $slaveUser = $this->getApi()->getUserManager()->getUserById($_POST['data']['slaveid']);
        $slave = $this->getApi()->getSedoxProductManager()->getSedoxUserAccountById($_POST['data']['slaveid']);
        
        echo "<div class='slave_information'>";
        echo "<div class='slave_name float-left' master_id='" . $_POST['data']['masterid'] . "' slave_id='" . $slaveUser->id . "'>" . $slaveUser->fullName . "</div>";
        echo "<div class='float-right'>";
        echo "<div class='slave_options checkbox' slave_id='" .  $slaveUser->id . "'>";
        echo "<input class='income_input' value='" .  $slave->slaveIncome . "'>";
        echo " Passive: "; 
        echo "<input class='passsive_slave_input' id='passiveslavebox_" . $slaveUser->id . "' type='checkbox' " . ($slave->isPassiveSlave ? "checked" : "") . ">";
        echo "<label for='passiveslavebox_" . $slaveUser->id ."'></label>";
        echo "</div>";
        echo " <div class='delete_slave'><i class='fa fa-times' slave_id='" . $slaveUser->id . "'></i></div>";
        echo "</div>";
        echo "</div>";
    }
}
?>

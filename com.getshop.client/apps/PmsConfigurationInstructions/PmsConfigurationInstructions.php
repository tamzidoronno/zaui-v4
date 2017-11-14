<?php
namespace ns_78b6ce1d_95df_4972_b530_e08a89e09c46;

class PmsConfigurationInstructions extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsConfigurationInstructions";
    }

    public function render() {
        $this->includefile("instructions");
    }
    
    public function saveContent() {
        $from = $_POST['data']['fromid'];
        
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        if($from == "otherinstructionsfiled") {
            $notificationSettings->otherinstructions = $_POST['data']['content'];
        } else {
            $notificationSettings->fireinstructions = $_POST['data']['content'];
        }
        
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notificationSettings);
        
        
    }
    
}
?>

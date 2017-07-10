<?php
namespace ns_e8cc6f68_d194_4820_adab_6052377b678d;

class SendRegning extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SendRegning";
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("sendregningconfig");
    }    
    
    public function render() {
        
    }

    public function saveSettings() {
        $this->setConfigurationSetting("email", $_POST['email']);
        $this->setConfigurationSetting("password", $_POST['password']);
        $this->setConfigurationSetting("originatorid", $_POST['originatorid']);
    }    
}
?>

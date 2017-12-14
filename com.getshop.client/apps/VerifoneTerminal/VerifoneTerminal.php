<?php
namespace ns_6dfcf735_238f_44e1_9086_b2d9bb4fdff2;

class VerifoneTerminal extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "We have a terminal connected directly to our booking system. Whenever paying with a verifone payment terminal you will automatically mark payments and completed. This integration is complex and needs a getshop expert to complete.";
    }

    public function getName() {
        return "Verifone terminal";
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("terminalconfig");
    }
    
    public function render() {
        
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("ipaddr0", $_POST['ipaddr0']);
        $this->setConfigurationSetting("ipaddr1", $_POST['ipaddr1']);
        $this->setConfigurationSetting("ipaddr2", $_POST['ipaddr2']);
        $this->setConfigurationSetting("ipaddr3", $_POST['ipaddr3']);
        $this->setConfigurationSetting("ipaddr4", $_POST['ipaddr4']);
    }
}
?>

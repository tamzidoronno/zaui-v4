<?php
namespace ns_f86e7042_f511_4b9b_bf0d_5545525f42de;

class PayOnRoom extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PayOnRoom";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("bookingengine", $_POST['bookingengine']);
    }
}
?>

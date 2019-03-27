<?php
namespace ns_1d02cbe8_6894_4438_af26_e7e5eccefbae;

class PaymentMethod3 extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "A special payment method to recieve payments manually from different sources. Activate this payment method and rename it to something that fits your need.";
    }

    public function getName($getOriginal = false) {
        $name = $this->getConfigurationSetting("name");
        if(!$name || $getOriginal) {
            $name = "PaymentMethod3";
        }
        return $name;
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includeFile("config");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("name", $_POST['name']);
    }     
    
    public function getColor() {
        return "red";
    }
}
?>

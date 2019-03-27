<?php
namespace ns_a263b749_abcd_4812_b052_e20eccb69aa5;

class PaymentMethod2 extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "A special payment method to recieve payments manually from different sources. Activate this payment method and rename it to something that fits your need.";
    }

    public function getName($getOriginal = false) {
        $name = $this->getConfigurationSetting("name");
        if(!$name || $getOriginal) {
            $name = "PaymentMethod2";
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

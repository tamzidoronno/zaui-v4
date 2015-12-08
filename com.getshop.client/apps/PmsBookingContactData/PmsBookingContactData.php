<?php
namespace ns_d3951fc4_6929_4230_a275_f2a7314f97c1;

class PmsBookingContactData extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingContactData";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        $this->includefile("roomcontactdata");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("booking_engine_name");
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
}
?>

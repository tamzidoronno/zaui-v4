<?php
namespace ns_9de81608_5cec_462d_898c_1266d1749320;

class PmsNotifications extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsNotifications";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        
        $this->includefile("notificationpanel");
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    public function saveNotifications() {
        echo "haha";
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
}
?>

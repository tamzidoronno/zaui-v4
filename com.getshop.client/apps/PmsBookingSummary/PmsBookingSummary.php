<?php
namespace ns_46b52a59_de5d_4878_aef6_13b71af2fc75;

class PmsBookingSummary extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Displays a view with a summary for the current booking that is processed";
    }

    public function getName() {
        return "PmsBookingSummary";
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        $this->includefile("summary");
    }

    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    public function addAddon() {
        $itemType = $_POST['data']['itemtypeid'];
        
    }
}
?>

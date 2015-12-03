<?php
namespace ns_8dcbf529_72ae_47dd_bd6b_bd2d0c54b30a;

class PmsBooking extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBooking";
    }

    public function render() {
        $this->includefile("pmsfront_1");
    }
    
    public function getText($key) {
        return $this->getConfigurationSetting($key);
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
}
?>

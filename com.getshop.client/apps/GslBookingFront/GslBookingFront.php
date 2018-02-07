<?php
namespace ns_4fb8b077_9370_48e4_827d_c323267040b4;

class GslBookingFront extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GslBookingFront";
    }

    public function render() {
        $this->includefile('gslbookingfront_1');
        echo "<script>";
        echo "getshop_urltonextpage = " . $this->getConfigurationSetting("next_page");
        echo "</script>";
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

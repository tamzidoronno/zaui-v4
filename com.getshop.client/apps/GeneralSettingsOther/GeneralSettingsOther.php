<?php
namespace ns_afbe1ef5_6c62_45c7_a5a0_fd16d380d7cb;

class GeneralSettingsOther extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GeneralSettingsOther";
    }

    public function render() {
        $this->includefile("settings");
    }
    
    public function save() {
        $this->getApi()->getStoreManager()->changeTimeZone($_POST['data']['timezone']);
    }
    
}
?>

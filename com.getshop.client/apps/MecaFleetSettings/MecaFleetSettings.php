<?php
namespace ns_6a7c8d8e_abd4_43b3_ad88_c568f245d4da;

class MecaFleetSettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetSettings";
    }

    public function render() {
        $this->includefile("settings");
    }
    
    public function save() {
        $this->setConfigurationSetting("openinghours", $_POST['data']['openinghours']);
        $this->setConfigurationSetting("contact_name", $_POST['data']['contact_name']);
        $this->setConfigurationSetting("contact_email", $_POST['data']['contact_email']);
        $this->setConfigurationSetting("contact_cell", $_POST['data']['contact_cell']);
        $this->setConfigurationSetting("roadmap", $_POST['data']['roadmap']);
    }
}
?>

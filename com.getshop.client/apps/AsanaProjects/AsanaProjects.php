<?php
namespace ns_b78a42d9_2713_4c89_bb9e_48c3dd363a40;

class AsanaProjects extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AsanaProjects";
    }

    public function render() {
        $this->includefile("projects");
    }
    
    public function renderConfig() {
        $this->includefile("settings");
    }
    
    public function saveGlobalSettings() {
        $this->setConfigurationSetting("token", $_POST['token'], true);
    }
}
?>

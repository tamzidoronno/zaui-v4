<?php
namespace ns_48a459b8_90b2_4dea_9bae_f548d006f526;

class CertegoSystemDepartments extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CertegoSystemDepartments";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("settings");
    }
    
    public function getDefaultEmailAddress() {
        return $this->getConfigurationSetting("defaultemail");
    }
    
    public function getDepartments() {
        $departments = $this->getConfigurationSetting("departments");
        if ($departments) {
            return json_decode($departments);
        }
        
        return [];
    }
    
    public function addDepartment() {
        $departments = $this->getDepartments();
        $departments[] = uniqid();
        $this->setConfigurationSetting("departments", json_encode($departments));
    }

    public function getEmailAddress($dep) {
        return $this->getConfigurationSetting("mail_".$dep);
    }

    public function getDepartmentName($dep) {
        return $this->getConfigurationSetting("name_".$dep);
    }
    
    public function saveDepartments() {
        $this->setConfigurationSetting("defaultemail", $_POST['defaultemail']);
        foreach ($this->getDepartments() as $dep) {
            $this->setConfigurationSetting("name_".$dep, $_POST["name_$dep"]);
            $this->setConfigurationSetting("mail_".$dep, $_POST["mail_$dep"]);
        }
    }
    
    public function renderGroupInformation($group) {
        $this->currentGroup = $group;
        $this->includefile("groupconnection");
    }
    
    public function getCurrentSelectedGroup() {
        $key = $this->currentGroup->id;
        return $this->getConfigurationSetting("selected_$key");
    }

    public function saveGroupConnection() {
        $this->setConfigurationSetting("selected_".$_POST['value'], $_POST['current_selected_department']);
    }
}
?>

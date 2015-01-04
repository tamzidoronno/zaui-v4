<?php
namespace ns_8ad8243c_b9c1_48d4_96d5_7382fa2e24cd;

class Mail extends \MarketingApplication implements \Application {
    public $singleton = true;

    public function getDescription() {
        return $this->__f("Configure this application if you want to send email from a different email provider en the default one for GetShop");
    }
    
    public function getName() {
        return $this->__("Mail");
    }
    
    public function postProcess() {
        
    }
    
    public function preProcess() {
        
    }
    
    public function render() {
        
    }
    
    
    public function renderConfig() {
        $this->includefile("config");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("hostname", $_POST['hostname']);
        $this->setConfigurationSetting("port", $_POST['port']);
        $this->setConfigurationSetting("password", $_POST['password'], true);
        $this->setConfigurationSetting("username", $_POST['username']);
        $this->setConfigurationSetting("enabletls", $_POST['enabletls']);
    }
}
?>

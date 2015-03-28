<?php
namespace ns_12fecb30_4e5c_49d8_aa3b_73f37f0712ee;

class Clickatell extends \MarketingApplication implements \Application {
    public $singleton = true;

    public function getDescription() {
        return $this->__f("If you send sms with Clickatell as provider, you can use this to configure username an passwords");
    }
    
    public function getName() {
        return "Clickatell";
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
        $this->setConfigurationSetting("username", $_POST['username']);
        $this->setConfigurationSetting("apiid", $_POST['apiid']);
        $this->setConfigurationSetting("numberprefix", $_POST['numberprefix']);
        $this->setConfigurationSetting("password", $_POST['password']);
        $this->setConfigurationSetting("from", $_POST['from']);
    }
}
?>

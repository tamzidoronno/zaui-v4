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
    
    public function testSendEmail() {
        $to = $_POST['data']['email'];
        $this->getApi()->getMessageManager()->sendMail($to, $to, "Test email", "This is a test email.", $to, $to);
    }
    
    public function preProcess() {
        
    }
    
    public function render() {
        $this->includefile("mailsettings");
    }
    
    
    public function renderConfig() {
        $this->includefile("config");
    }
    
    public function saveMailSettings() {
        $this->saveKeySettings("hostname", $_POST['data']['hostname']);
        $this->saveKeySettings("port", $_POST['data']['port']);
        
        //dont overwrite password with all ******
        if( !( strpos($_POST['data']['password'],'*') !== false && str_replace('*','',$_POST['data']['password']) == '' ) ) $this->saveKeySettings("password", $_POST['data']['password'], true);
        
        $this->saveKeySettings("username", $_POST['data']['username']);
        $this->saveKeySettings("enabletls", $_POST['data']['enabletls']);
        $this->saveKeySettings("sendMailFrom", $_POST['data']['sendMailFrom']);
        $this->saveKeySettings("sendMailFromName", $_POST['data']['sendMailFromName']);        
        $this->saveKeySettings("replyTo", $_POST['data']['replyTo']);        
    }
    
    public function saveKeySettings($key, $value, $secure = false) {
        
        $setting = new \core_common_Setting();
        $setting->id = $key;
        $setting->value = $value;
        $setting->name = $key;
        $setting->secure = $secure;
        
        $this->getApi()->getStoreApplicationPool()->setSetting("8ad8243c-b9c1-48d4-96d5-7382fa2e24cd", $setting);
    }
    
    public function saveSettings() {
        
        $this->setConfigurationSetting("hostname", $_POST['hostname']);
        $this->setConfigurationSetting("port", $_POST['port']);
        $this->setConfigurationSetting("password", $_POST['password'], true);
        $this->setConfigurationSetting("username", $_POST['username']);
        $this->setConfigurationSetting("enabletls", $_POST['enabletls']);
        $this->setConfigurationSetting("sendMailFrom", $_POST['sendMailFrom']);
    }
}
?>

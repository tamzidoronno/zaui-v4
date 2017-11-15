<?php
namespace ns_624fa4ac_9b27_4166_9fc3_5c1d1831b56b;

class PmsSendMessagesConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsSendMessagesConfiguration";
    }

    public function render() {
        $this->includefile("addmessage");
    }
    
    public function changeLanguage() {
        $_SESSION['PmsSendMessagesConfigurationLang'] = $_POST['data']['lang'];
    }
    
    function endsWith($haystack, $needle) {
        $length = strlen($needle);
        return $length === 0 || (substr($haystack, -$length) === $needle);
    }
    
    public function addMessage() {
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $notificationSettings->{$_POST['data']['submit']}->{$_POST['data']['type']} = $_POST['data']['message'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notificationSettings);
    }
    
    public function updateMessage() {
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        if(isset($_POST['data']['submit']) && $_POST['data']['submit'] == "remove") {
            unset($notificationSettings->{$_POST['data']['type']}->{$_POST['data']['key']});
        } else {
            $notificationSettings->{$_POST['data']['type']}->{$_POST['data']['key']} = $_POST['data']['message'];
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notificationSettings);
    }

    public function getLanguage() {
        if(isset($_SESSION['PmsSendMessagesConfigurationLang'])) {
            return $_SESSION['PmsSendMessagesConfigurationLang'];
        }
        return $this->getFactory()->getMainLanguage();
    }

}
?>

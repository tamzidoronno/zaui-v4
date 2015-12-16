<?php
namespace ns_9de81608_5cec_462d_898c_1266d1749320;

class PmsNotifications extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsNotifications";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        
        $this->includefile("notificationpanel");
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    public function saveNotifications() {
        $notifications = $this->getApi()->getPmsManager()->getNotifications($this->getSelectedName());
        foreach($_POST['data'] as $key => $value) {
            if($this->endsWith($key, "_email")) {
                $key = substr($key, 0, strlen($key)-6);
                $notifications->emails->{$key} = $value;
            }
        }
        foreach($_POST['data'] as $key => $value) {
            if($this->endsWith($key, "_sms")) {
                $key = substr($key, 0, strlen($key)-4);
                $notifications->smses->{$key} = $value;
            }
        }
        foreach($_POST['data'] as $key => $value) {
            if($this->endsWith($key, "_admin")) {
                $key = substr($key, 0, strlen($key)-6);
                $notifications->adminmessages->{$key} = $value;
            }
        }
        $notifications->emailTemplate = $_POST['data']['email_template'];
        
        $this->getApi()->getPmsManager()->saveNotification($this->getSelectedName(), $notifications);
    }
    
    function endsWith($haystack, $needle) {
    // search forward starting from end minus needle length characters
    return $needle === "" || (($temp = strlen($haystack) - strlen($needle)) >= 0 && strpos($haystack, $needle, $temp) !== FALSE);
}
    
    public function showSettings() {
        $this->includefile("settings");
    }
}
?>

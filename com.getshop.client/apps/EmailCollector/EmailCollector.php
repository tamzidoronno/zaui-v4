<?php
namespace ns_34981104_5a56_436f_b897_f9d7a9b80427;

class EmailCollector extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function addEmail() {
        $email = $_POST['data']['email'];
        $this->getApi()->getMessageManager()->collectEmail($email);
    }

    public function getName() {
        return "EmailCollector";
    }

    public function render() {
        $this->includefile("collectorform");
        if($this->isEditorMode()) {
            $this->printEmailList();
        }
    }

    public function printEmailList() {
        $list = $this->getApi()->getMessageManager()->getCollectedEmails();
        foreach($list as $email) {
            echo $email . "<br>";
        }
    }

    public function getEmailList() {
        $list = $this->getConfigurationSetting("emaillist");
        if($list) {
            $list = unserialize($list);
        } else {
            $list = array();
        }
        return $list;
    }

}
?>

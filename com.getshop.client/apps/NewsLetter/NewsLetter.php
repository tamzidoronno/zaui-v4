<?php
namespace ns_adbdc94d_a201_4586_aa07_1e4d46ca0fd6;

class NewsLetter extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return $this->__f("The newsletter application allowes you to send emails to all of your customers without. Create a news letter, select your customers and send.");
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "NewsLetter";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }
    
    public function saveNewsLetter() {
        $mail = $_POST['data']['mail'];
        $this->setConfigurationSetting("currentMail", $mail);
    }
    
    public function sendNewsLetter() {
        $users = $_POST['data']['users'];
        $mail = $_POST['data']['mail'];
        $subject = $_POST['data']['subject'];
        
        $allUsers = $this->getApi()->getUserManager()->getAllUsers();
        
        
        $group = new \core_messagemanager_NewsLetterGroup();
        $group->emailBody = $mail;
        $group->title = $subject;
        $group->userIds = $users;
        
        if($_POST['data']['id'] == "send_preview") {
            $group->userIds = array();
            $group->userIds[] = $this->getUser()->id;
            $this->getApi()->getNewsLetterManager()->sendNewsLetterPreview($group);
        } else {
            $this->getApi()->getNewsLetterManager()->sendNewsLetter($group);
        }
    }

    public function getMail() {
        $content = $this->__f("Build your newsletter");
        if($this->getConfigurationSetting("currentMail")) {
            $content = $this->getConfigurationSetting("currentMail");
        }
        return $content;
    }
    
    public function render() {
        if($this->hasWriteAccess()) {
            if(isset($_POST['event']) && $_POST['event'] == "sendNewsLetter") {
                echo "<div style='text-align:center;'>";
                echo "<h1>".$this->__f("The newsletter is now being sent.") . "</h1>";
                echo "</div>";
            } else {
                $this->includefile("NewsLetter");
            }
        }
    }
}
?>

<?php

namespace ns_d2e1d2b4_b604_46a4_a298_0b8161e00715;

class MailSpreader extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return "MailSpreader";
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function saveEmail() {
        $text = $_POST['data']['email'];
        $emailtitle = $_POST['data']['emailtitle'];
        $this->setConfigurationSetting("email", $text);
        $this->setConfigurationSetting("emailtitle", $emailtitle);
    }

    public function getName() {
        return "MailSpreader";
    }

    public function postProcess() {
        
    }

    public function getEmailText() {
        $email = $this->getConfigurationSetting("email");
        if (!$email) {
            $email = "Hi {name}, you just got tipped.";
        }
        return $email;
    }

    public function preProcess() {
        
    }

    public function getStarted() {
        
    }

    public function getEmailTitle() {
        $email = $this->getConfigurationSetting("emailtitle");
        if (!$email) {
            $email = "Just want to let you know";
        }
        return $email;
    }

    public function getSavedEntries() {
        $entries = $this->getConfigurationSetting("sentemails");
        if($entries) {
            return json_decode($entries, true);
        }
        return array();
    }
    
    public function sendEmails() {
        $name = $_POST['data']['name'];
        $entries = $_POST['data']['entries'];

        $savedEntries = $this->getSavedEntries();
        $fromEmail = $this->getFactory()->getSettings()->mainemailaddress->value;

        foreach ($entries as $entry) {
            $toemail = $entry['email'];
            $toname = $entry['name'];
            $subject = $this->getEmailTitle();
            $content = $this->getEmailText();
            
            $content = str_replace("{name}", $toname, $content);
            $subject = str_replace("{name}", $toname, $subject);
            
            $content = str_replace("{tipser}", $name, $content);
            $subject = str_replace("{tipser}", $name, $subject);
            
            if (!isset($savedEntries[$name][$toemail]) && $toname && $toemail) {
                $this->getApi()->getMessageManager()->sendMail($toemail, $name, $subject, $content, "Notification", $fromEmail);
                $savedEntries[$name][$toemail] = $toname;
            }
        }

		$this->startAdminImpersonation("PageManager", "setApplicationSettings");
        $this->setConfigurationSetting("sentemails", json_encode($savedEntries));
		$this->stopImpersionation();
    }

    public function render() {
        if(isset($_POST['data']['name'])) {
            echo $this->__w("Thank you for participating in our campaign.");
            return;
        }
        
        $this->includefile("MailSpreader");
        if ($this->hasWriteAccess()) {
            $this->includefile("MailSpreaderAdmin");
        }
        echo "<br><br><br><br><br><br>";
    }

	public function requestAdminRights() {
        $this->requestAdminRight("PageManager", "setApplicationSettings", $this->__o("Need admin rights to save sent mails."));
    }
}

?>

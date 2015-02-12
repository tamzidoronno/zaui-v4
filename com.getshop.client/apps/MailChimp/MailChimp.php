<?php

namespace ns_d73698c6_7fda_4154_97c5_b3b6d0927abe;
class MailChimp extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return $this->__f("MailChimp is an easy to use news letter sender. Allows you to collect emails and send bulk emails to them.");
    }

    public function getName() {
        return "MailChimp";
    }

    public function render() {
        $this->includefile("frontend");
    }
    
    public function addEmail() {
        $email = $_POST['data']['email'];
        echo $email;
        $this->getApi()->getMessageManager()->collectEmail($email);
    }

}
?>
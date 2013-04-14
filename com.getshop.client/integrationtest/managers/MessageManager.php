<?php
class MessageManager extends TestBase {
    public function MessageManager($api) {
        $this->api = $api;
    }
    
    /**
     * Need to send an email, maybe a notification of some sort?
     */
    public function test_sendMail() {
        $manager = $this->getApi()->getMessageManager();
        
        $to = "test@getshop.com";
        $toName = "Getshop support";
        $subject = "just a test";
        $content = "The body of the email";
        $from = "test@getshop.com";
        $fromName = "My name";
        
        $manager->sendMail($to, $toName, $subject, $content, $from, $fromName);
    }
    
    /**
     * To check how many sms has been sent trough the system 
     */
    public function test_getSmsCount() {
        $manager = $this->getApi()->getMessageManager();
        $year = 2012;
        $month = 10;
        $count = $manager->getSmsCount($year, $month);
    }
}

?>

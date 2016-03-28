<?php
namespace ns_20d70922_63fb_4cd8_bcc6_cfd1c1a1a4fd;

class EventSendTestToCandidates extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventSendTestToCandidates";
    }

    public function render() {
        $this->includefile("sendTests");
    }
    
    public function sendTest() {
        $eventId = $this->getModalVariable("eventid");
        $testid = $_POST['data']['testid'];
        $users = $this->getApi()->getEventBookingManager()->getUsersForEvent($this->getBookingEngineName(), $eventId);
        
        foreach ($users as $user) {
            if (isset($_POST['data'][$user->id]) && $_POST['data'][$user->id] == "true") {
                $this->getApi()->getQuestBackManager()->assignUserToTest($testid, $user->id);
            } 
        }
    }
}
?>

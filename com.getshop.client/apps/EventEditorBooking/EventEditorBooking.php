<?php
namespace ns_93b0a895_0713_4c59_acf3_c4714ad1f200;

class EventEditorBooking extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }
    
    public function getName() {
        return "EventEditorBooking";
    }

    public function render() {
        $this->includefile("addmoreusers");
    }
    
    public function searchForUsers() {
        $this->includefile("searchresult");
    }
    
    public function addUserToEvent() {
        $this->getApi()->getEventBookingManager()->addUserToEvent($this->getBookingEngineName(), $_POST['data']['eventid'], $_POST['data']['userid']);
    }
}
?>

<?php
namespace ns_83df5ae3_ee55_47cf_b289_f88ca201be6e;

class EventList extends EngineCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventList";
    }

 
    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("eventlist");
        }    
    }
    
    public function deleteEvent() {
        $this->getApi()->getEventBookingManager()->deleteEvent($this->getBookingEgineName(), $_POST['data']['eventid']);
    }
}
?>

<?php
namespace ns_0b9a8784_5399_415f_9d79_b1c271f3bed2;

class SimpleEventList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SimpleEventList";
    }

    public function render() {
        if(isset($_POST['data']['showedit']) && $_POST['data']['showedit'] == "true") {
            $this->includefile("doedit");
        } else {
            if(isset($_POST['event']) && $_POST['event'] == "saveEvent") {
                $this->closeModal();
            } 
            
            $this->includefile("header");
            if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isEditor() || \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
                $this->includefile("addevent");
            }
            $this->includefile("eventlist");
        }
    }
    
    public function addEvent() {
        $event = new \core_simpleeventmanager_SimpleEvent();
        $event->name = $_POST['data']['name'];
        $event->date = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $event->location = $_POST['data']['location'];
        $event->originalPageId = $this->getPage()->getId();
        
        $this->getApi()->getSimpleEventManager()->saveEvent($event);
    }
    
    public function saveEvent() {
        $event = $this->getApi()->getSimpleEventManager()->getEventById($_POST['data']['eventid']);
        $event->name = $_POST['data']['name'];
        $event->date = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $event->location = $_POST['data']['location'];
        $event->originalPageId = $this->getPage()->getId();
        
        $this->getApi()->getSimpleEventManager()->saveEvent($event);
    }
    
    public function toggleSignupRequired() {
        $events = $this->getApi()->getSimpleEventManager()->getAllEvents($this->getPage()->getId());
        foreach ($events as $event) {
            if ($event->id == $_POST['data']['eventid']) {
                
                if ($event->requireSignup)
                    $event->requireSignup = "false";
                else 
                    $event->requireSignup = "true";
                
                $this->getApi()->getSimpleEventManager()->saveEvent($event);
            }
        }
        
    }
    
    public function deleteEvent() {
        $this->getApi()->getSimpleEventManager()->deleteEvent($_POST['data']['eventid']);
    }
}
?>

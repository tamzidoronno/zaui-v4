<?php
namespace ns_0b9a8784_5399_415f_9d79_b1c271f3bed2;

class SimpleEventList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SimpleEventList";
    }

    public function render() {
        $this->includefile("header");
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isEditor() || \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->includefile("addevent");
        }
        $this->includefile("eventlist");
    }
    
    public function addEvent() {
        $event = new \core_simpleeventmanager_SimpleEvent();
        $event->name = $_POST['data']['name'];
        $event->date = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $event->location = $_POST['data']['location'];
        $this->getApi()->getSimpleEventManager()->saveEvent($event);
    }
    
    public function deleteEvent() {
        $this->getApi()->getSimpleEventManager()->deleteEvent($_POST['data']['eventid']);
    }
}
?>

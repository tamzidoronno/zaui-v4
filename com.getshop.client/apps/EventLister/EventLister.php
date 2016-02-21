<?php
namespace ns_f06d0c6a_882e_4592_917c_b74273edfd11;

class EventLister extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventLister";
    }

    public function render() {
        $this->includefile("eventslist");
    }
    
    public function getEvents() {
        $events = $this->getApi()->getEventBookingManager()->getEvents($this->getBookingEngineName());
        if (!count($events)) {
            return [];
        }
        
        $retArray = [];
        
        foreach ($events as $event) {
            $startDate = $event->days[0]->startDate;
            $date = getdate(strtotime($startDate));
            $key = $date["month"]."_".$date["year"];
            if (!isset($retArray[$key])) {
                $retArray[$key] = [];
            }
            $retArray[$key][] = $event;
        }
        
        return $retArray;
    }

    public function getLocations($groupedEvents) {
        
        
        return $this->getApi()->getEventBookingManager()->getAllLocations($this->getBookingEngineName());
    }
    
    public function applyFilter() {
        $this->getApi()->getEventBookingManager()->addLocationFilter($this->getBookingEngineName(), $_POST['data']['locationid']);
    }
   
    public function deleteEvent() {
        $this->getApi()->getEventBookingManager()->deleteEvent($this->getBookingEngineName(), $_POST['data']['eventid']);
    }
}
?>
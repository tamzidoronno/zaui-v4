<?php
namespace ns_f06d0c6a_882e_4592_917c_b74273edfd11;

class EventLister extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventLister";
    }

    public function render() {
        $loggedInUser = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        if ($loggedInUser && $loggedInUser->type > 10) {
            $this->includefile("timefilter");
        }
        $this->includefile("eventslist");
    }
    
    public function clearFilters() {
        $this->getApi()->getEventBookingManager()->clearFilters($this->getBookingEngineName());
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
        return $this->getApi()->getEventBookingManager()->getActiveLocations($this->getBookingEngineName());
    }
    
    public function applyFilter() {
        $this->getApi()->getEventBookingManager()->addLocationFilter($this->getBookingEngineName(), $_POST['data']['locationid']);
    }
   
    public function deleteEvent() {
        $this->getApi()->getEventBookingManager()->deleteEvent($this->getBookingEngineName(), $_POST['data']['eventid']);
    }
    
    public function setTimeFilter() {
        if ($_POST['data']['from'] && !$_POST['data']['to']) {
            $from = $this->convertToJavaDate(strtotime($_POST['data']['from']));
            $this->getApi()->getEventBookingManager()->setTimeFilter($this->getBookingEngineName(), $from, null);
        } else if (!$_POST['data']['from'] && !$_POST['data']['to']) {
            $this->getApi()->getEventBookingManager()->setTimeFilter($this->getBookingEngineName(), null, null  );
        } else if (!$_POST['data']['from'] && $_POST['data']['to']) {
            $to = $this->convertToJavaDate(strtotime($_POST['data']['to']));
            $this->getApi()->getEventBookingManager()->setTimeFilter($this->getBookingEngineName(), null, $to);
        } else {
            $from = $this->convertToJavaDate(strtotime($_POST['data']['from']));
            $to = $this->convertToJavaDate(strtotime($_POST['data']['to']));
            $this->getApi()->getEventBookingManager()->setTimeFilter($this->getBookingEngineName(), $from, $to);
        }
    }
}
?>
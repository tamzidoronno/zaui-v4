<?php
namespace ns_d5444395_4535_4854_9dc1_81b769f5a0c3;

class Event extends EventCommon implements \Application {
    public function getDescription() {
        
    }
    
    

    public function getName() {
        return "Event";
    }

    public function render() {
        $this->includefile("eventinformation");
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
            $this->includefile("booking");
        }
    }
    
    public function bookNow() {
        $this->getApi()->getEventBookingManager()->bookCurrentUserToEvent($this->getBookingEngineName(), $_POST['data']['eventid'], "web");
    }
    
    public static function formatMainStartDates($event) {
        return date("d/m-Y", strtotime($event->mainStartDate))."&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".date("H:i", strtotime($event->mainStartDate))." - ".date("H:i", strtotime($event->mainEndDate));
    }
    
    public static function formatDates($date1, $date2) {
        return date("d/m-Y", strtotime($date1))."&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".date("H:i", strtotime($date1))." - ".date("H:i", strtotime($date2));
    }
}
?>

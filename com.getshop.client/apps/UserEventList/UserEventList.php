<?php
namespace ns_138a1b9a_d8e3_4fec_8f3f_1cae11bca54f;

class UserEventList extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    private $mode = "";
    
    public function getDescription() {
        
    }

    public function getName() {
        return "UserEventList";
    }

    public function render() {
        $this->mode = "upcoming";
        $this->includefile("eventlist");
        
        $this->mode = "old";
        $oldEvents = $this->getEventsToPrint();
        if (count($oldEvents)) {
            echo "<div class='oldEvents'>";
            echo $this->wrapContentManager("oldTextRef", "Old Events");
            $this->includefile("eventlist");
            echo "</div>";
        }
    }
    
    private function downlaodEvents() {
        if (isset($this->events))
            return $this->events;
        
        $this->events = $this->getApi()->getEventBookingManager()->getEventsForUser($this->getBookingEngineName(), $this->getUserId());
        return $this->events;
    }
    
    public function getEventsToPrint() {
        $userId = $this->getUserId();
        $eventsFromMemory = $this->downlaodEvents();
        if (!$eventsFromMemory) {
            return [];
        }
        
        $eventsFromMemory = array_reverse($eventsFromMemory);
        
        $events = [];
        foreach ($eventsFromMemory as $event) {
            if ($event->isInFuture && $this->mode === "upcoming") {
                $events[] = $event;
            }
            if (!$event->isInFuture && $this->mode === "old") {
                $events[] = $event;
            }
        }
        
        $userId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        
        return $events;
    }

    public function getUserId() {
        return isset($_SESSION['ProMeisterSpiderDiager_current_user_id_toshow']) ? $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] : \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
    }

}
?>

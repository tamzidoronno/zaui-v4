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
    
    private function downlaodEvents($userId) {       
        $evnts = $this->getApi()->getEventBookingManager()->getEventsForUser($this->getBookingEngineName(), $userId);
        
        $events = [];
        foreach ($evnts as $event) {
            $event->currentUserId = $userId;
            $events[] = $event;
        }
        
        return $events;
    }
    
    public function getEventsToPrint() {
        $userId = $this->getUserId();
        $users = [];
        
        if ($userId == "company") {
            $retUsers = $this->getApi()->getUserManager()->getUsersByCompanyId($this->getApi()->getUserManager()->getLoggedOnUser()->companyObject->id);
            foreach ($retUsers as $user) {
                $users[] = $user->id;
            }
        } else {
            $users[] = $userId;
        }
        
        $eventsFromMemory = [];
        foreach ($users as $userId) {
            $eventsFromMemory = array_merge($eventsFromMemory, $this->downlaodEvents($userId));
        }
        
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
        
        return $events;
    }

    public function getUserId() {
        return isset($_SESSION['ProMeisterSpiderDiager_current_user_id_toshow']) ? $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] : \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
    }

}
?>

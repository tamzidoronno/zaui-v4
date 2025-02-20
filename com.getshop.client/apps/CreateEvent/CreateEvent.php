<?php
namespace ns_f4c3fce7_123c_4dcc_b9ce_dfea2ac6b755;

class CreateEvent extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CreateEvent";
    }

    public function getBookingEgineName() {
        return $this->getConfigurationSetting("bookingEngineName");
    }
    
    public function setBookingEngineName() {
        $this->setConfigurationSetting("bookingEngineName", $_POST['data']['bookingEngineName']);
    }

    private function printNotConnectedWarning() {
        $this->includefile("notspecifiedenginename");
    }
    
    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("createevent");
        }    
    }
    
    public function createEvent() {
        $event = new \core_eventbooking_Event();
        $event->bookingItem = new \core_bookingengine_data_BookingItem();
        if (isset($_POST['data']['entryId']) && $_POST['data']['entryId']) {
            $event = $this->getApi()->getEventBookingManager()->getEvent($this->getBookingEgineName(), $_POST['data']['entryId']);
        }
        $event->bookingItem->bookingSize = $_POST['data']['spots'];
        $event->bookingItem->fullWhenCountHit = $_POST['data']['spots'];
        $event->bookingItem->bookingItemTypeId = $_POST['data']['eventType'];
        $event->subLocationId = $_POST['data']['subLocationId'];
        $event->eventHelderUserId = $_POST['data']['selectedEventHelder'];
        $event->freeTextEventHelder = $_POST['data']['freetexteventhelder'];
        $event->extraInformation = $_POST['data']['extra'];
        
        $event->days = [];
        foreach ($_POST['data']['days'] as $dataDay) {
            $day = new \core_eventbooking_Day();
            $day->startDate = $this->convertToJavaDate(strtotime($dataDay['start']));
            $day->endDate = $this->convertToJavaDate(strtotime($dataDay['end']));
            $event->days[] = $day;
        }
        
        if ($event->id) {
            $this->getApi()->getEventBookingManager()->saveEvent($this->getBookingEgineName(), $event);
        } else {
            $event = $this->getApi()->getEventBookingManager()->createEvent($this->getBookingEgineName(), $event);
            $this->addUsersToEventIfNeeded($event);
            echo $event->bookingItem->pageId;
        }
        
        die();
    }
    
    public function markAsReady() {
        $this->getApi()->getEventBookingManager()->markAsReady($this->getBookingEgineName(), $_POST['data']['eventid']);
    }
    
    public function cancelEvent() {
        $this->getApi()->getEventBookingManager()->cancelEvent($this->getBookingEgineName(), $_POST['data']['eventid']);
    }
    
    public function unCancelEvent() {
        $this->getApi()->getEventBookingManager()->unCancelEvent($this->getBookingEgineName(), $_POST['data']['eventid']);
    }
    
    public function deleteEvent() {
        $this->getApi()->getEventBookingManager()->deleteEvent($this->getBookingEgineName(), $_POST['data']['eventid']);
    }
    
    public function toggleLocked() {
        $this->getApi()->getEventBookingManager()->toggleLocked($this->getBookingEgineName(), $_POST['data']['eventid']);
    }
    
    public function toggleHide() {
        $this->getApi()->getEventBookingManager()->toggleHide($this->getBookingEgineName(), $_POST['data']['eventid']);
    }

    /**
     * 
     * @param \core_eventbooking_Event $event
     * @return type
     */
    public function addUsersToEventIfNeeded($event) {
        if (!isset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'])) {
            return;
        }
        
        $usersArray = $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'];
        foreach ($usersArray as $userId => $location) {
            $this->getApi()->getEventBookingManager()->addUserToEvent($this->getBookingEgineName(), $event->id, $userId, true, "web_interests");
            $this->getApi()->getEventBookingManager()->removeInterest($this->getBookingEgineName(), $event->bookingItemType->id , $userId);
        }
        
        unset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers']);
        unset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/adminaction']);
    }

}
?>

<?php
namespace ns_f4c3fce7_123c_4dcc_b9ce_dfea2ac6b755;

class CreateEvent extends \MarketingApplication implements \Application {
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
        $event->bookingItem->bookingSize = $_POST['data']['spots'];
        $event->bookingItem->fullWhenCountHit = $_POST['data']['spots'];
        $event->bookingItem->bookingItemTypeId = $_POST['data']['eventType'];
        
        $event->days = [];
        foreach ($_POST['data']['days'] as $dataDay) {
            $day = new \core_eventbooking_Day();
            $day->startDate = $this->convertToJavaDate(strtotime($dataDay['start']));
            $day->endDate = $this->convertToJavaDate(strtotime($dataDay['end']));
            $event->days[] = $day;
        }
        
        $this->getApi()->getEventBookingManager()->createEvent($this->getBookingEgineName(), $event);
    }
}
?>

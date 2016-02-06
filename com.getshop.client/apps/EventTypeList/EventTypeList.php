<?php
namespace ns_9683701a_8217_411f_a321_26775f645f06;

class EventTypeList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventTypeList";
    }

    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("summary");
        }    
    }
    
    private function printNotConnectedWarning() {
        $this->includefile("notspecifiedenginename");
    }
    
    public function getBookingEgineName() {
        return $this->getConfigurationSetting("bookingEngineName");
    }
    
    public function setBookingEngineName() {
        $this->setConfigurationSetting("bookingEngineName", $_POST['data']['bookingEngineName']);
    }

    public function deleteEventType() {
        $this->getApi()->getBookingEngine()->deleteBookingItemType($this->getBookingEgineName(), $_POST['data']['value']);
    }

    public function getExtraAttributesToAppArea() {
        return "bookingeginename='".$this->getBookingEgineName()."'";
    }
}
?>

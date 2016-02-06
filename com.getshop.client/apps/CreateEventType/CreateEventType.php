<?php
namespace ns_49b1af76_d6e8_4f8a_a652_8f6ffab1812e;

class CreateEventType extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CreateEventType";
    }

    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->includefile("notspecifiedenginename");
        } else {
            $this->includefile("createNewType");
        }
    }
    
    public function getBookingEgineName() {
        return $this->getConfigurationSetting("bookingEngineName");
    }
    
    public function setBookingEngineName() {
        $this->setConfigurationSetting("bookingEngineName", $_POST['data']['bookingEngineName']);
    }
    
    public function craeteNewType() {
        $this->getApi()->getBookingEngine()->createABookingItemType($this->getBookingEgineName(), $_POST['data']['name']);
    }

}
?>

<?php
namespace ns_78b5c3b4_f475_40eb_aa87_171e78c24200;

class DayEventLister extends \ns_83df5ae3_ee55_47cf_b289_f88ca201be6e\EngineCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "DayEventLister";
    }

    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("eventDayLister");
        }
    }
    
    public function getBookingEgineName() {
        // This is hardcoded as ProMeister Academy uses 
        // templatepages, and therefor a new instance is automatically
        // created, and to avoid manually specifying the booking engine name.
        return "booking";
    }
}
?>

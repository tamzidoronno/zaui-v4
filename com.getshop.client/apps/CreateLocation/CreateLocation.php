<?php
namespace ns_a30bbe8f_15f7_4327_9f80_ece81ebd64e8;

class CreateLocation extends \ns_83df5ae3_ee55_47cf_b289_f88ca201be6e\EngineCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CreateLocation";
    }

    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("createlocation");
        }
    }
    
    public function createLocation() {
        $location = new \core_eventbooking_Location();
        $location->name = $_POST['data']['locationName'];
        $this->getApi()->getEventBookingManager()->saveLocation($this->getBookingEgineName(), $location);
    }
    
    public function deleteLocation() {
        $this->getApi()->getEventBookingManager()->deleteLocation($this->getBookingEgineName(), $_POST['data']['locationid']);
    }
}
?>

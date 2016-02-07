<?php
namespace ns_afec873d_d876_46c8_856c_2c599bada065;

class EditEventType extends \ns_83df5ae3_ee55_47cf_b289_f88ca201be6e\EngineCommon implements \Application {
    public function getDescription() {
        
    }

    public function addBookingItemType() {
        
    }
    
    public function preProcess() {
        if (isset($_GET['bookingItemTypeId'])) {
            $_SESSION['editevent_bookingItemTypeId'] = $_GET['bookingItemTypeId'];
        } 
   }
    
    public function getName() {
        return "EditEventType";
    }
    
    public function saveEvent() {
        $type = $this->getEventType();
        $type->name = $_POST['data']['name'];
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getBookingEgineName(), $type);
    }

    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includeFile("edit_event_type");
        }
    }
    
    public function getEventType() {
        return $this->getApi()->getBookingEngine()->getBookingItemType($this->getBookingEgineName(), $_SESSION['editevent_bookingItemTypeId']);
    }
}
?>

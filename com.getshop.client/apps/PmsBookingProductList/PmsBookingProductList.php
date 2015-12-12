<?php
namespace ns_ed7efba0_de37_4cd8_915b_cc7be10b8b8b;

class PmsBookingProductList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingProductList";
    }

    public function getAllProducts() {
        return $this->getApi()->getProductManager()->getAllProducts();
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }

    public function getCurrentBooking() {
        return $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function getText($key) {
        return $this->getConfigurationSetting($key);
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine.";
            return;
        }
        $this->includefile("productlist");
    }
    
    public function selectRoom() {
        $current = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        /* @var $current \core_pmsmanager_PmsBooking */
        
        if(sizeof($current->rooms) > 0) {
            $current->rooms[0]->bookingItemTypeId = $_POST['data']['typeid'];
        } else {
            $room = new \core_pmsmanager_PmsBookingRooms();
            $current->rooms = array();
            $room->bookingItemTypeId = $_POST['data']['typeid'];
            $current->rooms[] = $room;
        }
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $current);
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function getStartDate() {
        return strtotime($this->getCurrentBooking()->rooms[0]->date->start);
    }

    public function getEndDate() {
        $end = $this->getCurrentBooking()->rooms[0]->date->end;
        if($end) {
            return strtotime($end);
        }
        return $this->getStartDate()+86400;
    }

}
?>

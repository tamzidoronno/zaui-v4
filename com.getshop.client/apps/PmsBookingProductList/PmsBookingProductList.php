<?php
namespace ns_ed7efba0_de37_4cd8_915b_cc7be10b8b8b;

class PmsBookingProductList extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Display all that the user can book in a list view";
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
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        return $booking;
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
    
    public function selectRoomCount() {
        $current = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        /* @var $current \core_pmsmanager_PmsBooking */
        
        $newRooms = array();
        foreach($current->rooms as $idx => $room) {
            if($room->bookingItemTypeId != $_POST['data']['typeid']) {
                $newRooms[] = $room;
            }
        }
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        for($i = 0; $i < $_POST['data']['count']; $i++) {
            $room = new \core_pmsmanager_PmsBookingRooms();
            $room->bookingItemTypeId = $_POST['data']['typeid'];
            
            
            $start = strtotime(date("d.m.Y", strtotime($this->getCurrentBooking()->sessionStartDate)) . " " . $config->defaultStart);
            $end = strtotime(date("d.m.Y", strtotime($this->getCurrentBooking()->sessionEndDate)) . " " . $config->defaultEnd);

            $room->date->start = $this->convertToJavaDate($start);
            $room->date->end = $this->convertToJavaDate($end);
            $newRooms[] = $room;
        }
        
        $current->rooms = $newRooms;
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $current);
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function getStartDate() {
        if(!$this->getCurrentBooking()->sessionStartDate) {
            return $this->getCurrentBooking()->sessionStartDate;
        }
        return strtotime($this->getCurrentBooking()->sessionStartDate);
    }

    public function getEndDate() {
        $end = $this->getCurrentBooking()->sessionEndDate;
        if($end) {
            return strtotime($end);
        }
        return $this->getStartDate()+(86400*$this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName())->minStay);
    }

}
?>

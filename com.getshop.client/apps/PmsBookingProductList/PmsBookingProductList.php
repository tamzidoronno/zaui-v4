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
        if(!$booking->rooms || sizeof($booking->rooms) == 0) {
            $start = $this->convertToJavaDate(time());
            $end = $this->convertToJavaDate(time()+86400);

            $rooms = $this->getApi()->getPmsManager()->getAllRoomTypes($this->getSelectedName(), $start, $end);
            foreach($rooms as $room) {
                $availability = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedName(), $room->type->id, $start, $end);
                if($availability > 0) {
                    $newRoom = new \core_pmsmanager_PmsBookingRooms();
                    $newRoom->bookingItemTypeId = $room->type->id;
                    $newRoom->date = new \core_pmsmanager_PmsBookingDateRange();
                    $newRoom->date->start = $start;
                    $newRoom->date->end = $end;
                    $booking->rooms = array();
                    $booking->rooms[] = $newRoom;
                    $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
                    break;
                }
            }
        }
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
        
        for($i = 0; $i < $_POST['data']['count']; $i++) {
            $room = new \core_pmsmanager_PmsBookingRooms();
            $room->bookingItemTypeId = $_POST['data']['typeid'];
            $room->date->start = $this->convertToJavaDate($this->getStartDate());
            $room->date->end = $this->convertToJavaDate($this->getEndDate());
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
        return strtotime($this->getCurrentBooking()->rooms[0]->date->start);
    }

    public function getEndDate() {
        $end = $this->getCurrentBooking()->rooms[0]->date->end;
        if($end) {
            return strtotime($end);
        }
        return $this->getStartDate()+(86400*$this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName())->minStay);
    }

}
?>

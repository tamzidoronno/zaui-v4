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
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        /* @var $current \core_pmsmanager_PmsBooking */
        
        $room = new \core_pmsmanager_PmsBookingRooms();
        $current->rooms = array();
        $room->bookingItemTypeId = $_POST['data']['typeid'];
        $room->date = new \core_pmsmanager_PmsBookingDateRange();
        
        if($this->getCurrentBooking()->sessionStartDate) {
           $start = strtotime(date("d.m.Y", strtotime($this->getCurrentBooking()->sessionStartDate)) . " " . $config->defaultStart);
           $room->date->start = $this->convertToJavaDate($start);
        }
        if($this->getCurrentBooking()->sessionEndDate) {
           $end = strtotime(date("d.m.Y", strtotime($this->getCurrentBooking()->sessionEndDate)) . " " . $config->defaultEnd);
           $room->date->end = $this->convertToJavaDate($end);
        }

        
        $current->rooms[] = $room;
            
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
            $room->date = new \core_pmsmanager_PmsBookingDateRange();
            
            if($this->getCurrentBooking()->sessionStartDate) {
                $start = strtotime(date("d.m.Y", strtotime($this->getCurrentBooking()->sessionStartDate)) . " " . $config->defaultStart);
                $room->date->start = $this->convertToJavaDate($start);
            }
            if($this->getCurrentBooking()->sessionEndDate) {
                $end = strtotime(date("d.m.Y", strtotime($this->getCurrentBooking()->sessionEndDate)) . " " . $config->defaultEnd);
                $room->date->end = $this->convertToJavaDate($end);
            }

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

    /**
     * @return \core_pmsmanager_Room[]
     */
    public function getRooms() {
        $rooms = $this->getApi()->getPmsManager()->getAllRoomTypes($this->getSelectedName(), 
        $this->convertToJavaDate($this->getStartDate()), 
        $this->convertToJavaDate($this->getEndDate()));
        return $rooms;
    }

    public function getImagesFromPage($pageId) {
        $apps = $this->getApi()->getPageManager()->getApplicationsForPage($pageId);
        $imgIds = "";
        foreach($apps as $app) {
            $res = $this->getFactory()->getApplicationPool()->createAppInstance($app);
            if($res instanceOf \ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer) {
                /* @var $res \ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer */
                $imgIds[] = $res->getImageId();
            }
        }
        return $imgIds;
    }
    
    /**
     * @return \core_pmsmanager_PmsBooking
     */
    public function getBookings() {
        $booking = $this->getCurrentBooking();
        return $booking;
    }

}
?>

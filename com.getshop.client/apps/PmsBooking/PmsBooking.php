<?php
namespace ns_8dcbf529_72ae_47dd_bd6b_bd2d0c54b30a;

class PmsBooking extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "A simple booking view";
    }

    public function getName() {
        return "PmsBooking";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please set name of booking engine.";
            return;
        }
        $this->includefile("pmsfront_1");
    }
    
    public function initBooking() {
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());
        /* @var $booking \core_pmsmanager_PmsBooking */
        $range = new \core_pmsmanager_PmsBookingDateRange();
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $start = strtotime(date("d.m.Y", strtotime($_POST['data']['start'])) . " " . $config->defaultStart);
        $range->start = $this->convertToJavaDate($start);
        if(isset($_POST['data']['end'])) {
            $end = strtotime(date("d.m.Y", strtotime($_POST['data']['end'])) . " " . $config->defaultEnd);
            $range->end = $this->convertToJavaDate($end);
        }
        
        $room = new \core_pmsmanager_PmsBookingRooms();
        $room->bookingItemTypeId = $_POST['data']['product'];
        $room->date = $range;
        
        $booking->sessionStartDate = $range->start;
        $booking->sessionEndDate = $range->end;
        $booking->rooms = array();
        $booking->rooms[] = $room;

        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
        
    }
    
    public function getText($key) {
        return $this->getConfigurationSetting($key);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("booking_engine_name");
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
}
?>

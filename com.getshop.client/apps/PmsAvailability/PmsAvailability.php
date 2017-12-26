<?php
namespace ns_28886d7d_91d6_409a_a455_9351a426bed5;

class PmsAvailability extends \MarketingApplication implements \Application {
    private $types = null;
    private $items = null;
    private $currentBooking = null;
    private $currentBookingFromEngine = null;
    private $currentPmsRoom = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAvailability";
    }

    public function render() {
        $this->includefile("filter");
        $this->includefile("timelines");
        $this->includefile("bookingtemplate");
    }
    
    public function getData() {
        $filter = $this->getSelectedFilter();
        $result = $this->getApi()->getPmsManager()->getIntervalAvailability($this->getSelectedMultilevelDomainName(), $filter);
        return $result;
    }

    public function getSelectedFilter() {
        $filter = new \core_pmsmanager_PmsIntervalFilter();
        $filter->start = $this->convertToJavaDate(strtotime("11/01-2017"));
        $filter->end = $this->convertToJavaDate(strtotime("11/16-2017"));
        $filter->interval = 60*60*24;
        return $filter;
    }
    
    public function showBookingInformationExternal($bookingEngineBooking, $pmsBooking, $pmsRoom) {
        $this->currentBooking = $pmsBooking;
        $this->currentBookingFromEngine = $bookingEngineBooking;
        $this->currentPmsRoom = $pmsRoom;
        $this->includefile("movebooking");
    }
    
    public function showBookingInformation() {
        $this->setData();
        
        $this->includefile("generalinformation");
        
        if (!isset($_POST['data']['tab']) || !$_POST['data']['tab']) {
            $this->includefile("movebooking");
        }
    }
    
    /**
     * @return \core_pmsmanager_PmsBooking
     */
    public function getCurrentBooking() {
        return $this->currentBooking;
    }
    
    /**
     * 
     * @param type $id
     * @return \core_bookingengine_data_BookingItem
     */
    public function getBookingItem($id) {
        $this->loadAllItems();
        
        foreach ($this->items as $item) {
            if ($item->id == $id) {
                return $item;
            }
        }
        
        return null;
    }

    public function loadAllItems() {
        if (!$this->items) {
            $this->items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedMultilevelDomainName());
        }
    }

    /**
     * @return \core_bookingengine_data_BookingItem[]
     */
    public function getItems() {
        $this->loadAllItems();
        return $this->items;
    }

    /**
     * @return \core_bookingengine_data_BookingItemType[]
     */
    public function getTypes() {
        $this->loadTypes();
        return $this->types;
    }

    public function loadTypes() {
        if (!$this->types) {
            $this->types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedMultilevelDomainName());
        }
    }

    public function anyMatches($arr1, $arr2) {
        if (!$arr2)
            return false;
        
        if (!$arr1)
            return false;
        
        foreach ($arr1 as $i) {
            if (in_array($i, $arr2)) {
                return true;
            }
        }
        
        return false;
    }

    public function getNextBookingIds($itemTimeLines, $time) {
        $found = false;
        foreach ($itemTimeLines as $iTime => $data) {
            if ($found) {
                return $data->bookingIds;
            }
            if ($iTime == $time) {
                $found = true;
            }
        }
        
        return array();
    }

    public function setCurrentBooking() {
        $this->currentBooking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
    }

    public function setCurrentBookingFromBookingEngine() {
        $this->currentBookingFromEngine = $this->getApi()->getBookingEngine()->getBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
    }
    
    /**
     * 
     * @return \core_bookingengine_data_Booking
     */
    public function getCurrentBookingFromBookingEngine() {
        return $this->currentBookingFromEngine;
    }
    
    public function loaditemview() {
        $this->setData();
        $this->includefile("itemview");
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getCurrentRoom() {
        return $this->currentPmsRoom;
    }

    public static function sortByDate($a, $b) {
        $ad = strtotime($a);
        $bd = strtotime($b);
        
        if ($ad == $bd) {
            return 0;
        }

        return $ad < $bd ? -1 : 1;
    }
    
    public function setCurrentRoom() {
        if (!isset($_POST['data']['roomId'])) {
            return;
        }
        
        $booking = $this->getCurrentBooking();
        foreach ($booking->rooms as $room) {
            if ($room->pmsBookingRoomId == $_POST['data']['roomId']) {
                $this->currentPmsRoom = $room;
            }
        }
    }

    public function setData() {
        $this->setCurrentBooking();
        $this->setCurrentBookingFromBookingEngine();
        $this->setCurrentRoom();
    }

}
?>

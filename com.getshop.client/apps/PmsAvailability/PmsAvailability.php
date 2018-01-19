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
    }

    public function getData() {
        $filter = $this->getSelectedFilter();
        $result = $this->getApi()->getPmsManager()->getIntervalAvailability($this->getSelectedMultilevelDomainName(), $filter);
        return $result;
    }

    public function getSelectedFilter() {
        $filter = new \core_pmsmanager_PmsIntervalFilter();
        $filter->start = $this->convertToJavaDate($this->getStartDate());
        $filter->end = $this->convertToJavaDate($this->getEndDate());
        $filter->interval = 60 * 60 * 24;
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
    
    public function setNewStartDateAndRoomId() {
        $date = $this->convertToJavaDate(strtotime($_POST['data']['newStartDate']));
        $this->getApi()->getPmsManager()->setNewStartDateAndAssignToRoom($this->getSelectedMultilevelDomainName(), $_POST['data']['roomId'], $date, $_POST['data']['bookingitemid']);
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

    public function getStartDate() {
        if (!isset($_SESSION['PmsAvailability_startDate'])) {
            $date = strtotime('-3 days');
        } else {
            $date = strtotime($_SESSION['PmsAvailability_startDate']);
        }

        return strtotime(date("d.m.Y 16:00", $date));
    }

    public function getEndDate() {
        if (!isset($_SESSION['PmsAvailability_endDate'])) {
            $date = strtotime('+7 days');
        } else {
            $date = strtotime($_SESSION['PmsAvailability_endDate']);
        }

        return strtotime(date("d.m.Y 12:00", $date));
    }

    public function setStartDate($date) {
        $_SESSION['PmsAvailability_startDate'] = $date;
    }

    public function setEndDate($date) {
        $_SESSION['PmsAvailability_endDate'] = $date;
    }

    public function createNameOfBooker($data) {
        if ($data->state !== "normal") {
            return "<div class='otherdata'><i class='fa fa-trash'></i></div>";
        }
        
        $words = explode(" ", ucwords($data->name));
        $acronym = "";

        foreach ($words as $w) {
            $acronym .= $w[0];
        }

        $header = "<div class='bookername'><span class='fullname'>" . $data->name . "</span><span class='acronyme'>$acronym</span></div>";
        return $header;
    }

    public function getStart() {
        $data = $this->getData();
        if(isset($data->start)) {
            $time = $data->start;
        } else {
            $time = time();
        }
        return strtotime(date("d.m.Y 16:00", $time));
    }

    public function getEnd() {
        $data = $this->getData();
        $time = time()+(86400*7);
        if(isset($data->end)) {
            $time = $data->end;
        }
        return strtotime(date("d.m.Y 06:00", $time));
    }

}

?>

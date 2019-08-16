<?php

namespace ns_28886d7d_91d6_409a_a455_9351a426bed5;

class PmsAvailability extends \MarketingApplication implements \Application {

    private $types = null;
    private $items = null;
    private $currentBooking = null;
    private $currentBookingFromEngine = null;
    private $currentPmsRoom = null;
    private $activityPrinted = array();

    public function getDescription() {
        
    }
    
    public function loadEventData() {
        $this->includefile("eventseditarea");
    }

    
    public function updateevent() {
        $event = $this->getApi()->getPmsEventManager()->getEvent($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        $event->title = $_POST['data']['title'];
        $event->start = $this->convertToJavaDate(strtotime($_POST['data']['startdate']));
        $event->end = $this->convertToJavaDate(strtotime($_POST['data']['enddate']));
        $event->content = $_POST['data']['content'];
        $this->getApi()->getPmsEventManager()->saveEvent($this->getSelectedMultilevelDomainName(), $event);
    }
    
    public function deleteevent() {
        $this->getApi()->getPmsEventManager()->deleteEvent($this->getSelectedMultilevelDomainName(), $_POST['data']['eventid']);
    }

    public function setCategoryFilter($list) {
        $_SESSION['pmsavailbility_categories'] = json_encode($list);
    }
    public function getCategoryFilter() {
         if(isset($_SESSION['pmsavailbility_categories'])) {
            return json_decode($_SESSION['pmsavailbility_categories'], true);
        }
        return array();
    }
    
    public function setSortType($type) {
        $_SESSION['pmsavailbility_sorttype'] = $type;
    }
    
    public function getSortType() {
        if(isset($_SESSION['pmsavailbility_sorttype'])) {
            return $_SESSION['pmsavailbility_sorttype'];
        }
        return "category";
    }
    
    public function loadTitleOnConferenceRoom() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingengineid']);
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        echo $user->fullName;
    }

    public function getName() {
        return "PmsAvailability";
    }

    public function removeClosedOfUntil() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $newArray = array();
        foreach($config->closedOfPeriode as $periode) {
            if($periode->repeaterId == $_POST['data']['id']) {
                continue;
            }
            $newArray[] = $periode;
        }
        $config->closedOfPeriode = $newArray;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    
    
    public function render() {
        if($_SERVER['PHP_SELF'] == "/json.php" || isset($_SESSION['firstloadpage'])) {
            $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
            $app->setStartDate(date("d.m.Y", time()-86400));
            $app->setEndDate(date("d.m.Y", time()+(86400*14)));
        }

        $this->setPmsBookingIdsFilter();
        
        $this->includefile("warning");
        
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedMultilevelDomainName());

        if(sizeof($items) == 0) {
            $this->includefile("noitemsyet");
        } else {
            $this->includefile("waitinglist");
            $this->includefile("filter");
            $this->includefile("activites");
            $this->includefile("timelines");
        }
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
        $filter->pmsBookingIds = $this->getPmsBookingFilterIds(); 
        $filter->interval = 60 * 60 * 24;
        return $filter;
    }

    public function showBookingInformationExternal($bookingEngineBooking, $pmsBooking, $pmsRoom) {
        $this->currentBooking = $pmsBooking;
        $this->currentBookingFromEngine = $bookingEngineBooking;
        $this->currentPmsRoom = $pmsRoom;
        $this->includefile("movebooking");
    }

    public function getIdForBooking() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingengineid']);
        echo $booking->id;
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
            $this->types = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        }
        $allTypes = array();
        foreach($this->types as $type) {
            if($type->systemCategory == 1) {
                continue;
            }
            $allTypes[] = $type;
        }
        $this->types = $allTypes;
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

        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        
        return strtotime(date("d.m.Y " . $config->defaultStart, $date));
    }

    public function getEndDate() {
        if (!isset($_SESSION['PmsAvailability_endDate'])) {
            $date = strtotime('+7 days');
        } else {
            $date = strtotime($_SESSION['PmsAvailability_endDate']);
        }

        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        return strtotime(date("d.m.Y " . $config->defaultEnd, $date));
    }

    public function setStartDate($date) {
        $_SESSION['PmsAvailability_startDate'] = $date;
    }

    public function setEndDate($date) {
        $_SESSION['PmsAvailability_endDate'] = $date;
    }

    public function loadMarkedAreaBox() {
        $this->includefile("markedareabox");
    }
    
    public function addToWaitingList() {
        $this->getApi()->getPmsManager()->addToWaitingList($this->getSelectedMultilevelDomainName(), $_POST['data']['roomid']);
        unset($_SESSION['tmpselectedroom']);
    }
    
    public function updateStayTime() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']));
        $roomId = $_POST['data']['roomid'];
        $bookingId = $_POST['data']['bookingid'];
        $itemId = $_POST['data']['itemid'];
        $this->getApi()->getPmsManager()->setBookingItemAndDate($this->getSelectedMultilevelDomainName(), $roomId, $itemId, false, $start, $end);
        unset($_SESSION['tmpselectedroom']);
    }
    
    public function closeroom() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " 22:00"));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " 03:00"));
        $itemid = $_POST['data']['bookingitemid'];
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        
        $comment = "closed by: " . $user->fullName . ", ";
        $comment .= $_POST['data']['comment'];
        
        $this->getApi()->getPmsManager()->closeItem($this->getSelectedMultilevelDomainName(), $itemid, $start, $end, $comment);
        unset($_SESSION['tmpselectedroom']);
    }
    
    public function updateBookingDirect() {
        
        if(isset($_POST['data']['submit']) && $_POST['data']['submit'] == "openroom") {
            $this->getApi()->getBookingEngine()->deleteBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
            return;
        }
        
        $booking = $this->getApi()->getBookingEngine()->getBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
        
        $this->getApi()->getBookingEngine()->changeDatesOnBooking($this->getSelectedMultilevelDomainName(), 
                $_POST['data']['bookingid'], 
                $this->convertToJavaDate(strtotime($_POST['data']['start'] . " " . date("H:i", strtotime($booking->startDate)))), 
                $this->convertToJavaDate(strtotime($_POST['data']['end'] . " " . date("H:i", strtotime($booking->endDate)))));
        
        $this->getApi()->getBookingEngine()->changeSourceOnBooking($this->getSelectedMultilevelDomainName(), 
                $_POST['data']['bookingid'], $_POST['data']['source']);
        unset($_SESSION['tmpselectedroom']);
    }
    
    public function markRoomCleaned() {
        $itemid = $_POST['data']['itemid'];
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedMultilevelDomainName(), $itemid);
    }
    
    public function createNameOfBooker($data) {
        $words = explode(" ", ucwords($data->name));
        $acronym = "";

        foreach ($words as $w) {
//            if($w && strlen($w) > 0) {
//                $acronym .= $w[0];
//            }
        }
        if ($data->state !== "normal") {
            $markCleaning = "";
            $markRoomClean = "";
            if($data->state == "cleaning") { $markRoomClean = "<i class='fa fa-trash-o markRoomClean'></i> "; }
            return "<div class='bookername'><span class='fullname'>$markRoomClean" . $data->name . "</span><span class='acronyme'>$acronym</span></div>";
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

    public function deleteroom() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $_POST['data']['roomid']);
        $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedMultilevelDomainName(), $booking->id, $_POST['data']['roomid']);
        unset($_SESSION['tmpselectedroom']);
    }

    public function getBookingCount($timelines, $time, $typeIds) {
        
        $sorting = $this->getSortType();
        $allInOne = false;
        if($sorting == "room") {
            $allInOne = true;
        }

        $count = 0;
        foreach ($timelines->itemTimeLines as $itemId => $itemTimeLines) {
            foreach ($itemTimeLines as $itime => $date) {
                if (count($typeIds) && !in_array($date->typeId, $typeIds)) {
                    continue;
                }
                
                if ($itime == $time) {
                    $count += $date->count;
                }
            }
        }
        
        return $count;
    }

    public function getPmsBookingFilterIds() {
        if (!isset($_SESSION['PMS_AVIAILABILITY_BOOKING_IDS_FILTER'])) {
            return array();
        }
        
        return $_SESSION['PMS_AVIAILABILITY_BOOKING_IDS_FILTER'];
    }

    public function setPmsBookingIdsFilter() {
        if (isset($_GET['groupview']) && $_GET['groupview'] == "cancel") {
            unset($_SESSION['PMS_AVIAILABILITY_BOOKING_IDS_FILTER']);
            return;
        }
        
        if (isset($_GET['groupview'])) {
            $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $_GET['groupview']);
            $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
            $app->setStartDate(date("d.m.Y", strtotime($booking->startDate)));
            $app->setEndDate(date("d.m.Y", strtotime($booking->endDate)));
            $_SESSION['PMS_AVIAILABILITY_BOOKING_IDS_FILTER'] = array($_GET['groupview']);
        }
    }

    public function hasEventEntries($time, $lines, $type) {
        $offset = (date('z', $time) + 1) . "-" . date("Y", $time);
        if(isset($lines->lines->{$type})) {
            $entries = $lines->lines->{$type};
            if(isset($entries->entry->{$offset})) {
                return $entries->entry->{$offset};
            }
            return "";
        }
        return "";
    }

    public function printedNameOnActivity($id) {
        if(isset($this->activityPrinted[$id])) {
            return true;
        }
        $this->activityPrinted[$id] = true;
        return false;
    }

}

?>

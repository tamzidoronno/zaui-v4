<?php
namespace ns_af54ced1_4e2d_444f_b733_897c1542b5a8;

class PmsPaymentProcess extends \MarketingApplication implements \Application {
    private $pmsBookings = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsPaymentProcess";
    }
    
    public function reload() {
        
    }
    
    public function sendPaymentLinkRequest() {
        $message = $_POST['data']['message'];
        $prefix = $_POST['data']['prefix'];
        $phone = $_POST['data']['prefix'];
        $email = $_POST['data']['email'];
        $bookingId = $_POST['data']['roomid'];
        $engine = $this->getSelectedMultilevelDomainName();
        $message = str_replace("{name}", $_POST['data']['name'], $message);
        $msg = $this->getApi()->getPmsManager()->sendPaymentRequest($engine, $bookingId, $email, $prefix, $phone, $message);
    }
    
    public function render() {
        $this->setLoadState();
        
        if (!isset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state']) || $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "select_payment") {
            $this->includefile("select_payment");
        } elseif ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "bookings_summary") {
            $this->includefile('selectbookingdata');
        } elseif ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "paymentoverview") {
            $this->includefile('paymentoverview');
        } elseif ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "select_user") {
            $this->includefile("select_user");
        } elseif ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "select_rooms") {
            $this->includefile("select_rooms");
        } elseif ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "onlinepaymentrequest") {
            $this->includefile("onlinepaymentrequest");
        }
    }
    
    public function selectPaymentMethod() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod'] = $_POST['data']['method'];
        $this->goToSelectRooms();
    }
    
    public function startOnlinePaymentRequest() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'onlinepaymentrequest';
    }

    public function getNameOfSelectedPaymentMethod() {
        $method = $this->getApi()->getStoreApplicationPool()->getApplication($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod']);
        $appInstance = $this->getFactory()->getApplicationPool()->createInstace($method);
        return $appInstance->getName();
    }

    public function needToSelectRooms() {
        $paymentSelected = isset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod']) && $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod'];
        $bookings = $this->getSelectedBookings();
        $roomsCount = 0;
        
        foreach ($bookings as $booking) {
            $roomsCount += count($booking->rooms);
        }
        
        return $roomsCount > 1 && $paymentSelected;
    }
    
    /**
     * @return \core_pmsmanager_PmsBooking[]
     */
    public function getSelectedBookings() {
        if (!count($this->pmsBookings)) {
            foreach ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_roomids'] as $pmsBookingRoomId) {
                $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $pmsBookingRoomId);
                if (!$this->existInArray($booking->id, $this->pmsBookings)) {
                    $this->pmsBookings[] = $booking;
                }
            }
        }
        
        return $this->pmsBookings;
    }

    public function isSelectedRoom($roomId) {
        foreach ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_roomids'] as $pmsBookingRoomId) {
            if ($roomId == $pmsBookingRoomId)
                return true;
        }
        
        return false;
    }

    public function existInArray($id, $bookings) {
        foreach ($bookings as $booking) {
            if ($booking->id == $id)
                return true;
        }
        
        return false;
    }

    public function gotopaymentselection() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'select_payment'; 
   }
    
    public function setRoomIds() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_roomids'] = $_POST['data']['setRoomIds'];
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'bookings_summary';
    }

    public function formatRoomHeader($room) {
        $roomName = $room->bookingItemId ? $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $room->bookingItemId)->bookingItemName : "Floating";
        return $roomName;
    }
    
    public function applyDateFilter() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_startdate'] = $_POST['data']['startdate'];
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_enddate'] = $_POST['data']['enddate'];
    }
    
    public function createOrder() {
        $userId = $this->getUserIdToCreateOrderOn();
        $orderId = $this->getApi()->getPmsManager()->createOrderFromCheckout($this->getSelectedMultilevelDomainName(), $_POST['data']['rooms'], $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod'], $userId);
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_orderid'] = $orderId;
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = "paymentoverview";
    }

    public function clearState() {
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_roomids']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_startdate']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_enddate']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_skip_room_select']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_selected_userid']);
    }

    public function setLoadState() {
        if (isset($_POST['data']['state'])) {
            if ($_POST['data']['state'] == "clear") {
                $this->clearState();
            } else {
                $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = $_POST['data']['state'];
            }
        }
        
        if (isset($_POST['data']['pmsBookingRoomId'])) {
            $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_roomids'] = $_POST['data']['pmsBookingRoomId'];
        }
        
        if (isset($_POST['data']['orderId'])) {
            $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_orderid'] = $_POST['data']['orderId'];
        }
        
        if (isset($_POST['data']['skiproomselection'])) {
            $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_skip_room_select'] = true;
        }
    }

    /**
     * 
     * @param \core_pmsmanager_PmsRoomPaymentSummaryRow $row
     * @param type $startDate
     * @param type $endDate
     */
    public function removeDueToFilter($row, $startDate, $endDate) {
        if (!$startDate || !$endDate) {
            return false;
        }
        
        $rowDate = strtotime($row->date);
        $sDate = strtotime($startDate);
        $eDate = strtotime($endDate);
        $isWithin = $rowDate >= $sDate && $rowDate <= $eDate;
        
        return !$isWithin;
    }
    
    public function selectUser() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_selected_userid'] = $_POST['data']['userid'];
        $this->goToSelectRooms();
    }

    public function goToSelectRooms() {
        
        if ($this->checkIfNeedToSelectUser()) {
            return;
        }
        
        if ($this->checkIfShouldSkipRoomSelection()) {
            return;
        }
        
      
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'select_rooms';
    }

    public function getUserIdToCreateOrderOn() {
        $distinctUserIds = $this->getDistinctUserIds();
        
        if (count($distinctUserIds) == 1) {
            return $distinctUserIds[0];
        }
        
        if (isset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_selected_userid'])) {
            return $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_selected_userid'];
        }
        
        return false;
    }
    
    public function checkIfNeedToSelectUser() {
        $distinctUserIds = $this->getDistinctUserIds();
        
        if (count($distinctUserIds) < 2) {
            return false;
        }
        
        if ($this->getUserIdToCreateOrderOn()) {
            return false;
        }
        
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'select_user';
        
        return true;
    }
    
    public function checkIfShouldSkipRoomSelection() {
        $totalRooms = 0;
        
        foreach ($this->getSelectedBookings() as $booking) {
            $totalRooms += count($booking->rooms);
        }
        
        if (!isset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_skip_room_select'])) {
            $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_skip_room_select'] = $totalRooms == 1; 
        }
        
        if (isset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_skip_room_select']) && $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_skip_room_select']) {
            $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'bookings_summary';
            return true;
        } 
        
        return false;
    }

    public function getDistinctUserIds() {
        $distinctUserIds = array();
        
        foreach ($this->getSelectedBookings() as $booking) {
            if (!in_array($booking->userId, $distinctUserIds)) {
                $distinctUserIds[] = $booking->userId;
            }
        }
        
        return $distinctUserIds;
    }

    public function searchForConferences() {
        $searchResult = $this->getApi()->getPmsConferenceManager()->searchConferences($_POST['data']['searchWord']);
        
        if (!count($searchResult)) {
            echo $this->__f("No result found");
        } else {
            foreach ($searchResult as $result) {
                echo "<div class='row' style='border-top: solid 1px #DDD; border-bottom: solid 1px #CCC;'>";
                    echo "<div class='col' style='font-size: 16px; width: calc(100% - 143px);'>".$result->meetingTitle."</div>";
                    echo "<div class='col'><div class='shop_button add_pos_tab' conferenceid='$result->id'>".$this->__f("Add to checkout")."</div></div>";
                echo "</div>";
            }
        }
    }
    
    public function getPosTabContent() {
        $this->includefile("postabcontent");
    }
}
?>

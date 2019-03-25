<?php
namespace ns_af54ced1_4e2d_444f_b733_897c1542b5a8;

class PmsPaymentProcess extends \MarketingApplication implements \Application {
    private $pmsBookings = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsPaymentProcess";
    }

    public function render() {
        $this->setLoadState();
        
        if (isset($_POST['data']['pmsBookingRoomId'])) {
            $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_roomids'] = $_POST['data']['pmsBookingRoomId'];
        }
        
        
        if (!isset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state']) || $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "select_payment") {
            $this->includefile("select_payment");
        } elseif ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "bookings_summary") {
            $this->includefile('selectbookingdata');
        } elseif ($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] == "select_rooms") {
            $this->includefile("select_rooms");
        }
    }
    
    public function selectPaymentMethod() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod'] = $_POST['data']['method'];
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'select_rooms';
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

    public function gotoroomselection() {
        $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = 'select_rooms';
    }
    
    public function createOrder() {
        $this->getApi()->getPmsManager()->createOrderFromCheckout($this->getSelectedMultilevelDomainName(), $_POST['data']['rooms']);
    }

    public function clearState() {
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_roomids']);
        unset($_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_paymentmethod']);
    }

    public function setLoadState() {
        if (isset($_POST['data']['state'])) {
            if ($_POST['data']['state'] == "clear") {
                $this->clearState();
            } else {
                $_SESSION['ns_af54ced1_4e2d_444f_b733_897c1542b5a8_state'] = $_POST['data']['state'];
            }
        }
    }

}
?>

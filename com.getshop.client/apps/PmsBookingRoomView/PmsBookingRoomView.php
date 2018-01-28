<?php
namespace ns_f8cc5247_85bf_4504_b4f3_b39937bd9955;

class PmsBookingRoomView extends \MarketingApplication implements \Application {
    
    private $types = array();
    
    private $items = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingRoomView";
    }

    public function render() {
        $this->setModalVariable();
        $this->includefile("bookingoverview");
    }

    public function setRoomId($roomId) {
        $_SESSION['PmsBookingRoomView_current_pmsroom_id'] = $roomId;
    }

    private function getPmsRoom() {
        $pmsBooking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        
        foreach ($pmsBooking->rooms as $room) {
            if ($room->bookingId == $_POST['data']['id']) {
                return $room;
            }
        }
        return null;
    }
    public function updateBooking() {
        $this->setData();
        $selectedRoom = $this->getPmsRoom();
        $this->updateGuests($selectedRoom);
        $this->setStay($selectedRoom);
        $this->updateUnitPrices($selectedRoom);
        $this->setData(true);
        $this->includefile("bookingoverview");
        
        if ($this->isModalView()) {  
            $this->closeModal();
        }
        
        die();
    }
    
    public function refresh() {
    }
    
    public function loaditemview() {
        $this->includefile("itemview");
    }

    public function hasUnsettledAmount() {
        $booking = $this->getPmsBooking();
        $this->setData();
        
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->avoidOrderCreation = true;
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($booking->endDate));

        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $this->getPmsBooking()->id, $filter);    

        $itemCount = count($this->getApi()->getCartManager()->getCart()->items);
        
        echo $itemCount > 0 ? "YES" : "NO";
        die();
    }
    
    public function setStay($selectedRoom) {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $typeId = explode("_", $_POST['data']['itemid']);
        $itemId = "";
        
        if (count($typeId) == 2) {
            $itemId = $typeId[1];
            $typeId = $typeId[0];
        } else {
            $typeId = $typeId[0];
        }
        
        $this->getApi()->getPmsManager()->setBookingItemAndDate(
                $this->getSelectedMultilevelDomainName(), 
                $selectedRoom->pmsBookingRoomId,
                $itemId, 
                false, 
                $start, 
                $end);
    }


    public function getChannelMatrix() {
        $channels = $this->getApi()->getPmsManager()->getChannelMatrix($this->getSelectedMultilevelDomainName());
        
        if($this->channels) {
            return $this->channels;
        }
        
        $this->channels = $channels;
        return $channels;
    }

    public function forceAccessRegardlessOfPayment() {
        $this->setData();
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $this->getSelectedRoom()->pmsBookingRoomId) {
                $room->forceAccess = true;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->setData();
        $this->includefile("accesscode");
        die();
    }
    
    public function ungrantPayment() {
        $this->setData();
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $this->getSelectedRoom()->pmsBookingRoomId) {
                $room->forceAccess = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->setData();
        $this->includefile("accesscode");
        die();
    }

    public function startPaymentProcess() {
        if (isset($_SESSION['payment_process_cart_'.$this->getSelectedRoom()->pmsBookingRoomId])) {
            $this->includefile("paymentprocess_started_for_room");
            return false;
        }
        
        $bookingId = $this->getPmsBooking()->id;
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $bookingId);

        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->avoidOrderCreation = true;
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($booking->endDate));

        $this->getApi()->getCartManager()->clear();
        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $bookingId, $filter);    

        $itemCount = count($this->getApi()->getCartManager()->getCart()->items);
        if (!$itemCount) {
            return false;
        }
        
        echo "<div class='SalesPointCartCheckout'>";
        $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salesPointCartCheckout->setOriginalCart();
        $salesPointCartCheckout->render();
        echo "</div>";
        
        
        return true;
    }
    
    public function renderPaymentProcess() {
        $this->includefile("paymentprocess");
    }

    public function updateCartAndPrice() {
        $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salesPointCartCheckout->updateCartAndPrice();
    }
    
    public function subMenuChanged() {
        $_SESSION['currentSubMenu'] = $_POST['data']['selectedTab'];
        $this->renderTabContent();
        die();
    }

    public function isTabActive($tabname) {
        if (isset($_SESSION['currentSubMenu']) && $_SESSION['currentSubMenu'] == $tabname) {
            echo "active";
        }
        
        echo "";
    }
    private function isModalView() {
        return $this->getModalVariable("roomid");
    }
    
    private function setModalVariable() {
        if ($this->isModalView()) {    
            
            $_SESSION['PmsBookingRoomView_current_pmsroom_id'] = $this->getModalVariable("roomid");
            $this->setData();
        } 
    }

    public function reloadApp() {
        $this->setData();
    }
    
    public function setData($reload = false) {
        
        if(!isset($_SESSION['cachedroomspmsrooms'])) {
            $_SESSION['cachedroomspmsrooms'] = array();
        }
        if(!isset($_SESSION['pmsbookings'])) {
            $_SESSION['pmsbookings'] = array();
        }
        if(!isset($_SESSION['cachedbookings'])) {
            $_SESSION['cachedbookings'] = array();
        }
        
        if (!isset($_SESSION['PmsBookingRoomView_current_pmsroom_id']) && !$reload) {
            return;
        }
        
        if (isset($_SESSION['cachedroomspmsrooms'][$_SESSION['PmsBookingRoomView_current_pmsroom_id']])) {
            $this->selectedRoom = json_decode($_SESSION['cachedroomspmsrooms'][$_SESSION['PmsBookingRoomView_current_pmsroom_id']]);
            $this->bookingEngineBooking = json_decode($_SESSION['cachedbookings'][$this->selectedRoom->bookingId]);
            $this->pmsBooking = json_decode($_SESSION['cachedpmsbookings'][$_SESSION['PmsBookingRoomView_current_pmsroom_id']] );
        } else {
            if (!isset($this->pmsBooking) || $reload) {
                $this->pmsBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $_SESSION['PmsBookingRoomView_current_pmsroom_id']);
                $_SESSION['cachedpmsbookings'][$_SESSION['PmsBookingRoomView_current_pmsroom_id']] = json_encode($this->pmsBooking);
            }

            foreach ($this->pmsBooking->rooms as $room) {
                if ($room->pmsBookingRoomId == $_SESSION['PmsBookingRoomView_current_pmsroom_id']) {
                    $_SESSION['cachedroomspmsrooms'][$room->pmsBookingRoomId] = json_encode($room);
                    $this->selectedRoom = $room;
                }
            }

            $this->bookingEngineBooking = $this->getApi()->getBookingEngine()->getBooking($this->getSelectedMultilevelDomainName(), $this->selectedRoom->bookingId);
            $_SESSION['cachedbookings'][$this->selectedRoom->bookingId] = json_encode($this->bookingEngineBooking);
        }
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getPmsBooking() {
        $this->setData();
        return $this->pmsBooking;
    }
    
    /**
     * 
     * @return \core_bookingengine_data_Booking
     */
    public function getBookingEngineBooking() {
        $this->setData();
        return $this->bookingEngineBooking;
    }

    /**
     * @return \core_usermanager_data_User 
     */
    public function getUserForBooking() {
        if (!isset($this->currentUserForBooking)) {
            $this->currentUserForBooking = $this->getApi()->getUserManager()->getUserById($this->pmsBooking->userId);
        }

        return $this->currentUserForBooking;
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getSelectedRoom() {
        $this->setData();
        return $this->selectedRoom;
    }

    public function saveUser($user) {
        $this->setData();

        if ($user->address->countrycode) {
            $booking = $this->getPmsBooking();
            $booking->countryCode = $user->address->countrycode;
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        }
        
        $this->setData(true);
    }
    
       
    public function createNewUser(){
        $name = $_POST['data']['name'];
        $bookingId = $this->getPmsBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, "");
        $this->setData(true);
        return $this->getUserForBooking();
    }
   
    
    public function changeUser($user) {
        $this->setData();
        $booking = $this->getPmsBooking();
        $booking->userId = $user->id;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        
        $this->setData(true);
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $bookingId = $this->getPmsBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, $vat);
        
        $this->setData(true);
        return $this->getUserForBooking();
    }

    private function updateGuests($selectedRoom) {
        $guests = array();
        
        foreach ($_POST['data'] as $key => $value) {
            $rowDel = explode("_", $key);
            if (count($rowDel) === 3 && $rowDel[0] === "guestinfo") {
                $guests[$rowDel[1]][$rowDel[2]] = $value;
            }
        }
        
        $guestsToUse = array();
        foreach ($guests as $count => $guest) {
            $guestsToUse[] = $guest;
        }
        
        $pmsBooking = $this->getPmsBooking();
        $this->getApi()->getPmsManager()->setGuestOnRoom($this->getSelectedMultilevelDomainName(), $guestsToUse, $pmsBooking->id, $selectedRoom->pmsBookingRoomId);
        
    }

    public function renderTabContent() {
        if ($_SESSION['currentSubMenu'] == "log") {
            $this->includefile("log");
        }
        if ($_SESSION['currentSubMenu'] == "orders") {
            $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
            $this->includefile("orderstab");
        } 
    }

    public function addCurrentCartForPaymentProcess() {
        $this->setData();
        $roomId = $this->getSelectedRoom()->pmsBookingRoomId;
        $salespoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salespoint->addCurrentCartForPaymentProcess($roomId);
        
        $this->includefile("orderstab");
        die();
    }
    
    public function cancelPaymentProcess() {
        $this->setData();
        $roomId = $this->getSelectedRoom()->pmsBookingRoomId;
        $salespoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salespoint->cancelPaymentProcessForRoom($roomId);
        
        $this->includefile("orderstab");
        die();
    }
    
    public function cancelAllPaymentProcesses() {
        $salespoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salespoint->cancelAllPaymentProcesses();
        die();
    }

    public function getCartsForPaymentProcess() {
        $carts = array();
        foreach ($_SESSION as $key => $value) {
            if (strstr($key, "payment_process_cart_") > -1) {
                try {
                    $cart = json_decode($_SESSION[$key]);
                } catch (Exception $ex) {
                    continue;
                }
                @$keyArr = explode("_", $key);
                @$cart->roomId = $keyArr[3];
                $carts[] = $cart;
            }
        }
        return $carts;
    }

    public function doPaymentNormal() {
        $this->setData();
        
        $salesPoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $cart = $salesPoint->getTmpCartForRoom($_POST['data']['roomid']);
        
        $app = $this->getApi()->getStoreApplicationPool()->getApplication($_POST['data']['paymentid']);
        $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
        $order = $instance->doPayment($cart);
        $order->createByManager = "SalesPoint";
        $this->getApi()->getOrderManager()->saveOrder($order);
        
        $this->sendPaymentLinkIfNeeded($order);
        $this->setData(true);
    }

    public function sendPaymentLinkIfNeeded($order) {
        if (isset($_POST['data']['sendPaymentLink']) && $_POST['data']['sendPaymentLink'] !== "true") {
            return;
        }        
        
        $pmsBooking = $this->getPmsBooking();
        $email = $_POST['data']['email'];
        $prefix = $_POST['data']['prefix'];
        $phone = $_POST['data']['cellphone'];
        $smsText = $_POST['data']['textMessage'];
        
        $this->getApi()->getPmsManager()->sendPaymentLinkWithText($this->getSelectedMultilevelDomainName(), $order->id, $pmsBooking->id, $email, $prefix, $phone, $smsText);
    }

    public function updateUnitPrices($selectedRoom) {
        $priceMatrix = array();
        
        foreach ($_POST['data'] as $key => $value) {
            if (strpos($key, "room_price_") > -1) {
                $arr = explode("_", $key);
                $date = $arr[2];
                $priceMatrix[$date] = $value;
            }
        }
        
        $this->setData(true);
        $pmsBooking = $this->getPmsBooking();
        foreach ($pmsBooking->rooms as $room) {
            if ($room->pmsBookingRoomId == $this->getSelectedRoom()->pmsBookingRoomId) {
                $room->priceMatrix = $priceMatrix;
            }
        }
        
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $pmsBooking);
    }


    public function invoiceNoteChanged() {
        $this->setData();
        $booking = $this->getPmsBooking();
        $booking->invoiceNote = $_POST['data']['invoicenote'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->setData();
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
    
    public function markRoomCleaned() {
        $this->setData();
        $room = $this->getSelectedRoom();
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
        
        $this->setData();
        $this->includefile("accesscode");
        die();
    }
    
    public function markRoomCleanedwithoutlog() {
        $this->setData();
        $room = $this->getSelectedRoom();
        $this->getApi()->getPmsManager()->markRoomAsCleanedWithoutLogging($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
        $this->setData();
        $this->includefile("accesscode");
        die();
    }
    
    public function markRoomDirty() {
        $this->setData();
        $room = $this->getSelectedRoom();
        $this->getApi()->getPmsManager()->markRoomDirty($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
        
        $this->setData();
        $this->includefile("accesscode");
        die();
    }
    
    public function blockAccessToRoom() {
        $this->setData();
        $booking = $this->getPmsBooking();
        $room = $this->getSelectedRoom();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $room->pmsBookingRoomId) {
                $room->blocked = true;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->setData();
        $this->includefile("accesscode");
        die();
    }
    
    public function unBlockRoom() {
        $this->setData();
        $booking = $this->getPmsBooking();
        $room = $this->getSelectedRoom();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $room->pmsBookingRoomId) {
                $room->blocked = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->setData();
        $this->includefile("accesscode");
        die();
    }
}
?>

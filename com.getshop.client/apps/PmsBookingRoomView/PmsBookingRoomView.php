<?php
namespace ns_f8cc5247_85bf_4504_b4f3_b39937bd9955;

class PmsBookingRoomView extends \MarketingApplication implements \Application {
    
    private $types = array();
    
    private $items = array();
    
    /* @var $selectedRoom \core_pmsmanager_PmsBookingRooms */
    private $selectedRoom;
    
    public function getDescription() {
        
    }
    
    public function updateGuestData() {
        $room = $this->getSelectedRoom();
        $i = 0;
        $newGuests = array();
        foreach($_POST['data'] as $guestPost) {
            $guest = null;
            foreach($room->guests as $g) {
                if($g->guestId == $guestPost['guestId']) {
                    $guest = $g;
                }
            }
            if(!$guest) {
                $guest = new \core_pmsmanager_PmsGuests();
                $room->guests[] = $guest;
            }
            $guest->name = $guestPost['name'];
            $guest->email = $guestPost['email'];
            $guest->prefix = $guestPost['prefix'];
            $guest->phone = $guestPost['phone'];
            $i++;
            $newGuests[] = $guest;
        }
        $room->guests = $newGuests;
        $room->numberOfGuests = sizeof($room->guests);
        $this->updateRoom($room);
    }
    
    public function updatePriceMatrixWithPeriodePrices() {
        $this->setData();
        $room = $this->getSelectedRoom();
        $amount = $_POST['data']['periodePrice'];
        switch($_POST['data']['periodePriceType']) {
            case "dayprice":
                foreach($room->priceMatrix as $day => $val) {
                    $room->priceMatrix->{$day} = $amount;
                }
                break;
            case "wholestay":
                foreach($room->priceMatrix as $day => $val) {
                    $days = sizeof(array_keys((array)$room->priceMatrix));
                    $room->priceMatrix->{$day} = round(($amount / $days), 2);
                }
                break;
            case "start_of_stay":
                $time = strtotime($room->date->start);
                while(true) {
                    $start = $time;
                    $end = strtotime("+1 month", $time);
                    $time = strtotime("+1 month", $time);
                    
                    $datediff = $end - $start;
                    $days = floor($datediff / (60 * 60 * 24));
                    $avg = $amount / $days;
                    
                    foreach($room->priceMatrix as $day => $val) {
                        $dayInTime = strtotime($day . " 23:59");
                        if($dayInTime >= $start && $dayInTime < $end) {
                            $room->priceMatrix->{$day} = round($avg,2);
                        }
                    }
                    
                    if($end > strtotime($room->date->end)) {
                        break;
                    }
                }
                break;
            case "start_of_month":
                $time = strtotime($room->date->start);
                $time = strtotime(date("01.m.Y", $time));
                while(true) {
                    $start = $time;
                    $end = strtotime("+1 month", $time);
                    $time = strtotime("+1 month", $time);
                    
                    $datediff = $end - $start;
                    $days = floor($datediff / (60 * 60 * 24));
                    $avg = $amount / $days;
                    
                    echo date("d.m.Y", $start) . " - ";
                    echo date("d.m.Y", $end) . " ($days)" . "<br>";
                    
                    foreach($room->priceMatrix as $day => $val) {
                        $dayInTime = strtotime($day . " 23:59");
                        if($dayInTime >= $start && $dayInTime < $end) {
                            $room->priceMatrix->{$day} = round($avg,2);
                        }
                    }
                    
                    if($end > strtotime($room->date->end)) {
                        break;
                    }
                }
                break;
            case "whole_stay":
                $avg = $amount / sizeof($room->priceMatrix);
                foreach($room->priceMatrix as $key => $val) {
                    $room->priceMatrix[$key] = $avg;
                }
                break;
        }
        $this->updateRoom($room);
    }

    
    
    public function updateAddons() {
        $this->setData();
        
        $room = $this->getSelectedRoom();
        $newAddonList = array();
        foreach($room->addons as $addon) {
            if($_POST['data']['type']== "save") {
                $addon->date = $this->convertToJavaDate(strtotime($_POST['data'][$addon->addonId."_date"]));
                $addon->count = $_POST['data'][$addon->addonId."_count"];
                $addon->price = $_POST['data'][$addon->addonId."_price"];
            }
            if($_POST['data']['type']== "delete" && $_POST['data'][$addon->addonId."_delete"] == "true") {
                continue;
            }
            
            $newAddonList[] = $addon;
        }
        $room->addons = $newAddonList;
        $this->updateRoom($room);
    }

    public function loadEditEvent() {
        $this->includefile("editaddons");
    }
    
    public function getName() {
        return "PmsBookingRoomView";
    }

    public function render() {
        $this->setModalVariable();
        $this->includefile("bookingoverview");
    }

    public function loadChangesPanel() {
        $this->includefile("differenceinroom");
    }
    
    public function discardChanges() {
        $this->clearCache();
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
    
    public function reloadAddons() {
        $this->includefile("addons");
    }
    
    public function saveRoom() {
        $booking = $this->getPmsBooking();
        $room = $this->getSelectedRoom();
        $toAdd = array();
        foreach($booking->rooms as $r) {
            if($room->pmsBookingRoomId == $r->pmsBookingRoomId) {
                continue;
            }
            $toAdd[] = $r;
        }
        $toAdd[] = $room;
        $booking->rooms = $toAdd;
        
        $text = $this->createReadableDiffText($this->getDifferenceInRoom());
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $item = "";
        if(isset($room->bookingItemId)) {
            $item = $room->bookingItemId;
        }
        $this->getApi()->getPmsManager()->logEntry($this->getSelectedMultilevelDomainName(), $text, $booking->id, $item);
        $this->setData(true);
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
    
    public function updateAvialablity() {
        $room = $this->getSelectedRoom();
        if (isset($_POST['data']['itemId']) && isset($_POST['data']['itemId'])) {
            $room->bookingItemId = $_POST['data']['itemId'];
        }
        if (isset($_POST['data']['typeId']) && isset($_POST['data']['typeId'])) {
            $room->bookingItemTypeId = $_POST['data']['typeId'];
        }
        if (isset($_POST['data']['start']) && isset($_POST['data']['end'])) {
            $room->date->start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
            $room->date->end = $this->convertToJavaDate(strtotime($_POST['data']['end']));  
            $newPricesRoom = $this->getApi()->getPmsManager()->getPrecastedRoom($this->getSelectedMultilevelDomainName(), 
                    $room->pmsBookingRoomId, 
                    $room->bookingItemTypeId, 
                    $room->date->start, 
                    $room->date->end);
            foreach($room->priceMatrix as $day => $val) {
                foreach($newPricesRoom->priceMatrix as $day2 => $val2) {
                    if($day2 == $day) {
                        $newPricesRoom->priceMatrix->{$day} = $val;
                    }
                }
            }
            $room->priceMatrix = $newPricesRoom->priceMatrix;
        }
        
        $this->updateRoom($room);

    }
    
    public function loaditemview() {
        $this->setData();
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
        
        $this->refreshCartForRoom();
        
        $cart = $this->getApi()->getCartManager()->getCart();
        $itemCount = count($cart->items);
        if (!$itemCount) {
            return false;
        }
        
        echo "<div class='SalesPointCartCheckout' roomid='".$this->getSelectedRoom()->pmsBookingRoomId."'>";
        $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salesPointCartCheckout->setOriginalCart($cart);
        $salesPointCartCheckout->setReadOnly();
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
        $this->render();
    }
    
    public function clearCache() {
        unset($_SESSION['cachedroomspmsrooms']);
        unset($_SESSION['cachedbookings']);
        unset($_SESSION['cachedpmsbookings']);
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
        
        if (isset($_SESSION['cachedroomspmsrooms'][$_SESSION['PmsBookingRoomView_current_pmsroom_id']]) && !$reload) {
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
                    $this->updateRoom($room, true);
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
    
    public function sendMessage() {
        $type = $_POST['data']['submit'];
        $message = $_POST['data']['message'];
        $room = $this->getSelectedRoom();
        for($i = 0; $i <= 30; $i++) {
            if(!isset($_POST['data']['sendtoguest_'.$i])) {
                continue;
            }
            if($_POST['data']['sendtoguest_'.$i] !== "true") {
                continue;
            }
            $prefix = $_POST['data']['prefix_'.$i];
            $phone = $_POST['data']['phone_'.$i];
            $email = $_POST['data']['email_'.$i];
            $guestId = $_POST['data']['guestid_'.$i];
            if($type == "sendassms") {
                $this->getApi()->getPmsManager()->sendSmsOnRoom($this->getSelectedMultilevelDomainName(), $prefix, $phone, $message, $room->pmsBookingRoomId);
            }
            if($type == "sendasemail") {
                //sendMessageOnRoom(String email, String title, String message, String roomId)
                $this->getApi()->getPmsManager()->sendMessageOnRoom($this->getSelectedMultilevelDomainName(), $email, "", $message, $room->pmsBookingRoomId);
            }
            if($type == "sendasboth") {
                $this->getApi()->getPmsManager()->sendSmsOnRoom($this->getSelectedMultilevelDomainName(), $prefix, $phone, $message, $room->pmsBookingRoomId);
                $this->getApi()->getPmsManager()->sendMessageOnRoom($this->getSelectedMultilevelDomainName(), $email, "", $message, $room->pmsBookingRoomId);
            }
        }
    }
    
    public function resendConfirmation() {
        $email = $_POST['data']['email'];
        $this->setData();
        $booking = $this->getPmsBooking();
        $bookingId = $booking->id;
        $this->getApi()->getPmsManager()->sendConfirmation($this->getSelectedMultilevelDomainName(), $email, $bookingId, "");
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
        if ($_SESSION['currentSubMenu'] == "addons") {
            $this->printAddonsArea();
        }
        if ($_SESSION['currentSubMenu'] == "messages") {
            $this->printMessages();
        }
        if ($_SESSION['currentSubMenu'] == "cleaning") {
            $this->printCleaning();
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
        $this->setData(true);
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

    public function updateRoom($room, $avoidChanges = false) {
        if(!$avoidChanges) {
            $room->hasBeenUpdated = true;
        }
        $_SESSION['cachedroomspmsrooms'][$room->pmsBookingRoomId] = json_encode($room);
    }

    public function printAddonsArea() {
        echo "<br>";
        echo "<span class='addonsArea'>";
        $this->includefile("addons");
        echo "</span>";
    }
    
    public function removeSelectedAddons() {
        $this->setData();
        $room = $this->selectedRoom;
        $keep = array();
        foreach($room->addons as $addon) {
            if(!in_array($addon->productId, $_POST['data']['productIds'])) {
                $keep[] = $addon;
            }
        }
        $room->addons = $keep;
        $this->updateRoom($room);
    }

    public function getAddonsCost() {
        $this->setData();
        $room = $this->getSelectedRoom();
        $totalAmount = 0.0;
        foreach($room->addons as $addon) {
            $totalAmount += $addon->price * $addon->count;
        }
        return $totalAmount;
    }

    public function getStayPrice() {
        $this->setData();
        $room = $this->getSelectedRoom();
        $total = 0.0;
        foreach($room->priceMatrix as $day => $val) {
            $total += $val;
        }
        return $total;
    }

    public function printMessages() {
        $this->includefile("messages");
    }

    public function markRoomCleanedWithoutLogging() {
        $itemId = $this->getSelectedRoom()->bookingItemId;
        $this->getApi()->getPmsManager()->markRoomAsCleanedWithoutLogging($this->getSelectedMultilevelDomainName(), $itemId);
        $this->clearCache();
    }
    
    public function markRoomCleanedWithLogging() {
        $itemId = $this->getSelectedRoom()->bookingItemId;
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedMultilevelDomainName(), $itemId);
        $this->clearCache();
    }
    
    public function setCleaningComment() {
        $roomId = $_POST['data']['roomid'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomId);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomId) {
                $room->cleaningComment = $_POST['data']['comment'];
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }

    public function setCleaningDate() {
        $roomId = $_POST['data']['roomid'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomId);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomId) {
                $room->date->cleaningDate = $this->convertToJavaDate(strtotime($_POST['data']['date']));
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function setCleaningInterval() {
        $roomId = $_POST['data']['roomid'];
        $interval = $_POST['data']['interval'];
        $this->getApi()->getPmsManager()->setNewCleaningIntervalOnRoom($this->getSelectedMultilevelDomainName(), $roomId, $interval);
        $this->clearCache();
    }
    
    
    public function renewCodeForRoom() {
        $this->setData();
        $code = $this->getApi()->getPmsManager()->generateNewCodeForRoom($this->getSelectedMultilevelDomainName(), $this->getSelectedRoom()->pmsBookingRoomId);
        $room = $this->getSelectedRoom();
        $room->code = $code;
        $this->updateRoom($room);
        $this->includefile("accesscode");
    }
    
    public function resendCodeForRoom() {
        $this->setData();
        $room = $this->getSelectedRoom();
        $roomId = $room->pmsBookingRoomId;
        $prefix = $_POST['data']['prefix'];
        $phoneNumber = $_POST['data']['phone'];
        $this->getApi()->getPmsManager()->sendCode($this->getSelectedMultilevelDomainName(), $prefix, $phoneNumber, $roomId);
        $this->includefile("accesscode");
    }

    public function printCleaning() {
        $this->includefile("cleaning");
    }

    /**
     * 
     * @return \core_pmsmanager_PmsLog[]
     */
    public function getSelectedRoomLog() {
        if(isset($this->roomLog)) {
            return $this->roomLog;
        }
        $filter = new \core_pmsmanager_PmsLog();
        $filter->roomId = $this->getSelectedRoom()->pmsBookingRoomId;
        $logs = $this->getApi()->getPmsManager()->getLogEntries($this->getSelectedMultilevelDomainName(), $filter);
        $this->roomLog = $logs;
        return $logs;
    }

    public function getRoom($roomId) {
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $roomId);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomId) {
                return $room;
            }
        }
        return null;
    }

    public function transferSelectedToCart() {
        $this->setData();
        
        $selectedRoom = null;
        
        $this->removeRoomFromCart();
        $this->refreshCartForRoom();
        
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $this->getSelectedRoom()->pmsBookingRoomId);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $this->getSelectedRoom()->pmsBookingRoomId) {
                $selectedRoom = $room;
                break;
            }
        }
        
        if (!$selectedRoom) {
            die("Could not find the room.");
        }
        
        if (!$selectedRoom->orderUnderConstructionId) {
            if (isset($_SESSION['orderUnderConstructionId'])) {
                $selectedRoom->orderUnderConstructionId = $_SESSION['orderUnderConstructionId'];
            } else {
                $selectedRoom->orderUnderConstructionId = $this->uuidV4();
            }
            
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        }
        
        echo $selectedRoom->orderUnderConstructionId;

        $_SESSION['orderUnderConstructionId'] = $selectedRoom->orderUnderConstructionId;
   
        $cart = $this->getApi()->getCartManager()->getCart();

        $this->getApi()->getOrderManager()->updateCartOnOrderUnderConstruction($selectedRoom->orderUnderConstructionId, $selectedRoom->pmsBookingRoomId, $cart);
        $this->setData(true);
    }
    
    public function removeRoomFromCart() {
        $id = $this->getSelectedRoom()->orderUnderConstructionId;

        $this->getApi()->getOrderManager()->removeRoomForOrderUnderConstruction($id, $this->getSelectedRoom()->pmsBookingRoomId);
        
        $booking = $this->getPmsBooking();
        
        foreach ($booking->rooms as $room) {
            if ($room->pmsBookingRoomId == $this->getSelectedRoom()->pmsBookingRoomId) {
                $room->orderUnderConstructionId = "";
            }
        }
        
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    private function uuidV4() {
        return sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',

          // 32 bits for "time_low"
          mt_rand(0, 0xffff), mt_rand(0, 0xffff),

          // 16 bits for "time_mid"
          mt_rand(0, 0xffff),

          // 16 bits for "time_hi_and_version",
          // four most significant bits holds version number 4
          mt_rand(0, 0x0fff) | 0x4000,

          // 16 bits, 8 bits for "clk_seq_hi_res",
          // 8 bits for "clk_seq_low",
          // two most significant bits holds zero and one for variant DCE1.1
          mt_rand(0, 0x3fff) | 0x8000,

          // 48 bits for "node"
          mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
        );
    }

    public function getDifferenceInRoom() {
        $totalChanges = 0;
        $unsavedRoom = $this->getSelectedRoom();
        $savedBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $unsavedRoom->pmsBookingRoomId);
        $savedRoom = null;
        $changes = array();
        foreach($savedBooking->rooms as $room) {
            if($room->pmsBookingRoomId == $unsavedRoom->pmsBookingRoomId) {
                $savedRoom = $room;
                break;
            }
        }
        
        //Pricematrix diff.
        $dayPriceOnSavedRoom = 0.0;
        foreach($savedRoom->priceMatrix as $val) { $dayPriceOnSavedRoom += $val; }
        $dayPriceOnUnsavedRoom = 0.0;
        foreach($unsavedRoom->priceMatrix as $val) { $dayPriceOnUnsavedRoom += $val; }
        $diff = $dayPriceOnUnsavedRoom-$dayPriceOnSavedRoom;
        if($diff > 0) { $changes['pricematrix'] = "Stay cost increased by " . $diff; }
        if($diff < 0) { $changes['pricematrix'] = "Stay cost decreased by " . $diff; }
        
        //Has the room or type been changed?
        if($savedRoom->bookingItemTypeId != $unsavedRoom->bookingItemTypeId) {
            $savedType = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $savedRoom->bookingItemTypeId)->name;
            $unSavedType = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $unsavedRoom->bookingItemTypeId)->name;
            $changes['typechange'] = "Room type has bene changed from <b>" . $savedType . "</b> to <b>" . $unSavedType . "</b>";
        }
        if($savedRoom->bookingItemId != $unsavedRoom->bookingItemId) {
            $savedItem = "Floating";
            if($savedRoom->bookingItemId) {
                $savedItem = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $savedRoom->bookingItemId)->bookingItemName;
            }
            $unSavedItem = "Floating";
            if($unsavedRoom->bookingItemId) {
                $unSavedItem = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $unsavedRoom->bookingItemId)->bookingItemName;
            }
            $changes['itemchange'] = "Room type has bene changed from <b>" . $savedItem . "</b> to <b>" . $unSavedItem . "</b>";
        }
        
        $totalChanges += $diff;
        
        //Stay periode diff.
        $unsavedStart = strtotime($unsavedRoom->date->start);
        $savedStart = strtotime($savedRoom->date->start);
        $unsavedEnd = strtotime($unsavedRoom->date->end);
        $savedEnd = strtotime($savedRoom->date->end);
        
        if($unsavedStart != $savedStart) { $changes['startdate'] = "Checkin changed from: " . date("d.m.Y H:i", $savedStart) . " to " . date("d.m.Y H:i", $unsavedStart); }
        if($unsavedEnd != $savedEnd) { $changes['enddate'] = "Checkout changed from: " . date("d.m.Y H:i", $savedEnd) . " to " . date("d.m.Y H:i", $unsavedEnd); }
        
        //Addons added.
        $changes['newAddon'] = array();
        foreach($unsavedRoom->addons as $unsavedAddon) {
            $found = false;
            foreach($savedRoom->addons as $savedAddon) {
                if($savedAddon->addonId == $unsavedAddon->addonId) {
                    $found = true;
                }
            }
            if(!$found) {
                $changes['newAddon'][] = $unsavedAddon;
            }
        }
        
        
        //Addons removed.
        $changes['removedAddon'] = array();
        foreach($savedRoom->addons as $savedAddon) {
            $found = false;
            foreach($unsavedRoom->addons as $unsavedAddon) {
                if($savedAddon->addonId == $unsavedAddon->addonId) {
                    $found = true;
                }
            }
            if(!$found) {
                $changes['removedAddon'][] = $savedAddon;
            }
        }

        //Addons updated.
        $changes['changedaddons'] = array();
        foreach($savedRoom->addons as $savedAddon) {
            $found = false;
            foreach($unsavedRoom->addons as $unsavedAddon) {
                if($savedAddon->addonId == $unsavedAddon->addonId) {
                    $totalChanges += ($unsavedAddon->count*$unsavedAddon->price)-($savedAddon->count*$savedAddon->price);
                    $savedAddon->price = $unsavedAddon->price - $savedAddon->price;
                    $savedAddon->count = $unsavedAddon->count - $savedAddon->count;
                    if($savedAddon->count != 0 || $savedAddon->price != 0) {
                        $changes['changedaddons'][] = $savedAddon; 
                    }
                }
            }
        }
        $changes['totalchanges'] = $totalChanges;
        
        $changes['guestchanges'] = "";
        
        //Guests added
        foreach($unsavedRoom->guests as $guest) {
            if(!$guest->guestId) {
                $changes['guestchanges'] .= "<div>Guest added : ". $guest->name . " - " . $guest->email . " - " . $guest->prefix . " - " . $guest->phone . "</div>";
            }
        }
        
        //Guests removed
        foreach($savedRoom->guests as $guest) {
            $found = false;
            foreach($unsavedRoom->guests as $unsavedGuest) {
                if($guest->guestId == $unsavedGuest->guestId) {
                    $found = true;
                }
            }
            if(!$found) {
                $changes['guestchanges'] .= "<div>Guest removed : ". $guest->name . " - " . $guest->email . " - " . $guest->prefix . " - " . $guest->phone . "</div>";
            }
        }
        
        //Guests changed
        foreach($savedRoom->guests as $guest) {
            $found = false;
            $savedGuest = $guest; 
            $unsavedGuest = null;
            foreach($unsavedRoom->guests as $unsaved) {
                if($guest->guestId == $unsaved->guestId) {
                    $unsavedGuest = $unsaved;
                }
            }
            
            if($unsavedGuest != null && $savedGuest->email && $savedGuest->email != $unsavedGuest->email) {
                $changes['guestchanges'] .= "<div>Email changed from " . $savedGuest->email . " to " . $unsavedGuest->email ."</div>";
            }
            
            if($unsavedGuest != null && $savedGuest->phone && $savedGuest->phone != $unsavedGuest->phone) {
                $changes['guestchanges'] .= "<div>Phone changed from " . $savedGuest->phone . " to " . $unsavedGuest->phone ."</div>";
            }
            
            if($unsavedGuest != null && $savedGuest->prefix && $savedGuest->prefix != $unsavedGuest->prefix) {
                $changes['guestchanges'] .= "<div>Phone prefix changed from " . $savedGuest->prefix . " to " . $unsavedGuest->prefix ."</div>";
            }
            
            if($unsavedGuest != null && $savedGuest->name && $savedGuest->name != $unsavedGuest->name) {
                $changes['guestchanges'] .= "<div>Name changed from " . $savedGuest->name . " to " . $unsavedGuest->name ."</div>";
            }
        }
        
        
        return $changes;
    }

    public function createReadableDiffText($diffs) {
        $text = "";
        foreach($diffs as $key => $diff) {
            if($key == "newAddon") { continue; }
            if($key == "removedAddon") { continue; }
            if($key == "changedaddons") { continue; }
            if($key == "totalchanges") { continue; }
            $text .= "<div class='changetextrow'>" . $diff . "</div>";
        }
        
        
        if(sizeof($diffs['newAddon'])) {
            $text .= "<div class='changetextrow'>";
            $text .= "New addons added:<br>";
            foreach($diffs['newAddon'] as $addon) {
                /* @var $addon \core_pmsmanager_PmsBookingAddonItem */
                $text .= date("d.m.Y", strtotime($addon->date)) . " : " . $this->getApi()->getProductManager()->getProduct($addon->productId)->name . " : " . $addon->count . " x " . $addon->price . "<br>";
            }
            $text .= "</div>";
        }
        
        if(sizeof($diffs['changedaddons'])) {
            $text .= "<div class='changetextrow'>";
            $text .= "Changes in addons:<br>";
            foreach($diffs['changedaddons'] as $addon) {
                /* @var $addon \core_pmsmanager_PmsBookingAddonItem */
                $text .= date("d.m.Y", strtotime($addon->date)) . " : " . $this->getApi()->getProductManager()->getProduct($addon->productId)->name . " : " . $addon->count . " x " . $addon->price . "<br>";
            }
            $text .= "</div>";
        }
        
        
        if(sizeof($diffs['removedAddon'])) {
            $text .= "<div class='changetextrow'>";
            $text .= "Addons removed:<br>";
            foreach($diffs['removedAddon'] as $addon) {
                /* @var $addon \core_pmsmanager_PmsBookingAddonItem */
                $text .= date("d.m.Y", strtotime($addon->date)) . " : " . $this->getApi()->getProductManager()->getProduct($addon->productId)->name . " : " . $addon->count . " x " . $addon->price . "<br>";
            }
            $text .= "</div>";
        }
        $text .= "<div class='changetextrow' style='font-weight:bold;'>Changes in total: ".$diffs['totalchanges']."</div>";
        
        return $text;
    }
    
    public function isRoomAddedToCart() {
        $id = $this->getSelectedRoom()->orderUnderConstructionId;
        return $this->getApi()->getOrderManager()->isRoomAddedToOrderUnderConstruction($id, $this->getSelectedRoom()->pmsBookingRoomId);
    }

    public function refreshCartForRoom() {
        $booking = $this->getPmsBooking();
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->avoidOrderCreation = true;
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($booking->endDate));
        $filter->pmsRoomIds = array();
        $filter->pmsRoomIds[]  = $this->getSelectedRoom()->pmsBookingRoomId;

        $this->getApi()->getCartManager()->clear();
        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $booking->id, $filter);    
    }
}
?>

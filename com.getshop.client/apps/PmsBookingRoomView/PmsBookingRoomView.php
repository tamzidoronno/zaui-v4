<?php
namespace ns_f8cc5247_85bf_4504_b4f3_b39937bd9955;

class PmsBookingRoomView extends \MarketingApplication implements \Application {
    
    private $types = array();
    
    private $items = array();
    
    private $logEntries = array();
    
    private $cachedProducts = array();
    
    private $defaultPrefix;
    
    
    /* @var $selectedRoom \core_pmsmanager_PmsBookingRooms */
    public $selectedRoom;
    public $pmsBooking;

    public function getDescription() {
        
    }
 
    public static function sortByIncrementalOrderId($a, $b) {
        return $b->incrementOrderId > $a->incrementOrderId;
    }
    
    public static function sortSummaryRowByDate($a, $b) {
        $aDate = strtotime($a->date);
        $bDate = strtotime($b->date);
        
        if ($aDate == $bDate) {            
            return strcmp($a->createOrderOnProductId, $b->createOrderOnProductId);
        }
        
        return $aDate > $bDate;
    }
    
    public function confirmbooking() {
        $booking = $this->getPmsBooking();
        $booking->confirmed = true;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->getApi()->getPmsManager()->logEntry($this->getSelectedMultilevelDomainName(), "booking confirmed", $booking->id,null);
    }
    
    public function splitStay() {
        $roomId = $_POST['data']['roomid'];
        $splitTime = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['time']));
        $this->getApi()->getPmsManager()->splitStay($this->getSelectedMultilevelDomainName(), $roomId, $splitTime);
        $this->toggleListGroupRooms();
    }
    
    public function changeCountryCodeOnBooking() {
        $booking = $this->getPmsBooking();
        $booking->countryCode = $_POST['data']['gsvalue'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function changeDiscountCode() {
        $code = $_POST['data']['code'];
        $booking = $this->getPmsBooking();
        $booking->couponCode = $code;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function segmentDiscountCode() {
        $segment = $_POST['data']['segment'];
        $booking = $this->getPmsBooking();
        $booking->segmentId = $segment;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function deleteWaitingListRoom() {
        $id = $_POST['data']['id'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $id);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $id) {
                $room->deleted = true;
                $room->addedToWaitingList = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function togglerefundable() {
        $booking = $this->getPmsBooking();
        $room = $this->getSelectedRoom();
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $room->pmsBookingRoomId) {
                $r->nonrefundable = !$r->nonrefundable;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }

    public function toggleCreateAfterStay() {
        $this->setData();
        $booking = $this->getPmsBooking();
        $booking->createOrderAfterStay = !$booking->createOrderAfterStay;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }

    public function toggleautocharging() {
        $this->setData();
        $booking = $this->getPmsBooking();
        $booking->tryAutoCharge = !$booking->tryAutoCharge;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function loadGroupPayment() {
        $this->includefile("grouppaymentselection");
    }
    
    public function quickAction() {
        $this->includefile("doquickaction");
    }
    
    public function deleteComment() {
        $booking = $this->getPmsBooking();
        foreach($booking->comments as $time => $val) {
            if($time == $_POST['data']['time']) {
                $val->deleted = !$val->deleted;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function addComment() {
        $comment = $_POST['data']['comment'];
        $booking = $this->getPmsBooking();
        $this->getApi()->getPmsManager()->addComment($this->getSelectedMultilevelDomainName(), $booking->id, $comment);
    }
    
    public function removeFromWaitingList() {
        $roomId = $_POST['data']['id'];
        $this->getApi()->getPmsManager()->removeFromWaitingList($this->getSelectedMultilevelDomainName(), $roomId);
    }
    
    public function loadQuickMenu($type) {
        ?>
        <div style="padding: 10px;" gstype="form" method="quickAction">
            <input type='hidden' gsname='type' value='<?php echo $_POST['data']['type']; ?>'>
            <input type='hidden' gsname='roomid' value='<?php echo $_POST['data']['roomid']; ?>'>
        <?php
        switch($type) {
            case "changeroom":
            case "changecategory":
            case "delete":
            case "changeprice":
            case "changestay":
            case "updateaddons":
            case "togglenonref":
            case "guestinfo":
                echo "<div style='text-align:right; border-bottom: solid 1px #bbb; padding-bottom: 10px; margin-bottom: 10px;'><i class='fa fa-close' style='cursor:pointer;' onmousedown='$(\".quickmenuoption\").hide();'></i></div>";
                $this->setRoomId($_POST['data']['roomid']);
                $this->includefile("quickmenu_" . $type);
                break;
            default:
                echo "Not found: " . $type;
                break;
        }
        ?>
        </div>
        <?php
    }
    
    public function isGroupBookingView() {
        if($this->getPage()) {
            $_SESSION['cachedgroupedbooking'] = $this->getPage()->getId() == "groupbooking";
            return $this->getPage()->getId() == "groupbooking";
        }
        if(isset($_SESSION['cachedgroupedbooking'])) {
            return $_SESSION['cachedgroupedbooking'];
        }
        return false;
    }
    
    public function deleteRoom() {
        $room = $this->getSelectedRoom();
        $booking = $this->getPmsBooking();
//        $error = $this->getApi()->getPmsManager()->cancelRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId);
        $error = $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedMultilevelDomainName(),$booking->id, $room->pmsBookingRoomId);
        if($error) {
            $this->errors[] = $error;
        }
        $this->pmsBooking = null;
        $this->selectedRoom = null;
        $this->clearCache();
    }
    
    public function cancelRoom() {
        $room = $this->getSelectedRoom();
        $booking = $this->getPmsBooking();
        $error = $this->getApi()->getPmsManager()->cancelRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId);
        if($error) {
            $this->errors[] = $error;
        }
        $this->pmsBooking = null;
        $this->selectedRoom = null;
        $this->clearCache();
    }
    
    public function moveToWaitingList() {
        $room = $this->getSelectedRoom();
        $this->getApi()->getPmsManager()->addToWaitingList($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId);
        $this->clearCache();
    }
    
    public function removeFromOverBookingList() {
        $room = $this->getSelectedRoom();
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $r) {
            if($room->pmsBookingRoomId == $r->pmsBookingRoomId) {
                $room->overbooking = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->pmsBooking = null;
        $this->selectedRoom = null;
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
        $tmpRoom = $this->getTmpSelectedRoom($room->pmsBookingRoomId);
        $room = $this->doUpdatePriceMatrixWithPeriodePrice($tmpRoom);
        $room->priceMatrixChanged = true;
        $this->setTmpSelectedRoom($room);
    }

    
    
    public function updateAddons() {
        $booking = $this->getPmsBooking();
        $newAddonList = array();
        foreach($booking->rooms as $room) {
            foreach($room->addons as $addon) {
                if($_POST['data']['type']== "save") {
                    if(isset($_POST['data'][$addon->addonId."_date"])) {
                        $addon->date = $this->convertToJavaDate(strtotime($_POST['data'][$addon->addonId."_date"]));
                        $addon->count = $_POST['data'][$addon->addonId."_count"];
                        $addon->price = $_POST['data'][$addon->addonId."_price"];
                    }
                }
                if($_POST['data']['type']== "delete" && $_POST['data'][$addon->addonId."_delete"] == "true") {
                    continue;
                }
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking); 
    }
    
    public function switchToNewPaymentWindow() {
        $_SESSION['use_new_payment_view'] = true;
    }

    public function loadEditEvent() {
        $this->includefile("editaddons");
    }
    
    public function getName() {
        return "PmsBookingRoomView";
    }

    public function creditOrDeleteOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        
        if (!$order->closed) {
            $this->getApi()->getOrderManager()->deleteOrder($_POST['data']['orderid']);
            return;
        }
        
        $creditedOrder = $this->getApi()->getOrderManager()->creditOrder($order->id);
        $booking = $this->getPmsBooking();
        
        if($booking) {
            $booking->orderIds[] = $creditedOrder->id;
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
            $this->getApi()->getOrderManager()->saveOrder($creditedOrder);
        }
            
        
    }
    
    public function render() {
        echo "<div class='room_view_outer' usenewpayment='".$this->shouldUseNewPaymentWindow()."'>";
            if(isset($_POST['data']['getshop_resetlistmode']) && $_POST['data']['getshop_resetlistmode'] == "true") {
                $this->removeGroupList();
            }
            $this->setModalVariable();
            $this->includefile("bookingoverview");
        echo "</div>";
    }
    
    public function shouldUseNewPaymentWindow() {
        return isset($_SESSION['use_new_payment_view']);
    }

    public function switchToOldVersion() {
        unset($_SESSION['use_new_payment_view']);
    }
    public function loadChangesPanel() {
        $this->includefile("differenceinroom");
    }
    
    public function loadCheckout() {
        $app = new \ns_2e51d163_8ed2_4c9a_a420_02c47b1f7d67\PmsCheckout();
        $app->renderApplication(true);
    }
    
    public function discardChanges() {
        $this->clearCache();
    }
    
    public function setRoomId($roomId) {
        $_SESSION['PmsBookingRoomView_current_pmsroom_id'] = $roomId;
        $this->changeModalVariable("roomid", $roomId);
    }

    private function getPmsRoom() {
        if(isset($this->selectedRoom)) {
            return $this->selectedRoom;
        }
        $pmsBooking = $this->getPmsBooking();
        $roomId = $this->getSelectedRoomId();
        
        foreach ($pmsBooking->rooms as $room) {
            if ($room->pmsBookingRoomId == $roomId) {
                $this->selectedRoom = $room;
                return $room;
            }
        }
        return null;
    }
    
    public function printAddAddonsArea() {
        $addAddons = new \ns_b72ec093_caa2_4bd8_9f32_e826e335894e\PmsAddAddonsList();
        $addAddons->renderApplication(true);
    }
    
    public function reloadAddons() {
        $this->includefile("addons");
    }
    
    public function saveRoom() {
        $selectedRoom = $this->getTmpSelectedRoom($_POST['data']['roomid']);
        
        $curRoom = null;
        $curBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $selectedRoom->pmsBookingRoomId);
        foreach($curBooking->rooms as $r) {
            if($r->pmsBookingRoomId != $selectedRoom->pmsBookingRoomId) {
                continue;
            }
            $curRoom = $r;
        }
        
        //Update stay
        $roomId = $selectedRoom->pmsBookingRoomId;
        $start = $this->convertToJavaDate(strtotime($selectedRoom->date->start));
        $end = $this->convertToJavaDate(strtotime($selectedRoom->date->end));
        $itemId = $selectedRoom->bookingItemId;
        $typeId = $selectedRoom->bookingItemTypeId;
        
        $changed = false;
        $bookingItemChanged = false;
        $bookingTypeChanged = false;
        
        if($curRoom->bookingItemId != $itemId) {
            $changed = true;
            $bookingItemChanged = true;
        }
        if($curRoom->bookingItemTypeId != $typeId) {
            $bookingTypeChanged = true;
        }
        if(date("dmyHi", strtotime($start)) != date("dmyHi", strtotime($curRoom->date->start))) {
            $changed = true;
        }
        if(date("dmyHi", strtotime($end)) != date("dmyHi", strtotime($curRoom->date->end))) {
            $changed = true;
        }
        if($changed && $bookingItemChanged) {
            $this->getApi()->getPmsManager()->setBookingItemAndDate($this->getSelectedMultilevelDomainName(), $roomId,$itemId,false, $start, $end);
        } else if($changed) {
            $this->getApi()->getPmsManager()->changeDates($this->getSelectedMultilevelDomainName(), $curRoom->pmsBookingRoomId, $curBooking->id, $start, $end);
        } else if($bookingTypeChanged) {
          $this->getApi()->getPmsManager()->setNewRoomType($this->getSelectedMultilevelDomainName(), $curRoom->pmsBookingRoomId, $curBooking->id, $typeId);  
        }
        
        //Update price matrix
        if(isset($selectedRoom->priceMatrixChanged) && $selectedRoom->priceMatrixChanged) {
            $this->getApi()->getPmsManager()->updatePriceMatrixOnRoom($this->getSelectedMultilevelDomainName(), $selectedRoom->pmsBookingRoomId, $selectedRoom->priceMatrix);
        }
        
        $this->removeTmpRoom($selectedRoom->pmsBookingRoomId);
    }
    
    public function updateBooking() {
        $this->setData();
        $selectedRoom = $this->getPmsRoom();
        $this->updateGuests($selectedRoom);
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
        $room = $this->getTmpSelectedRoom($_POST['data']['roomId']);
        if (isset($_POST['data']['start']) && isset($_POST['data']['end'])) {
            $ignorecanadd = false;
            if($room->deleted || $room->addedToWaitingList || $room->overbooking) {
                $ignorecanadd = true;
            }
            $bookingsToAdd = $room->booking;
            $bookingsToAdd->startDate = $this->convertToJavaDate(strtotime($_POST['data']['start']));
            $bookingsToAdd->endDate = $this->convertToJavaDate(strtotime($_POST['data']['end']));
            if (isset($_POST['data']['itemId']) && isset($_POST['data']['itemId'])) {
                $bookingsToAdd->bookingItemId = $_POST['data']['itemId'];
            }
            if (isset($_POST['data']['typeId']) && isset($_POST['data']['typeId'])) {
                $bookingsToAdd->bookingItemTypeId = $_POST['data']['typeId'];
            }
            if($this->getApi()->getBookingEngine()->canAddBooking($this->getSelectedMultilevelDomainName(), $bookingsToAdd) || $ignorecanadd) {

                if (isset($_POST['data']['itemId']) && isset($_POST['data']['itemId'])) {
                    $room->bookingItemId = $_POST['data']['itemId'];
                }
                if (isset($_POST['data']['typeId']) && isset($_POST['data']['typeId'])) {
                    $room->bookingItemTypeId = $_POST['data']['typeId'];
                }

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
            } else {
                $room->date->start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
                $room->date->end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
                $room->warningText = "We are not able to change to the specified dates.";
                $_SESSION['tmpselectedroom'][$room->pmsBookingRoomId] = json_encode($room);
                return;
            }
        }
        $this->setTmpSelectedRoom($room);

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
        $filter->startInvoiceAt = $this->convertToJavaDate(strtotime($booking->startDate));

        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $this->getPmsBooking()->id, $filter);    

        $itemCount = count($this->getApi()->getCartManager()->getCart()->items);
        
        echo $itemCount > 0 ? "YES" : "NO";
        die();
    }
    
    public function setStay($selectedRoom) {
        $start = $this->convertToJavaDate(strtotime($selectedRoom->date->start));
        $end = $this->convertToJavaDate(strtotime($selectedRoom->date->end));
        $typeId = $selectedRoom->bookingItemTypeId;
        $itemId = $selectedRoom->bookingItemId;
        $split = false;
        if(isset($selectedRoom->doSplitChange)) {
            $split = $selectedRoom->doSplitChange;
        }
        if($split) {
            $currentBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $selectedRoom->pmsBookingRoomId);
        }
        
        $this->getApi()->getPmsManager()->setBookingItemAndDate(
                $this->getSelectedMultilevelDomainName(), 
                $selectedRoom->pmsBookingRoomId,
                $itemId, 
                $split, 
                $start, 
                $end);
        
        if($split) {
            $newBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $selectedRoom->pmsBookingRoomId);
            $roomIds = array();
            foreach($currentBooking->rooms as $r) {
                $roomIds[] = $r->pmsBookingRoomId;
            }
            foreach($newBooking->rooms as $r) {
                if(!in_array($r->pmsBookingRoomId, $roomIds)) {
                    $this->setRoomId($r->pmsBookingRoomId);
                    $this->selectedRoom = null;
                    $this->pmsBooking = null;
                    return $r->pmsBookingRoomId;
                }
            }
        }
        return $selectedRoom->pmsBookingRoomId;
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
        
        
        
        if (!$this->needToCreateOrders()) {
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
        
        if ($_SESSION['currentSubMenu'] == "stayinformation") {
            unset($_SESSION['tmpselectedroom']);
        }
        
        $this->renderTabContent();
        die();
    }

    public function isTabActive($tabname) {
        if (isset($_SESSION['currentSubMenu']) && $_SESSION['currentSubMenu'] == $tabname) {
            return "active";
        }
        
        echo "";
    }
    private function isModalView() {
        return $this->getModalVariable("roomid");
    }
    
    private function setModalVariable() {
        if (isset($_POST['firestLoad'])) {
            $this->clearCache();
            unset($_SESSION['tmpselectedroom']);
        }
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
        unset($_SESSION['tmpselectedroom']);
    }
    
    public function setData($reload = false) {
        if(isset($this->pmsBooking) && isset($this->selectedRoom) && !$reload) {
            return;
        }
        $cachedRoomId = $this->getSelectedRoomId();
        $this->pmsBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $cachedRoomId);
         
        foreach ($this->pmsBooking->rooms as $room) {
            if ($room->pmsBookingRoomId == $cachedRoomId) {
                $this->updateRoom($room, true);
                $this->selectedRoom = $room;
            }
        }

        $this->bookingEngineBooking = $this->getApi()->getBookingEngine()->getBooking($this->getSelectedMultilevelDomainName(), $this->selectedRoom->bookingId);
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getPmsBooking() {
        if(isset($this->pmsBooking)) {
            return $this->pmsBooking;
        }
        $roomId = $this->getSelectedRoomId();
        $this->pmsBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomId);
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

    public function removeTmpRoom($roomId) {
        $this->selectedRoom = null;
        $this->pmsBooking = null;
        unset($_SESSION['tmpselectedroom'][$roomId]);
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function setTmpSelectedRoom($room) {
        unset($_SESSION['tmpselectedroom']);
        if(!isset($_SESSION['tmpselectedroom'])) {
            $_SESSION['tmpselectedroom'] = array();
        }
        $room->warningText = null;
        $room->tmpModified = true;
        $_SESSION['tmpselectedroom'][$room->pmsBookingRoomId] = json_encode($room);
        
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getTmpSelectedRoom($roomId) {
        if(isset($_SESSION['tmpselectedroom'][$roomId])) {
            return json_decode($_SESSION['tmpselectedroom'][$roomId]);
        } else {
            if(!isset($_SESSION['tmpselectedroom'])) {
                $_SESSION['tmpselectedroom'] = array();
            }
            $room = $this->getSelectedRoom();
            $_SESSION['tmpselectedroom'][$roomId] = json_encode($room);
            return $room;
        }
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

    public function saveGuestInformation() {
        $selectedRoom = $this->getPmsRoom();
        $pmsBooking = $this->getPmsBooking();
        $guestsToUse = $_POST['data'];
        if(!$selectedRoom) {
            echo "NO room";
        }
        if(!$pmsBooking) {
            echo "NO booking";
        }
        
        $guests = array();
        for($i = 1; $i <= 100; $i++) {
            $guest = $this->findGuest($_POST['data']['guestinfo_'.$i.'_guestid']);
            if(isset($_POST['data']['guestinfo_'.$i.'_name'])) {
                $guest->name = $_POST['data']['guestinfo_'.$i.'_name'];
                $guest->email = $_POST['data']['guestinfo_'.$i.'_email'];
                $guest->phone = $_POST['data']['guestinfo_'.$i.'_phone'];
                $guest->prefix = $_POST['data']['guestinfo_'.$i.'_prefix'];
                $guests[] = $guest;
            }
        }
        
        $this->getApi()->getPmsManager()->setGuestOnRoomWithoutModifyingAddons($this->getSelectedMultilevelDomainName(), $guests, $pmsBooking->id, $selectedRoom->pmsBookingRoomId);
        $this->forceUpdate();
        $pmsBooking = $this->getPmsBooking();
        foreach($pmsBooking->rooms as $r) {
            if($r->pmsBookingRoomId == $selectedRoom->pmsBookingRoomId) {
                foreach($r->addons as $addon) {
                    if($addon->dependsOnGuestCount) {
                        $addon->count = sizeof($guests);
                    }
                }
                $r->language = $_POST['data']['language'];
                $r->countryCode = $_POST['data']['countrycode'];
            }
        }
        $pmsBooking->countryCode = $_POST['data']['countrycode'];
        $pmsBooking->language = $_POST['data']['language'];
        
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $pmsBooking);
        $this->forceUpdate();
    }
    
    public function renderTabContent() {
        $this->printArea($_SESSION['currentSubMenu']);
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
            $this->types = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
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
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $room->pmsBookingRoomId) {
                $r->blocked = true;
            }
        }
        $itemId = $room->pmsBookingRoomId;
        $bookingId = $booking->id;
        $logText = "Blocking room";
        $this->getApi()->getPmsManager()->logEntry($this->getSelectedMultilevelDomainName(), $logText, $bookingId, $itemId);
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

    public function updateDayPrices() {
        $tmp = $this->getSelectedRoom();
        $room = $this->getTmpSelectedRoom($tmp->pmsBookingRoomId);
        $room->priceMatrix = $_POST['data'];
        $room->priceMatrixChanged = true;
        $this->setTmpSelectedRoom($room);
    }
    
    public function setSplitChange() {
        $room = $this->getTmpSelectedRoom($_POST['data']['roomid']);
        $room->doSplitChange = $_POST['data']['split'] == "true";
        $this->setTmpSelectedRoom($room);
    }
    
    public function updateRoom($room, $avoidChanges = false) {
    }

    public function printAddonsArea() {
        echo "<br>";
        echo "<span class='addonsArea'>";
        $this->includefile("addons");
        echo "</span>";
    }
    
    public function saveAddonItems() {
        $booking = $this->getPmsBooking();
        $roomId = $this->getSelectedRoomId();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId != $roomId) {
                continue;
            }
            foreach($room->addons as $addon) {
                $addon->isIncludedInRoomPrice = $_POST['data'][$addon->addonId]['includedinroomprice'];
                $addon->price = $_POST['data'][$addon->addonId]['price'];
                $addon->count = $_POST['data'][$addon->addonId]['count'];
                $addon->date = $this->convertToJavaDate(strtotime($_POST['data'][$addon->addonId]['date']));
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function removeSelectedAddons() {
        $room = $this->getSelectedRoom();
        foreach($room->addons as $addon) {
            if(in_array($addon->addonId, $_POST['data']['addonIds'])) {
                $this->getApi()->getPmsManager()->removeAddonFromRoomById($this->getSelectedMultilevelDomainName(), $addon->addonId, $room->pmsBookingRoomId);
            }
        }
    }

    public function getAddonsCost($withIncludedInRoomPrice) {
        $this->setData();
        $room = $this->getSelectedRoom();
        $totalAmount = 0.0;
        foreach($room->addons as $addon) {
            if($addon->isIncludedInRoomPrice && !$withIncludedInRoomPrice) {
                continue;
            }
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
        $filter->bookingId = $this->getPmsBooking()->id;
        $logs = $this->getApi()->getPmsManager()->getLogEntries($this->getSelectedMultilevelDomainName(), $filter);
        $this->roomLog = $logs;
        return $logs;
    }

    public function getRoom($roomId) {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomId);
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
        
        $this->getApi()->getCartManager()->clear();
        
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
        
        
        $this->setData(true);
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
        $filter->startInvoiceAt = $this->convertToJavaDate(strtotime($booking->startDate));
        $filter->pmsRoomIds = array();
        $filter->pmsRoomId  = $this->getSelectedRoom()->pmsBookingRoomId;
        unset($_SESSION['groupordercreationtype']);
        if(isset($_POST['data']['multipleadd'])) {
            $_SESSION['groupordercreationtype'] = $_POST['data']['paymenttypeselection'];
            $filter->pmsRoomIds = array();
            foreach($_POST['data']['roomid'] as $id => $val) {
                if($val == "true") {
                    $filter->pmsRoomIds[] = $id;
                }
            }
            $filter->pmsRoomId = "";
        }
        
        $this->getApi()->getCartManager()->clear();
        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $booking->id, $filter);    
    }

    public function printOrderList() {
        $orderIds = $this->filterOrderIds($this->pmsBooking->orderIds);
        $orderlist = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
        $orderlist->setOrderIds($orderIds);

        $orderlist->setPaymentLinkCallBack("app.PmsBookingRoomView.refresh");
        $ids = array();
        $ids[] = $this->selectedRoom->pmsBookingRoomId;
        $orderlist->setExternalReferenceIds($ids);
        if (count($orderIds)) {
            $orderlist->renderApplication(true, $this);
        } else {
            echo $this->__f("No orders created for this group");
        }
        
        $extraOrderIds = $this->getApi()->getPmsManager()->getExtraOrderIds($this->getSelectedMultilevelDomainName(), $this->pmsBooking->id);
        $extraOrderIds = $this->filterOrderIds($extraOrderIds);
        
        if ( $extraOrderIds && count($extraOrderIds)) {
            echo "<div style='border-top: solid 5px green; margin-top: 50px; padding-top: 50px;'>";
                echo "<h2> Payments merged from multiple bookings (Group invoicing)</h2>";
                $orderlist = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
                $orderlist->setOrderIds($extraOrderIds);
                $orderlist->setPaymentLinkCallBack("app.PmsBookingRoomView.refresh");
                $ids = array();
                $ids[] = $this->selectedRoom->pmsBookingRoomId;
                $orderlist->setExternalReferenceIds($ids);
                $orderlist->renderApplication(true, $this);    
            echo "</div>";
        }
    }

    public function doUpdatePriceMatrixWithPeriodePrice($room) {
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
        return $room;
    }

    public function getSelectedRoomId() {
        return $_SESSION['PmsBookingRoomView_current_pmsroom_id'];
    }

    public function forceUpdate() {
        $this->selectedRoom = null;
        $this->pmsBooking = null;
    }

    public function printArea($area) {
        if(!$this->isTabActive($area)) {
            return;
        }
        if($area == "addons") {
            $this->printAddonsArea();
        } else {
            $this->includefile($area);
        }
    }

    public function needToCreateOrders() {
        $this->refreshCartForRoom();
        $cart = $this->getApi()->getCartManager()->getCart();
        $itemCount = count($cart->items);
        if (!$itemCount) {
            return false;
        }
        
        return true;
    }

    public function loadUnpaidView() {
        $this->includefile("unpaidview");
    }

    
    /**
     * 
     * @return \core_bookingengine_data_BookingItemType
     */
    public function getSelectedTypeForRoom() {
        $savedRoom = $this->getSelectedRoom();
        $room = $this->getTmpSelectedRoom($savedRoom->pmsBookingRoomId);
        $types = $this->getTypes();
        
        $selectedType = null;

        foreach ($types as $type) {
            if ($room->bookingItemTypeId == $type->id) {
                $selectedType = $type;
            }
        }
        
        return $selectedType;
    }
    
    /**
     * 
     * @return \core_bookingengine_data_BookingItem
     */
    public function getSelectedItemForRoom() {
        $savedRoom = $this->getSelectedRoom();
        $room = $this->getTmpSelectedRoom($savedRoom->pmsBookingRoomId);
        $items = $this->getItems();
        
        $selectedType = null;

        foreach ($items as $type) {
            if ($room->bookingItemId == $type->id) {
                $selectedType = $type;
            }
        }
        
        return $selectedType;
    }
    
    public function checkinoutguest() {
        $roomid = $_POST['data']['roomId'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomid);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId != $roomid) {
                continue;
            }
            if($room->checkedin && !$room->checkedout) {
                $this->getApi()->getPmsManager()->checkOutRoom($this->getSelectedMultilevelDomainName(), $roomid);
            } else if($room->checkedout) {
                $this->getApi()->getPmsManager()->undoCheckOut($this->getSelectedMultilevelDomainName(), $roomid);
            } else {
                $this->getApi()->getPmsManager()->checkInRoom($this->getSelectedMultilevelDomainName(), $roomid);
            }
            $this->removeTmpRoom($room->pmsBookingRoomId);
        }
    }

    public function startingToday($room) {
        $now = date("dmy", time());
        $roomstart = date("dmy", strtotime($room->date->start));
        return $now == $roomstart;
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @return boolean
     */
    public function isValidSelection($room) {
        if($room->deleted || $room->addedToWaitingList) {
            return true;
        }
        $start = $room->date->start;
        $end = $room->date->end;
        $available = (array)$this->getApi()->getBookingEngine()->getAllAvailbleItemsWithBookingConsidered($this->getSelectedMultilevelDomainName(),
                $start, 
                $end, 
                $room->bookingId);
                
        foreach($available as $av) {
            /* @var $av \core_bookingengine_data_BookingItem */
            if($av->bookingItemTypeId == $room->bookingItemTypeId) {
                return true;
            }
        }
        return false;
    }

    public function setLogEntries($logs) {
        $this->logEntries = $logs;
    }

    public function getLogEntries() {
        return $this->logEntries;
    }

    public function showItemView() {
        $this->includefile("availablerooms");
        die();
    }

    public function removeGroupList() {
        $_SESSION['pmsroomviewlistgrouproom'] = false;
    }
    
    public function getDefaultPrefix() {
        if($this->defaultPrefix) {
            return $this->defaultPrefix;
        }
        
        $this->defaultPrefix = $this->getFactory()->getStoreConfiguration()->defaultPrefix;
        return $this->defaultPrefix;
    }

    public function toggleListGroupRooms() {
        if(isset($_GET['page']) && $_GET['page'] == "groupbooking") {
            return true;
        }
        
        if($_SESSION['pmsroomviewlistgrouproom']) {
            $_SESSION['pmsroomviewlistgrouproom'] = false;
        } else {
            $_SESSION['pmsroomviewlistgrouproom'] = $_POST['data']['roomid'];
        }
    }
    
    public function listGroupRooms() {
        if(isset($_SESSION['pmsroomviewlistgrouproom'])) {
            return $_SESSION['pmsroomviewlistgrouproom'];
        }
        return false;
    }

    public function removeGroupView() {
        if(isset($_SESSION['forceroomlistview']) && $_SESSION['forceroomlistview']) {
            $_SESSION['pmsroomviewlistgrouproom'] = $_POST['data']['roomId'];
            unset($_SESSION['forceroomlistview']);
        }
        
        if($_SESSION['pmsroomviewlistgrouproom'] == $_POST['data']['roomId']) {
            return;
        }
        $_SESSION['pmsroomviewlistgrouproom'] = false;
    }

    public function grantAccessToGuest() {
        $this->setData();
        $pmsBookingRoomId = $this->getSelectedRoomId();
        $pmsBookingId = $this->getPmsBooking()->id;
        $this->getApi()->getPmsManager()->generatePgaAccess($this->getSelectedMultilevelDomainName(), $pmsBookingId, $pmsBookingRoomId);
        $this->setData(true);
        $this->includefile("pga");
        die();
    }
    
    public function removePgaAccess() {
        $this->setData();
        $pmsBookingRoomId = $this->getSelectedRoomId();
        $pmsBookingId = $this->getPmsBooking()->id;
        $this->getApi()->getPmsManager()->removePgaAccess($this->getSelectedMultilevelDomainName(), $pmsBookingId, $pmsBookingRoomId);
        $this->setData(true);
        $this->includefile("pga");
        die();
    }

    public function addAnotherRoom() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " " . $config->defaultStart));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " " . $config->defaultEnd));
        $bookingId = $_POST['data']['bookingid'];
        $type = $_POST['data']['type'];
        if(!$type) {
            return;
        }
        $gs_multilevel_name = $this->getSelectedMultilevelDomainName();
        $count = $_POST['data']['count'];
        for($i = 0; $i < $count; $i++) {
            $this->getApi()->getPmsManager()->addBookingItemType($gs_multilevel_name, $bookingId, $type, $start, $end, null);
        }
    }
    
    public function loadCategoryAvailability() {
        $this->includefile("roomsavailable");
    }
    
    public function printAvailableRoomsFromCategory($start, $end) {
        $start = $this->convertToJavaDate(strtotime($start));
        $end = $this->convertToJavaDate(strtotime($end));
        $categories = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        echo "<option value=''>Choose a category</option>";
        foreach($categories as $cat) {
            $number = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedMultilevelDomainName(), $cat->id, $start, $end);
            echo "<option value='" . $cat->id . "'>" . $cat->name . " ($number available)</option>";
        }
    }
    
    public function saveUserDescription() {
        $booking = $this->getPmsBooking();
        $user = $this->getUserForBooking();
        $user->description = $_POST['data']['comment'];
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function saveOrderNote() {
        $booking = $this->getPmsBooking();
        $booking->invoiceNote = $_POST['data']['comment'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function reloadAvailableRooms() {
        $start = $_POST['data']['start'];
        $end = $_POST['data']['end'];
        $this->printAvailableRoomsFromCategory($start, $end);
    }

    public function isStartingToday($room) {
        if(date("dmy", time()) == date("dmy", strtotime($room->date->start))) {
            return true;
        }
        if(strtotime($room->date->start) < time()) {
            return true;
        }
        return false;
    }

    public function getPrinters() {
        $devices = (array)$this->getApi()->getGdsManager()->getDevices();
        $ret = array();
        
        foreach ($devices as $device) {
            if ($device->type == "cashap") {
                $ret[] = $device;
            }
        }
        
        return $ret;
    }
    
    public function printCode() {
        $room = $this->getSelectedRoom();
        $this->getApi()->getPmsManager()->printCode($this->getSelectedMultilevelDomainName(), $_POST['data']['cashpointid'], $room->pmsBookingRoomId);
    }

    public function printGroupCodes() {
        $booking = $this->getPmsBooking();
        foreach ($booking->rooms as $room) {
            $this->getApi()->getPmsManager()->printCode($this->getSelectedMultilevelDomainName(), $_POST['data']['cashpointid'], $room->pmsBookingRoomId);
        }
    }

    public function shouldShowAccessLog() {
        return $this->getApi()->getGetShopLockSystemManager()->canShowAccessLog();
    }

    public function getLanguage($room, $pmsBooking) {
        $lang = $room->language;
        if(!$lang) { $lang = $pmsBooking->language; }
        if($lang == "nb_NO") { $lang = "no"; }
        if($lang == "en_en") { $lang = "en"; }
        return $lang;
    }

    public function getCountryCode($pmsSelectedRoom, $pmsBooking) {
        if(!$pmsBooking->countryCode) {
            $pmsBooking->countryCode = $this->getFactory()->getMainCountry();
        }

        if($pmsSelectedRoom->countryCode) {
            return $pmsSelectedRoom->countryCode;
        }
        return $pmsBooking->countryCode;
    }

    public function toggleCreditHistory() {
        $showCredittedHistory = isset($_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_showcredittedhistory']) && $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_showcredittedhistory'];
        $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_showcredittedhistory'] = !$showCredittedHistory;
    }

    public function filterOrderIds($orderIds) {
        $showCredittedHistory = isset($_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_showcredittedhistory']) && $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_showcredittedhistory'];
        
        if ($orderIds && count($orderIds) && !$showCredittedHistory) {
            return $this->getApi()->getOrderManager()->filterOrdersIsCredittedAndPaidFor($orderIds);
        }
        
        return $orderIds;
    }

    public function getLastTerminalMessage($isVerifone = true) {
       if($isVerifone) {
            $messages = $this->getApi()->getVerifoneManager()->getTerminalMessages();
            $this->getApi()->getVerifoneManager()->clearMessages();
        } else {
            $messages = $this->getApi()->getOrderManager()->getTerminalMessages();
            $this->getApi()->getOrderManager()->clearMessages();
        }        
        if ($messages && count($messages)) {
            $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_last_terminal_message'] = end($messages);
        }
        
        $this->updateState();
        
        return $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_last_terminal_message'];
    }
    
    public function updateState() {
        if ($_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_last_terminal_message'] == "completed") {
            $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_complete_payment_state'] = "completed";
        }
        
        if ($_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_last_terminal_message'] == "payment failed") {
            $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_complete_payment_state'] = "failed";
        }
    }
    
    public function getPaymentProcessMessage() {
        $this->includefile("verifonepaymentprocess");
        die();
    }
    
    public function getPaymentOrderProcessMessage() {
        $this->includefile("integratedpaymentprocess");
        die();
    }
    
    public function cancelVerifonePayment() {
        $deviceId = $this->getCurrentTerminalId();
        $this->getApi()->getVerifoneManager()->cancelPaymentProcess($deviceId);
        die();
    }
    
    public function cancelPaymentPayment() {
        $deviceId = $_POST['data']['tokenid'];
        $this->getApi()->getOrderManager()->cancelPaymentProcess($deviceId);
        die();
    }
    
    public function getCurrentTerminalId() {
        $devices = $this->getActiveVerifoneDevices();
        $deviceId = str_replace("ipaddr", "", $devices[0]->name);
        return $deviceId;
    }
    
    public function getActiveVerifoneDevices() {
        $verifoneApp = $this->getApi()->getStoreApplicationPool()->getApplication('6dfcf735-238f-44e1-9086-b2d9bb4fdff2');

        $activeDevices = array();
        foreach ($verifoneApp->settings as $key => $setting) {
            if (strstr($key, "ipaddr") && $setting->value) {
                $activeDevices[] = $setting;
            }
        }
        
        return $activeDevices;
    }
    
    public function handleDebugActions() {
        if ($_POST['data']['action'] == "success") {
            $this->getApi()->getPmsBookingProcess()->addTestMessagesToQueue($this->getSelectedMultilevelDomainName(), "completed");
        }
        if ($_POST['data']['action'] == "failed") {
            $this->getApi()->getPmsBookingProcess()->addTestMessagesToQueue($this->getSelectedMultilevelDomainName(), "payment failed");
        }
        if ($_POST['data']['action'] == "other") {
            $this->getApi()->getPmsBookingProcess()->addTestMessagesToQueue($this->getSelectedMultilevelDomainName(), "Please insert credit card");
        }
    }

    public function restartVerifonePayment() {
        $deviceId = $this->getCurrentTerminalId();
        $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_complete_payment_state'] = "in_progress";
        $this->getApi()->getVerifoneManager()->chargeOrder($_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_current_verifone_order_id'], $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_current_verifone_id'], false);
    }

    public function restartPayment() {
        $deviceId = $this->getCurrentTerminalId();
        $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_complete_payment_state'] = "in_progress";
        $this->getApi()->getOrderManager()->chargeOrder($_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_current_verifone_order_id'], $_SESSION['ns_f8cc5247_85bf_4504_b4f3_b39937bd9955_current_verifone_id'], false);
    }
    
    public function showMarkAsPaidWindow() {
        $this->includefile("markaspaidwindow");
    }
    
    public function markAsPaid() {
        $amount = $_POST['data']['amount'];
        if(!$amount) {
            $amount = 0.0;
        } else {
            $amount = str_replace(",",".", $amount);
        }
        $time = $_POST['data']['date'] . " " . $_POST['data']['time'];
        $this->getApi()->getOrderManager()->markAsPaid($_POST['data']['orderid'], $this->convertToJavaDate(strtotime($time)), $amount);
    }

    public function getPinnedComment() {
        $user = $this->getUserForBooking();
        return $user->description;
    }

    /**
     * 
     * @param type $productId
     * @return \core_productmanager_data_Product
     */
    public function getProduct($productId) {
        if (isset($this->cachedProducts[$productId])) {
            return $this->cachedProducts[$productId];
        }
        
        $this->cachedProducts[$productId] = $this->getApi()->getProductManager()->getProduct($productId);
        
        return $this->cachedProducts[$productId];
    }

    public function getPaymentType($order) {
        $orderList = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
        return $orderList->formatPaymentType($order);
    }

    public function shouldShowPager($distinctDates) {
        if (count($this->getDistinctMonthsAndYears($distinctDates)) < 2) {
            return false;
        }
        
        return count($distinctDates) > 30;
    }

    public function getDistinctMonthsAndYears($distinctDates) {
        $distinctList = array();
        foreach ($distinctDates as $date) {
            $arr = explode("-", $date);
            $key = $arr[1]."-".$arr[2];
            if (!in_array($key, $distinctList)) {
                $distinctList[] = $key;
            }
        }
        
        return $distinctList;
    }

    public function translateMonthAndYear($distinct) {
        $arr = explode("-", $distinct);
        $dateObj   = \DateTime::createFromFormat('!m', $arr[0]);
        $monthName = $dateObj->format('F');
        
        return $monthName." ".$arr[1];
    }

    public function removeConferenceFromGuest() {
        $booking = $this->getPmsBooking();
        $guestInvolved = null;
        foreach($booking->rooms as $room) {
            foreach($room->guests as $guest) {
                if($guest->guestId == $_POST['data']['guestid']) {
                    $array = (array)$guest->pmsConferenceEventIds;
                    $del_val = $_POST['data']['eventid'];
                    if (($key = array_search($del_val, $array)) !== false) {
                        unset($array[$key]);
                    }
                    $guest->pmsConferenceEventIds = $array;
                    
                    $guestInvolved = $guest;
                }
            }
        }
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, true);
        $this->printConnectedConferenceEventToGuest($guestInvolved);
    }
    
    public function createGuest() {
        $room = $this->getSelectedRoom();
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId != $room->pmsBookingRoomId) {
                continue;
            }
            $guest = new \core_pmsmanager_PmsGuests();
            $room->guests[] = $guest;
            $room->numberOfGuests = sizeof($room->guests);
            $guest->guestId = uniqid();
            echo $guest->guestId;
            break;
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function canTestNewRoutine() {
        return true;
    }

    public function printConnectedConferenceEventToGuest($guest) {
        echo "<div style='padding: 10px; font-size:12px;'>";
        $guestId ="";
        if($guest) {
            $guestId = $guest->guestId;
        }
        if($guest && @$guest->pmsConferenceEventIds) {
            foreach($guest->pmsConferenceEventIds as $eventId) {
                $event = $this->getApi()->getPmsConferenceManager()->getConferenceEvent($eventId);
                $item = $this->getApi()->getPmsConferenceManager()->getItem($event->pmsConferenceItemId);
                $conference = $this->getApi()->getPmsConferenceManager()->getConference($event->pmsConferenceId);
                echo "<div>";
                echo "<i class='fa fa-arrow-right'></i> Connected to conference " . $conference->meetingTitle . " - " . $item->name . " - " . date("d.m.Y", strtotime($event->from)) . " - " . date("d.m.Y", strtotime($event->to));
                echo " <span class='removeConferenceFromGuest bookinghighlightcolor' guestid='".$guest->guestId."' eventid='".$event->id."' style='cursor:pointer;'>remove</span>,";
                echo " <span class='connectGuestToConference bookinghighlightcolor' style='cursor:pointer'>add another</span>";
                echo "</div>";
            }
        } else {
            echo "Not connected to a conference, <span class='connectGuestToConference bookinghighlightcolor' style='cursor:pointer'>connect to a conference</span>.";
        }
        echo "</div>";
    }

    public function findGuest($guestId) {
        $room = $this->getPmsRoom();
        foreach($room->guests as $guest) {
            if($guest->guestId == $guestId) {
                return $guest;
            }
        }
        return new \core_pmsmanager_PmsGuests();
    }
    
    
    public function searchForEvent() {
        $filter = new \core_pmsmanager_PmsConferenceEventFilter();
        $filter->keyword = $_POST['data']['keyword'];
        $events = (array)$this->getApi()->getPmsConferenceManager()->getConferenceEventsByFilter($filter);
        $this->printEvents($events);
    }
    
    public function printEvents($events, $currentPmsEventId = "") {
        $items = $this->getApi()->getPmsConferenceManager()->getAllItem("-1");
        $items = $this->indexList($items);
        if(sizeof($events) == 0) {
            echo "<div style='padding: 5px;'>";
            echo "* No events found, please search for one in the input field above.";
            echo "</div>";
        }
        echo "<table width='100%'>";
        foreach($events as $event) {
            $eventAdded = "";
            if($event->id == $currentPmsEventId) {
                $eventAdded = "eventaddedtoguest";
            }
            $confernce = $this->getApi()->getPmsConferenceManager()->getConference($event->pmsConferenceId);
            echo "<tr class='$eventAdded'>";
            echo "<td>" . $confernce->meetingTitle . "</td>";
            echo "<td>" . $items[$event->pmsConferenceItemId]->name . "</td>";
            echo "<td>" . date("d.m.Y H:i", strtotime($event->from)) . "</td>";
            echo "<td>" . date("d.m.Y H:i", strtotime($event->to)) . "</td>";
            if(!$eventAdded) {
                echo "<td><span style='cursor:pointer;' class='attachguesttoevent' eventid='".$event->id."'>Select</span></td>";
            } else {
                echo "<td><span style='cursor:pointer;' class='attachguesttoevent' eventid=''>Remove</span></td>";
            }
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function loadConferenceEvents() {
        $this->includefile("addguesttoconference");
    }
    
    public function attachGuestToEvent() {
        $booking = $this->getPmsBooking();
        $guestInvolved = null;
        foreach($booking->rooms as $room) {
            foreach($room->guests as $guest) {
                if($guest->guestId == $_POST['data']['guestid']) {
                    $guest->pmsConferenceEventIds[] = $_POST['data']['eventid'];
                    $guestInvolved = $guest;
                }
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->printConnectedConferenceEventToGuest($guestInvolved);
        $this->clearCache();
    }
}
?>

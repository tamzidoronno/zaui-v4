<?php
namespace ns_3e2bc00a_4d7c_44f4_a1ea_4b1b953d8c01;

class PmsBookingGroupRoomView extends \WebshopApplication implements \Application {
   
    private $pmsBooking;
    private $pmsBookingRoom;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingGroupRoomView";
    }

    public function splitStay() {
        $roomId = $_POST['data']['roomid'];
        $splitTime = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['time']));
        $this->getApi()->getPmsManager()->splitStay($this->getSelectedMultilevelDomainName(), $roomId, $splitTime);
        $this->clearCache();
    }
    
    public function checkinoutguest() {
        $roomid = $this->getPmsBookingRoom()->pmsBookingRoomId;
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
        }
        $this->clearCache();
    }
    
    public function updateStayTime() {
        $roomId = $this->getPmsBookingRoom()->pmsBookingRoomId;
        $bookingId = $this->getPmsBooking()->id;
        $start = $this->convertToJavaDate(strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']));
        $this->getApi()->getPmsManager()->changeDates($this->getSelectedMultilevelDomainName(), $roomId, $bookingId, $start, $end);
        $this->clearCache();
    }
    
    public function render() {
        echo "<div class='room_view_outer' usenewpayment='true'>";
        $this->includefile("main");
        echo "</div>";
    }
    
    public function showItemView() {
        $this->includefile("availablerooms");
        die();
    }
    
    public function toggleDisabledGuest() {
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $r) {
            foreach($r->guests as $g) {
                if($g->guestId == $_POST['data']['guestid']) {
                    $g->isDisabled = !$g->isDisabled;
                }
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    
    
    public function deleteRoom() {
        $room = $this->getPmsBookingRoom();
        $booking = $this->getPmsBooking();
        $error = $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedMultilevelDomainName(),$booking->id, $room->pmsBookingRoomId);
        if($error) {
            $this->errors[] = $error;
        }
        $this->clearCache();
    }
    
    public function removeFromWaitingList() {
        $roomId = $_POST['data']['id'];
        $this->getApi()->getPmsManager()->removeFromWaitingList($this->getSelectedMultilevelDomainName(), $roomId);
        $this->clearCache();
    }
    
    
    public function togglePriorityRoom() {
        $selectedroom = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->togglePrioritizedRoom($this->getSelectedMultilevelDomainName(), $selectedroom->pmsBookingRoomId);
        $this->clearCache();
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
        $this->clearCache();
    }
    public function cancelRoom() {
        $room = $this->getPmsBookingRoom();
        $booking = $this->getPmsBooking();
        $error = $this->getApi()->getPmsManager()->cancelRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId);
        if($error) {
            $this->errors[] = $error;
        }
        $this->clearCache();
    }
    public function moveToWaitingList() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->addToWaitingList($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId);
        $this->clearCache();
    }
    
    public function updatePriceMatrixWithPeriodePrices() {
        $this->doUpdatePriceMatrixWithPeriodePrice($this->getPmsBookingRoom());
        $this->clearCache();
    }

    public function updatePrices() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->updatePriceMatrixOnRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId, $_POST['data']['prices']);
        $this->clearCache();
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
        $this->getApi()->getPmsManager()->updatePriceMatrixOnRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId, $room->priceMatrix);
        $this->clearCache();
    }

    
    public function reinstateStay() {
        $room = $this->getPmsBookingRoom();
        $minutes = $_POST['data']['minutes'];
        $this->getApi()->getPmsManager()->reinstateStay($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId, $minutes);
        $this->clearCache();
    }
    
    public function saveUserDescription() {
        $booking = $this->getPmsBooking();
        $user = $this->getUserForBooking();
        $user->description = $_POST['data']['comment'];
        $this->getApi()->getUserManager()->saveUser($user);
        $this->clearCache();
    }
    
    public function saveUser() {
        $this->getPmsBooking();
        $this->clearCache();
        exit(0);
    }
    
    public function saveOrderNote() { 
       $booking = $this->getPmsBooking();
        $booking->invoiceNote = $_POST['data']['comment'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function deleteInvoiceNote() {
        $booking = $this->getPmsBooking();
        $booking->invoiceNote = "";
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function deleteUserDescription() {
        $booking = $this->getPmsBooking();
        $user = $this->getUserForBooking();
        $user->description = "";
        $this->getApi()->getUserManager()->saveUser($user);
        $this->clearCache();
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

    
    public function addComment() {
        $comment = $_POST['data']['comment'];
        $booking = $this->getPmsBooking();
        if($_POST['data']['submit'] == "room") {
            $selectedRoom = $this->getPmsBookingRoom();
            $this->getApi()->getPmsManager()->addCommentToRoom($this->getSelectedMultilevelDomainName(), $selectedRoom->pmsBookingRoomId, $comment);
        } else {
            $this->getApi()->getPmsManager()->addComment($this->getSelectedMultilevelDomainName(), $booking->id, $comment);
        }
        $this->clearCache();
    }
    

    public function togglerefundable() {
        $booking = $this->getPmsBooking();
        $room = $this->getPmsBookingRoom();
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $room->pmsBookingRoomId) {
                $r->nonrefundable = !$r->nonrefundable;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
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
    
    
    public function isEndedToday($room) {
        if(date("dmy", time()) == date("dmy", strtotime($room->date->end)) && time() > strtotime($room->date->end)) {
            return true;
        }
        return false;
    }

    public function deleteComment() {
        $booking = $this->getPmsBooking();
        foreach($booking->comments as $time => $val) {
            if($val->commentId == $_POST['data']['commentid']) {
                $val->deleted = !$val->deleted;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
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
    
    
    public function findGuest($guestId) {
        $room = $this->getPmsBookingRoom();
        foreach($room->guests as $guest) {
            if($guest->guestId == $guestId) {
                return $guest;
            }
        }
        return new \core_pmsmanager_PmsGuests();
    }
    
    public function getBookerInformation() {
        $booking = $this->getPmsBooking();
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        
        $data = array();
        $data['name'] = $user->fullName;
        $data['email'] = $user->emailAddress;
        $data['prefix'] = $user->prefix;
        $data['phone'] = $user->cellPhone;
        
        echo json_encode($data);
    }
    
    public function loadConferenceEvents() {
        $this->includefile("addguesttoconference");
    }
     
    public function updateComment() {
        $id = $_POST['data']['id'];
        $text = $_POST['data']['text'];
//        $text = str_replace("<br>", "", $text);
        $this->getApi()->getPmsManager()->updateCommentOnBooking($this->getSelectedMultilevelDomainName(), $this->getPmsBooking()->id, $id, $text);
        $this->clearCache();
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
    
    
    public function markRoomCleanedWithoutLogging() {
        $itemId = $this->getPmsBookingRoom()->bookingItemId;
        $this->getApi()->getPmsManager()->markRoomAsCleanedWithoutLogging($this->getSelectedMultilevelDomainName(), $itemId);
        $this->clearCache();
    }
    
    public function markRoomCleanedWithLogging() {
        $itemId = $this->getPmsBookingRoom()->bookingItemId;
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
    

    
    public function saveGuestInformation() {
        $selectedRoom = $this->getPmsBookingRoom();
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

        $this->pmsBooking = null;
        $this->pmsBookingRoom = null;
        
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
        $this->clearCache();
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
    public function setLogEntries($logs) {
        $this->logEntries = $logs;
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

    public function shouldShowPager($distinctDates) {
        if (count($this->getDistinctMonthsAndYears($distinctDates)) < 2) {
            return false;
        }
        
        return count($distinctDates) > 30;
    }
    
    
    public function getPaymentType($order) {
        $orderList = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
        return $orderList->formatPaymentType($order);
    }
    
    public function filterOrderIds($orderIds) {
        $showCredittedHistory = false;
        
        if ($orderIds && count($orderIds) && !$showCredittedHistory) {
            return $this->getApi()->getOrderManager()->filterOrdersIsCredittedAndPaidFor($orderIds);
        }
        
        return $orderIds;
    }

    
    public function getLogEntries() {
        return $this->logEntries;
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

    public function setRoomId($id) {
        $this->pmsBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $id);
        
        foreach($this->pmsBooking->rooms as $room) {
            if($room->pmsBookingRoomId == $id) {
                $this->pmsBookingRoom = $room;
            }
        }
    }
      public function getDefaultPrefix() {
        if($this->defaultPrefix) {
            return $this->defaultPrefix;
        }
        
        $this->defaultPrefix = $this->getFactory()->getStoreConfiguration()->defaultPrefix;
        return $this->defaultPrefix;
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
    

    public function retrySendingCode() {
        $this->getApi()->getPmsManager()->processor($this->getSelectedMultilevelDomainName());
    }
        

    public function ungrantPayment() {
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $this->getSelectedRoomId()) {
                $room->forceAccess = false;
                $selectedRoom = $room;
            }
        }
        
        if ($selectedRoom != null && $selectedRoom->createOrdersOnZReport) {
            $this->getApi()->getPmsManager()->toggleAutoCreateOrders($this->getSelectedMultilevelDomainName(), $booking->id, $selectedRoom->pmsBookingRoomId);
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function markRoomDirty() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->markRoomDirty($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
        $this->clearCache();
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getPmsBooking() {
        if(!$this->pmsBooking) {
            $this->setRoomId($_POST['data']['roomid']);
        }
        return $this->pmsBooking;
    }
    
    
    public function unBlockRoom() {
        $booking = $this->getPmsBooking();
        $room = $this->getPmsBookingRoom();
        
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $room->pmsBookingRoomId) {
                $room->blocked = false;
            }
        }
        
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        
        if ($room != null && !$room->createOrdersOnZReport) {
            $this->getApi()->getPmsManager()->toggleAutoCreateOrders($this->getSelectedMultilevelDomainName(), $booking->id, $room->pmsBookingRoomId);
        }
        $this->clearCache();
    }
    
    
    public function loadConfirmationPreview() {
        $msg = $this->getApi()->getPmsNotificationManager()->getMessage($this->getSelectedMultilevelDomainName(), $_POST['data']['msgid']);
        $msg->content = $_POST['data']['content'];
        $msg->title = $_POST['data']['title'];
        $bookingid = $this->getPmsBooking()->id;
        $roomid = $this->getPmsBookingRoom()->pmsBookingRoomId;
        $formatted = $this->getApi()->getPmsNotificationManager()->doFormationOnMessage($this->getSelectedMultilevelDomainName(), $msg, $bookingid, $roomid);
        echo "<div style='text-align:center; font-size: 30px;border-bottom: solid 1px; padding-bottom: 10px; margin-bottom: 10px;'><b>" . $formatted->title . "</b></div>";
        echo nl2br($formatted->content);
    }

    public function sendConfirmation() {
        $msg = $this->getApi()->getPmsNotificationManager()->getMessage($this->getSelectedMultilevelDomainName(), $_POST['data']['msgid']);
        $msg->content = $_POST['data']['content'];
        $msg->title = $_POST['data']['title'];
        $bookingid = $this->getPmsBooking()->id;
        $roomid = $this->getPmsBookingRoom()->pmsBookingRoomId;
        $formatted = $this->getApi()->getPmsNotificationManager()->doFormationOnMessage($this->getSelectedMultilevelDomainName(), $msg, $bookingid, $roomid);
        $email = $_POST['data']['email'];
        $this->getApi()->getPmsNotificationManager()->sendEmail($this->getSelectedMultilevelDomainName(), $formatted, $email, $bookingid, null);
        $this->clearCache();
    }

    public function sendMessage() {
        $type = $_POST['data']['submit'];
        $message = $_POST['data']['message'];
        $room = $this->getPmsBookingRoom();
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
        $this->clearCache();
    }
        

    public function printCode() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->printCode($this->getSelectedMultilevelDomainName(), $_POST['data']['cashpointid'], $room->pmsBookingRoomId);
    }
    
    public function changeDiscountCode() {
        $code = $_POST['data']['code'];
        $booking = $this->getPmsBooking();
        $booking->couponCode = $code;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        if($_POST['data']['updateprices'] == "true") {
            foreach($booking->rooms as $room) {
                $this->getApi()->getCartManager()->forceNextCouponValid();
                $this->getApi()->getPmsManager()->resetPriceForRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId);
            }
        }
        
        $this->clearCache();
    }
    
    public function changeChannel() {
        $booking = $this->getPmsBooking();
        $booking->channel = $_POST['data']['channel'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
 
    public function segmentDiscountCode() {
        $segment = $_POST['data']['segment'];
        $booking = $this->getPmsBooking();
        $booking->segmentId = $segment;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    
    public function forceAccessRegardlessOfPayment() {
        $booking = $this->getPmsBooking();
        $selectedRoom = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->toggleAutoCreateOrders($this->getSelectedMultilevelDomainName(), $booking->id, $selectedRoom->pmsBookingRoomId);
        $this->clearCache();
    }
    public function resendCodeForRoom() {
        $room = $this->getPmsBookingRoom();
        $roomId = $room->pmsBookingRoomId;
        $prefix = $_POST['data']['prefix'];
        $phoneNumber = $_POST['data']['phone'];
        $this->getApi()->getPmsManager()->sendCode($this->getSelectedMultilevelDomainName(), $prefix, $phoneNumber, $roomId);
        $this->clearCache();
    }
    
    public function renewCodeForRoom() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->generateNewCodeForRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId);
        $this->clearCache();
    }
    
    public function blockAccessToRoom() {
        $booking = $this->getPmsBooking();
        $room = $this->getPmsBookingRoom();
        
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
        
        if ($room != null && $room->createOrdersOnZReport) {
            $this->getApi()->getPmsManager()->toggleAutoCreateOrders($this->getSelectedMultilevelDomainName(), $booking->id, $room->pmsBookingRoomId);
        }
        $this->clearCache();
    }
    
    public function markRoomCleaned() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
        $this->clearCache();
    }
    
    public function markRoomCleanedwithoutlog() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->markRoomAsCleanedWithoutLogging($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
        $this->clearCache();
    }
    public function togglePayAfterStayForGroup() {
        $booking = $this->getPmsBooking();
        $booking->createOrderAfterStay = !$booking->createOrderAfterStay;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
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
        $this->clearCache();
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
                $addon->name = "";
                if(isset($_POST['data'][$addon->addonId]['name'])) {
                    $addon->name = $_POST['data'][$addon->addonId]['name'];
                }
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getPmsBookingRoom() {
        if(!$this->pmsBooking) {
            $this->setRoomId($_POST['data']['roomid']);
        }
        return $this->pmsBookingRoom;
    }

    public function getUserForBooking() {
        return $this->getApi()->getUserManager()->getUserById($this->getPmsBooking()->userId);
    }
    
    public function getBookingEngineBooking() {
        return $this->getApi()->getBookingEngine()->getBooking($this->getSelectedMultilevelDomainName(), $this->pmsBookingRoom->bookingId);
    }

    public function clearCache() {
        unset($_SESSION['cachedbooking'][$this->pmsBooking->id]);
        $this->pmsBooking = null;
        $this->pmsBookingRoom = null;
    }
    
    public function changeUser($user) {
        $booking = $this->getPmsBooking();
        $booking->userId = $user->id;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
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
        $this->clearCache();
    }
    
    public function removeSelectedAddons() {
        $room = $this->getPmsBookingRoom();
        foreach($room->addons as $addon) {
            if(in_array($addon->addonId, $_POST['data']['addonIds'])) {
                $this->getApi()->getPmsManager()->removeAddonFromRoomById($this->getSelectedMultilevelDomainName(), $addon->addonId, $room->pmsBookingRoomId);
            }
        }
        $this->clearCache();
    }

    public function printAddAddonsArea() {
        $addAddons = new \ns_b72ec093_caa2_4bd8_9f32_e826e335894e\PmsAddAddonsList();
        $addAddons->renderApplication(true);
    }
    
    
    
    public function chooseBookingItem() {
        $bookingId = $this->getPmsBooking()->id;
        $roomId = $this->getPmsBookingRoom()->pmsBookingRoomId;
        $itemId = $_POST['data']['item'];
        $this->getApi()->getPmsManager()->setBookingItem($this->getSelectedMultilevelDomainName(), $roomId, $bookingId, $itemId, false);
        $this->clearCache();
    }

    public function getTypes() {
        return $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedMultilevelDomainName());
    }

    public function getItems() {
        return $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedMultilevelDomainName());
    }

    public function useNew() {
        return false;
    }

    public function getSelectedRoomId() {
        return $this->getPmsBookingRoom()->pmsBookingRoomId;
    }
    
    
    public function cancelautodeletion() {
        $booking = $this->getPmsBooking();
        $booking->avoidAutoDelete = true;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    

}
?>

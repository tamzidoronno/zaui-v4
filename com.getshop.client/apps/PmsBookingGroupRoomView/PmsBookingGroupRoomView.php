<?php
namespace ns_3e2bc00a_4d7c_44f4_a1ea_4b1b953d8c01;

class PmsBookingGroupRoomView extends \WebshopApplication implements \Application {
   
    private $pmsBooking;
    private $pmsBookingRoom;
    private $conference;
    private $events;
    private $eventItems;
    private $eventEntries;
    private $config;
    private $loadingFromReportView;
    
    public function getDescription() {
        
    }
    
    public function createNewUser(){
        $name = $_POST['data']['name'];
        $bookingId = $this->getPmsBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, "");
        $this->clearCache();
        return $this->getUserForBooking();
    }
   
    public function addExistingRoomToBooking() {
        $curbooking = $this->getPmsBooking();
        $roomId = $_POST['data']['tomoveroomid'];
        $moved = $this->getApi()->getPmsManager()->moveRoomToBooking($this->getSelectedMultilevelDomainName(), $roomId, $curbooking->id);
        if($moved) {
            echo "1";
        } else {
            echo "0";
        }
    }
    
    public function deleteConferenceAndRoom() {
        $booking = $this->getPmsBooking();
        $this->deleteConference();
        $this->getApi()->getPmsManager()->deleteBooking($this->getSelectedMultilevelDomainName(), $booking->id);
    }
    
    public function searchbooking() {
        $keyword = $_POST['data']['keyword'];
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->searchWord = $keyword;
        $rooms = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedMultilevelDomainName(), $filter);
        foreach($rooms as $room) {
            echo "<div class='row addfromotherroomrow'>";
            echo "<span class='col owner ellipsis'>" . $room->owner . "</span>";
            echo "<span class='col start ellipsis'>" . date("d.m.Y H:i", $room->start/1000) . "</span>";
            echo "<span class='col end ellipsis'>" . date("d.m.Y H:i", $room->end/1000) . "</span>";
            $guestNames = array();
            foreach($room->guest as $g) { $guestNames[] = $g->name; }
            echo "<span class='col guestnames ellipsis'>" . join(",", $guestNames) . "</span>";
            echo "<span class='col roomtype ellipsis'>" . $room->roomType . "</span>";
            echo "<span class='col room ellipsis'>" . $room->room . "</span>";
            echo "<span class='col choose importroom bookinghighlightcolor' roomid='".$room->pmsRoomId."'>Import</span>";
            echo "</div>";
        }
    }
    
    public function checkIfCanAdd() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $bookings = array();
        for($i = 0;$i < $_POST['data']['count'];$i++) {
            $startTime = $config->defaultStart;
            $endTime = $config->defaultEnd;

            $start = $this->convertToJavaDate(strtotime($_POST['data']['start']. " " . $startTime));
            $end = $this->convertToJavaDate(strtotime($_POST['data']['end']. " " . $endTime));            
            
            $booking = new \core_bookingengine_data_Booking();
            $booking->startDate = $start;
            $booking->endDate = $end;
            $booking->bookingItemTypeId = $_POST['data']['type'];
            $bookings[] = $booking;
        }
        $canadd = $this->getApi()->getBookingEngine()->canAddBookings($this->getSelectedMultilevelDomainName(), $bookings);
        if($canadd && sizeof($bookings) > 0) {
            echo "yes";
        } else {
            echo "no";
        }
    }


    public function addIncrementOrderIdToBooking() {
        $order = $this->getApi()->getOrderManager()->getOrderByincrementOrderId($_POST['data']['orderid']);
        $booking = $this->getPmsBooking();
        $booking->orderIds[] = $order->id;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function addRoomToGroup() {
        $type = $_POST['data']['type'];
        $count = $_POST['data']['count'];
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $startTime = $config->defaultStart;
        $endTime = $config->defaultEnd;
        
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']. " " . $startTime));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']. " " . $endTime));
        $bookingId = $this->getPmsBooking()->id;
        $guestInfoRoom = $_POST['data']['guestInfoOnRoom'];
        
        
        for($i = 0; $i < $count; $i++) {
            $this->getApi()->getPmsManager()->addBookingItemType($this->getSelectedMultilevelDomainName(), $bookingId, $type, $start, $end, $guestInfoRoom);
        }
        $this->currentBooking = null;
    }

    public function changeRoomCategory() {
        $newType = $_POST['data']['totype'];
        $roomId = $_POST['data']['roomId'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomId);
        $changed = $this->getApi()->getPmsManager()->setNewRoomType($this->getSelectedMultilevelDomainName(), $roomId, $booking->id, $newType);
        $res = array();
        $res['roomid'] = $roomId;
        if(!$changed) {
            $res['status'] = 1;
        } else {
            $res['status'] = 0;
        }
        echo json_encode($res);
    }

    
    public function addAddonsToRoom() {
        $filter = new \core_pmsmanager_PmsAddonFilter();
        $filter->start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $filter->end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $filter->deleteAddons = $_POST['data']['type'] == "removeaddon";
        $filter->productId = $_POST['data']['productid'];
        $filter->rooms = $_POST['data']['rooms'];
        $filter->singleDay = $_POST['data']['singleday'] == "true";
        $this->getApi()->getPmsAddonManager()->addProductToGroup($this->getSelectedMultilevelDomainName(), $filter);
    }
    
    public function updatePriceByDateRange() {
        $booking = $this->getPmsBooking();
        $bookingId = $booking->id;        
        $amount = $_POST['data']['value'];
        
        $start = strtotime($_POST['data']['start']);
        $end = strtotime($_POST['data']['end']);
        $daysToUpdate = array();
        while(true) {
            $daysToUpdate[] = date("d-m-Y", $start);
            $start += 86400;
            if($start > $end) {
                break;
            }
        }
        
        foreach($booking->rooms as $room) {
            if(!in_array($room->pmsBookingRoomId, $_POST['data']['rooms'])) {
                continue;
            }
            switch($_POST['data']['pricetype']) {
                case "dayprice":
                    foreach($room->priceMatrix as $day => $val) {
                        if(!in_array($day, $daysToUpdate)) { continue; }
                        $room->priceMatrix->{$day} = $amount;
                    }
                    break;
                case "wholestay":
                    foreach($room->priceMatrix as $day => $val) {
                        if(!in_array($day, $daysToUpdate)) { continue; }
                        $days = sizeof($daysToUpdate)-1;
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
                            if(!in_array($day, $daysToUpdate)) { continue; }
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
                            if(!in_array($day, $daysToUpdate)) { continue; }
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
                        if(!in_array($day, $daysToUpdate)) { continue; }
                        $room->priceMatrix[$key] = $avg;
                    }
                    break;
            }
        }
        
        $logtext = "Date range price update done for date: " . 
                $_POST['data']['start'] . " - " . $_POST['data']['end'] . 
                " amount:" . $_POST['data']['value'] . 
                " type: " . $_POST['data']['pricetype'];
        
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->getApi()->getPmsManager()->logEntry($this->getSelectedMultilevelDomainName(), $logtext, $booking->id, null);
        
        $this->clearCache();
        
    }
    
    public function updateGuestCount($booking) {
        $count = $_POST['data']['count'];
        foreach($booking->rooms as $room) {
            if(in_array($room->pmsBookingRoomId, $_POST['data']['rooms'])) {
                $room->numberOfGuests = $count;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }

    public function doRoomsBookedAction() {
        $action = $_POST['data']['type'];
        $booking = $this->getPmsBooking();
        $bookingId = $booking->id;
        if($action == "delete") {
            foreach($_POST['data']['rooms'] as $roomid) {
                $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedMultilevelDomainName(), $bookingId, $roomid);
            }
        } else if($action == "splitunique") {
            foreach($_POST['data']['rooms'] as $roomId) {
                $singleroomids = array();
                $singleroomids[] = $roomId;
                $this->getApi()->getPmsManager()->splitBooking($this->getSelectedMultilevelDomainName(), $singleroomids);
            }
        } else if($action == "split") {
            $this->getApi()->getPmsManager()->splitBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['rooms']);
        } else if($action == "updateGuestCount") {
            $this->updateGuestCount($booking);
        } else if($action == "singlepayments" || $action == "singlepaymentsnosend") {
            $this->getApi()->getPmsInvoiceManager()->removeOrderLinesOnOrdersForBooking($this->getSelectedMultilevelDomainName(), $bookingId, $_POST['data']['rooms']);
            foreach($_POST['data']['rooms'] as $roomid) {
                $selectedroom = null;
                foreach($booking->rooms as $room) {
                    if($room->pmsBookingRoomId == $roomid) {
                        $selectedroom = $room;
                        break;
                    }
                }
                
                if(!$selectedroom) {
                    return;
                }
                
                /* @var $selectedRoom core_pmsmanager_PmsBookingRooms */
                $filter = new \core_pmsmanager_NewOrderFilter();
                $bookingId = $_POST['data']['bookingid'];
                $filter->endInvoiceAt = $selectedroom->date->end;
                if(isset($_POST['data']['preview'])) {
                    $filter->avoidOrderCreation = $_POST['data']['preview'] == "true";
                }
                $filter->pmsRoomId = $selectedroom->pmsBookingRoomId;
                $filter->prepayment = true;
                $filter->createNewOrder = true;

                $newOrderId = $this->getManager()->createOrder($this->getSelectedMultilevelDomainName(), $bookingId, $filter);
                if($action != "singlepaymentsnosend") {
                    $email = $room->guests[0]->email;
                    $prefix = $room->guests[0]->prefix;
                    $phone = $room->guests[0]->phone;
                    $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedMultilevelDomainName(), $newOrderId, $bookingId, $email, $prefix, $phone);
                }
            }
        }
        $this->currentBooking = null;
    }
        
    public function updateStayPeriode() {
        $booking = $this->getPmsBooking();
        $_SESSION['notChangedError'] = array();
        foreach($booking->rooms as $room) {
            $start = $_POST['data']['start_'.$room->pmsBookingRoomId] . " " . $_POST['data']['starttime_'.$room->pmsBookingRoomId];
            $end = $_POST['data']['end_'.$room->pmsBookingRoomId] . " " . $_POST['data']['endtime_'.$room->pmsBookingRoomId];
            
            $start = $this->convertToJavaDate(strtotime($start));
            $end = $this->convertToJavaDate(strtotime($end));
            
            $res = $this->getApi()->getPmsManager()->changeDates($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId, $booking->id, $start, $end);
            if(!$res) {
                $_SESSION['notChangedError'][$room->pmsBookingRoomId] = "Unable to change date on this room";
            }
        }
        $this->currentBooking = null;
    }
    
    
    public function massUpdateRoomPrice() {
        $price = $_POST['data']['price'];
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $room) {
            foreach($room->priceMatrix as $day => $val) {
                $room->priceMatrix->{$day} = $price;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->currentBooking = null;
    }
    
    
    public function massUpdateGuests() {
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $room) {
            $room->numberOfGuests = sizeof($_POST['data']['guests'][$room->pmsBookingRoomId]);
            $room->guests = $_POST['data']['guests'][$room->pmsBookingRoomId];
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function getConfirmationContent() {
        $msgs = $this->getApi()->getPmsNotificationManager()->getAllMessages($this->getSelectedMultilevelDomainName());
         
       foreach($msgs as $conf) {
            if($conf->id == $_POST['data']['type']) {
                $data = array('title' => $conf->title, 'content' => $conf->content);
                echo json_encode($data);
            }
        }
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $bookingId = $this->getPmsBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, $vat);
        $this->clearCache();
    }

    public function toggleAutosendPaymentLink() {
        $booking = $this->getPmsBooking();
        $booking->autoSendPaymentLink = !$booking->autoSendPaymentLink;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function searchForProducts() {
        $this->includefile("conference_productsearchresult");
    }

    public function removeFromOverBookingList() {
        $room = $this->getPmsBookingRoom();
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $r) {
            if($room->pmsBookingRoomId == $r->pmsBookingRoomId) {
                $room->overbooking = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function addToBlockList() {
        $room = $this->getPmsBookingRoom();
        foreach($room->guests as $guest) {
            if($guest->guestId == $_POST['data']['guestid']) {
                $block = new \core_pmsmanager_PmsBlockedUser();
                $block->email = $guest->email;
                $block->phone = $guest->phone;
                $this->getApi()->getPmsManager()->addToBlockList($this->getSelectedMultilevelDomainName(), $block);
            }
        }
    }
    
    public function translateNotSendPaymentLinkReason($reason) {
        $reasons = array();
        /*
         if(!config.autoSendPaymentReminder) {
            return 0; //Not configure to send.
            return 1; //Booking is deleted
            return 2; //No active rooms.
            return 3; //Registrered by administrator
            return 4; //Already sent
            return 5; //Everything is paid for
                return 6; //Prepaid by ota
            return 7; //Booking has started.
         * 
         */
        $reasons[0] = "payment links has not been configured yet, go to settings -> global settings to configure the payment link system";
        $reasons[1] = "the booking has been deleted";
        $reasons[2] = "there are not rooms added to this booking to require payment on";
        $reasons[3] = "booked by a receptionist / hotellier.";
        $reasons[4] = "the payment link has already been sent";
        $reasons[5] = "everything is paid for.";
        $reasons[6] = "its already been paid for by the OTA";
        $reasons[7] = "the booking has already started.";
        $reasons[8] = "we are waiting for the booking to be automatically charged, if not charged in 10 minutes payment link is sent.";
        $reasons[9] = "payment link is not sent in the morning, its intrusive.";
        $reasons[10] = "booking will be deleted in 30 minutes.";
        $reasons[11] = "we are waiting 30 minutes for payment to be completed, payment link is sent after that.";
        $reasons[12] = "access has been forced.";
        $reasons[13] = "It have an order which is a prepaid order by ota.";
        return $reasons[$reason];
    }
    
    public function getName() {
        return "PmsBookingGroupRoomView";
    }

    public function loadCategoryAvailability() {
        $this->includefile("roomsavailable");        
    }
    
    public function canChangeStay() {
        
        
        $roomId = $this->getPmsBookingRoom()->pmsBookingRoomId;
        $bookingId = $this->getPmsBooking()->id;
        $start = $this->convertToJavaDate(strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']));
        
        $roomtypeanditem = $_POST['data']['roomtypeanditem'];
        $roomtypeanditem = explode("_", $roomtypeanditem);
        
        $bookingsToAdd = array();
        $booking = new \core_bookingengine_data_Booking();
        $booking->startDate = $start;
        $booking->endDate = $end;
        $booking->bookingItemId = $roomtypeanditem[1];
        $booking->bookingItemTypeId = $roomtypeanditem[0];
        $bookingsToAdd[] =$booking;
        
        if(strtotime($booking->startDate) > strtotime($booking->endDate)) {
            echo "<i class='fa fa-warning'></i> The stay is starting after its ending.";
            return;
        }
        
        $engine = $this->getSelectedMultilevelDomainName();
        
        $canAdd = true;
        if($this->getPmsBookingRoom()->bookingId) {
            $items = (array)$this->getApi()->getBookingEngine()->getAvailbleItemsWithBookingConsideredAndShuffling($engine, $booking->bookingItemTypeId, $booking->startDate, $booking->endDate, $this->getPmsBookingRoom()->bookingId);
            if(sizeof($items) == 0) {
                $canAdd = false;
            }
            if($booking->bookingItemId) {
                $found = false;
                foreach($items as $item) {
                    if($item->id == $booking->bookingItemId) {
                        $found = true;
                    }
                }
                if(!$found) { $canAdd = false; }
            }
        }
        if(!$canAdd) {
            echo "<i class='fa fa-warning'></i> ";
            if($booking->bookingItemId) {
                $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $booking->bookingItemId);
                echo "Room " . $item->bookingItemName . " is not available in the time span " . date("d.m.Y H:i", strtotime($start)) . " - " . date("d.m.Y H:i", strtotime($end));
            } else {
                $category = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $booking->bookingItemTypeId);
                echo "Category " . $category->name . " is not available in the time span " . date("d.m.Y H:i", strtotime($start)) . " - " . date("d.m.Y H:i", strtotime($end));
            }
        }
        return $canAdd;
    }
    
    public function addAnotherRoom() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " " . $config->defaultStart));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " " . $config->defaultEnd));
        $bookingId =  $this->getPmsBooking()->id;
        $type = $_POST['data']['type'];
        if(!$type) {
            return;
        }
        $gs_multilevel_name = $this->getSelectedMultilevelDomainName();
        $count = $_POST['data']['count'];
        $newRoom = "";
        for($i = 0; $i < $count; $i++) {
            $newRoom = $this->getApi()->getPmsManager()->addBookingItemType($gs_multilevel_name, $bookingId, $type, $start, $end, null);
        }
        echo $newRoom;
    }
    
    public function printAvailableRoomsFromCategory($start=null, $end=null) {
        //appending 14.00 and 11.00 to start & end
        $start = isset($start) ? $start : $_POST['data']['start'];
        $end = isset($end) ? $end : $_POST['data']['end'];
        $start = $this->convertToJavaDate(strtotime($start . " 14:00"));
        $end = $this->convertToJavaDate(strtotime($end . " 11:00"));

        $categories = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        echo "<option value=''>Choose a category</option>";

        foreach($categories as $cat) {
           // $number = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedMultilevelDomainName(), $cat->id, $start, $end);
           $number = $this->getApi()->getPmsManager()->getNumberOfAvailable($this->getSelectedMultilevelDomainName(), $cat->id, $start, $end, false);
            echo "<option value='" . $cat->id . "'>" . $cat->name . " ($number available)</option>";
        }
    }
    
    
    public function splitStay() {
        $roomId = $_POST['data']['roomid'];
        $splitTime = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['time']));
        $this->getApi()->getPmsManager()->splitStay($this->getSelectedMultilevelDomainName(), $roomId, $splitTime);
        $this->clearCache();
    }
    
    public function searchForGuest() {
        $guest = new \core_pmsmanager_PmsGuests();
        switch($_POST['data']['type']) {
            case "name": 
                $guest->name = $_POST['data']['keyword'];
                break;
            case "phone": 
                $guest->phone = $_POST['data']['keyword'];
                break;
            case "email": 
                $guest->email = $_POST['data']['keyword'];
                break;
        }
        $suggestions = $this->getApi()->getPmsManager()->findRelatedGuests($this->getSelectedMultilevelDomainName(), $guest);
        $newbooking = new \ns_bf644a39_c932_4e3b_a6c7_f6fd16baa34d\PmsNewBooking20();
        $newbooking->printSuggestions($suggestions);
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
    
    public function ignorechannelmanager() {
        $booking = $this->getPmsBooking();
        $booking->ignoreWubook = !$booking->ignoreWubook;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        if($booking->ignoreWubook) {
            $this->getApi()->getPmsManager()->logEntry($this->getSelectedMultilevelDomainName(), "Ignore update from channel manager", $booking->id, null);
        } else {
            $this->getApi()->getPmsManager()->logEntry($this->getSelectedMultilevelDomainName(), "cancelled - ignore update from channel manager", $booking->id, null);
        }
    }
    
    public function updateStayTime() {
        if(!$this->canChangeStay()) return;
        $catsanditems = $_POST['data']['roomtypeanditem'];
        $catsanditems = explode("_", $catsanditems);
        $typeId = $catsanditems[0];
        $itemId = $catsanditems[1];
        
        $roomId = $this->getPmsBookingRoom()->pmsBookingRoomId;
        $bookingId = $this->getPmsBooking()->id;
        $start = $this->convertToJavaDate(strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']));
        
        $room = $this->getPmsBookingRoom();
        if($room->bookingItemId != $itemId || $room->bookingItemTypeId != $typeId) {
            if(!$itemId) {
                $itemId = $typeId;
            }
            $this->getApi()->getPmsManager()->setBookingItemAndDate($this->getSelectedMultilevelDomainName(), $roomId, $itemId, false, $start, $end);
        } else {
            $this->getApi()->getPmsManager()->changeDates($this->getSelectedMultilevelDomainName(), $roomId, $bookingId, $start, $end);
        }
        $this->clearCache();
    }
    
    public function toggleCreditHistory() {
        $_SESSION['pmsshowcredithistory'] = (isset($_SESSION['pmsshowcredithistory']) && $_SESSION['pmsshowcredithistory']) ? false : true;
    }
    
    public function render() {
        $this->displayToggleNew();
        
        echo "<div class='room_view_outer' usenewpayment='true'>";
        $this->includefile("main");
        echo "</div>";
        echo "<style>";
        echo ".gsoverlay2 .gsoverlayinner, .gsoverlay1 .gsoverlayinner { width: 95%; }";
        echo "</style>";
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
        
        $prices = $_POST['data']['prices'];
        foreach($prices as $key => $val) {
            $prices[$key] = str_replace(",", ".", $val);
        }
        
        $this->getApi()->getPmsManager()->updatePriceMatrixOnRoom($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId, $prices);
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
    
    public function loadBooking() {
        $pmsBookingGroupView = new \ns_3e2bc00a_4d7c_44f4_a1ea_4b1b953d8c01\PmsBookingGroupRoomView();
        $pmsBookingGroupView->setRoomId($_POST['data']['id']);
        $pmsBookingGroupView->renderApplication(true, $this, true);
    }
    
    public function updateTimelineDates() {
        $_SESSION['timelinestart'] = $_POST['data']['start'];
        $_SESSION['timelinesend'] = $_POST['data']['end'];
    }
    
    public function getTimeLineStart() {
        if(isset($_SESSION['timelinestart'])) {
            return $_SESSION['timelinestart'];
        }
        return date("01.m.Y");
    }
    public function getTimeLineEnd() {
        if(isset($_SESSION['timelinesend'])) {
            return $_SESSION['timelinesend'];
        }
        return date("t.m.Y");
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
    public function attachOrderToBooking() {
        $this->getApi()->getPmsManager()->attachOrderToBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid'], $_POST['data']['orderid']);
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

    public function removeAddons() {
        $booking = $this->getPmsBooking();
        $productId = $_POST['data']['productid'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $room) {
            foreach($config->addonConfiguration as $addonItem) {
                if($addonItem->productId == $productId) {
                    $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedMultilevelDomainName(), $addonItem->addonType, $room->pmsBookingRoomId, true);
                    break;
                }
            }
        }
        $this->currentBooking = null;
    }
    
    public function addAddons() {
        $booking = $this->getPmsBooking();
        $productId = $_POST['data']['productid'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $room) {
            foreach($config->addonConfiguration as $addonItem) {
                if($addonItem->productId == $productId) {
                    $this->getApi()->getPmsManager()->addAddonsToBookingIgnoreRestriction($this->getSelectedMultilevelDomainName(), $addonItem->addonType, $room->pmsBookingRoomId, false);
                    break;
                }
            }
        }
        $this->currentBooking = null;
    }
    
    
    public function getSummary($summaries, $room) {
        foreach ($summaries as $summary) {
            if ($summary->pmsBookingRoomId == $room->pmsBookingRoomId) {
                return $summary;
            }
        }
        
        return null;
    }
    
    public function countNumberOfDays($room) {
        $i = 0;
        foreach ($room->priceMatrix as $date => $value) {
            $i++;
        }
        return $i;
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

    
    public function printGuestRow($guest) {
        $prefix = $guest->prefix;
        if(!$prefix) {
            $prefix = $this->getDefaultPrefix();
        }
        echo "<div style='margin-bottom: 2px;' class='guestrow'>";
        echo "<span class='shop_button removeguestrow' style='margin-right:5px;border-radius:5px;'><i class='fa fa-trash-o'></i></span>";
        echo "<input type='text' class='gsniceinput1' gsname='name' value='".$guest->name."' style='margin-right: 10px;'>";
        echo "<input type='text' class='gsniceinput1' gsname='email' value='".$guest->email."' style='margin-right: 10px;'>";
        echo "<input type='text' class='gsniceinput1' gsname='prefix' value='".$prefix."' style='width: 30px;margin-right: 10px;'>";
        echo "<input type='text' class='gsniceinput1' gsname='phone' value='".$guest->phone."' style='margin-right: 10px;'>";
        echo "<input type='hidden' class='gsniceinput1' gsname='guestId' value='".$guest->guestId."'>";
        echo "</div>";
    }
    
    public function setRoomId($id) {
        $this->pmsBooking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $id);
        
        $setFirst = false;
        if(!$this->pmsBooking) {
            $this->pmsBooking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $id);
            $setFirst = true;
        }
        
        foreach($this->pmsBooking->rooms as $room) {
            if($room->pmsBookingRoomId == $id || $setFirst) {
                $this->pmsBookingRoom = $room;
                break;
            }
        }
    }
      public function getDefaultPrefix() {
        if(isset($this->defaultPrefix) && $this->defaultPrefix) {
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
    
    public function toggleAllOrders() {
        $showAllOrders = isset($_SESSION['showallorders']) && $_SESSION['showallorders'];
        $_SESSION['showallorders'] = !$showAllOrders;
        $_SESSION['pmsshowcredithistory'] = !$showAllOrders;
    }
    
    public function toggleAccrued() {
        $showAllOrders = isset($_SESSION['showAccrued']) && $_SESSION['showAccrued'];
        $_SESSION['showAccrued'] = !$showAllOrders;
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
    
    public function toggleIsChild() {
        $booking = $this->getPmsBooking();
        foreach($booking->rooms as $room) {
            foreach($room->guests as $guest) {
                if($guest->guestId == $_POST['data']['guestid']) {
                    $guest->isChild = !$guest->isChild;
                }
            }
        }
            
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function markRoomCleanedwithoutlog() {
        $room = $this->getPmsBookingRoom();
        $this->getApi()->getPmsManager()->markRoomAsCleanedWithoutLogging($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
        $this->clearCache();
    }
    public function togglePayAfterStayForGroup() {
        $booking = $this->getPmsBooking();
        $booking->createOrderAfterStay = !$booking->createOrderAfterStay;
        foreach ($booking->rooms as $room){
            $room->createOrdersOnZReport = !$room->createOrdersOnZReport;
        }
        $text = $booking->createOrderAfterStay ? " on " : " off";
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->getApi()->getPmsManager()->logEntry($this->getSelectedMultilevelDomainName(), "Pay after stay toggled to : " . $text, $booking->id, null);
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
        unset($_SESSION['cachedbooking'][$this->getSelectedRoomId()]);
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
        $ids = array();
        foreach($room->addons as $addon) {
            if(in_array($addon->addonId, $_POST['data']['addonIds'])) {
                $ids[] = $addon->addonId;
            }
        }
        $this->getApi()->getPmsManager()->removeAddonFromRoomByIds($this->getSelectedMultilevelDomainName(), $ids, $room->pmsBookingRoomId);
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
        return $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
    }

    public function getItems() {
        return $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedMultilevelDomainName());
    }

    public function useNew() {
        if(isset($_SESSION['oldbookingviewtoggled']) && $_SESSION['oldbookingviewtoggled']) {
            return false;
        }
        return true;
;    }

    public function getSelectedRoomId() {
        return $this->getPmsBookingRoom()->pmsBookingRoomId;
    }
    
    
    public function cancelautodeletion() {
        $booking = $this->getPmsBooking();
        $booking->avoidAutoDelete = true;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->clearCache();
    }
    
    public function removenewversion() {
        $_SESSION['oldbookingviewtoggled'] = 'true';
    }

    public function displayToggleNew() {
        $config = $this->getPmsConfiguration();
        if(!$config->conferenceSystemActive) {
            echo '<div id="switch-booking-view" title="Switch to old booking detail view"><i class="fa fa-retweet" gsclick="removenewversion" gs_callback="window.location.reload();"></i></div>';
            //echo "<div style='background-color:green; color:#fff;text-align:center;cursor:pointer; padding: 5px;' gsclick='removenewversion' gs_callback='window.location.reload();'>(This view is in BETA MODE - if you find errors please inform us about it) - Toggle old version</div>";
        }
    }

    public function getConference() {
        if(!$this->conference) {
            $this->conference = $this->getApi()->getPmsConferenceManager()->getConference($this->getPmsBooking()->conferenceId);
        }
        return $this->conference;
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsConferenceEvent[]
     */
    public function getEvents() {
        if(!$this->events) {
            $this->events = $this->getApi()->getPmsConferenceManager()->getConferenceEvents($this->getConference()->id);
        }
        return $this->events;
    }

    public function deleteEvent() {
        $_SESSION['pmsconferenceeventdeleted'] = $this->getSelectedEventId();
        $this->getApi()->getPmsConferenceManager()->deleteConferenceEvent($this->getSelectedEventId());
    }
    
    public function saveConferenceTitle() {
        $conference = $this->getApi()->getPmsConferenceManager()->getConference($_POST['data']['conferenceid']);
        $conference->meetingTitle = $_POST['data']['title'];
        $this->getApi()->getPmsConferenceManager()->saveConference($conference);
    }
    
    public function addNewEvent() {
        $conference = $this->getConference()->id;
        
        $event = new \core_pmsmanager_PmsConferenceEvent();
        $event->name = $_POST['data']['title'];
        $event->pmsConferenceId = $this->getConference()->id;
        $event->pmsConferenceItemId = $_POST['data']['pmsConferenceItemId'];
        $event->from = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$_POST['data']['starttime']));
        $event->to = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$_POST['data']['endtime']));
        
        $eventId = $this->getApi()->getPmsConferenceManager()->createConferenceEvent($event);
        echo $eventId;
    }
    
    public function updateEvent() {
        $event = $this->getSelectedEvent();
        $event->name = $_POST['data']['name'];
        $event->pmsConferenceId = $this->getConference()->id;
        $event->pmsConferenceItemId = $_POST['data']['pmsConferenceItemId'];
        $event->from = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$_POST['data']['starttime']));
        $event->to = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$_POST['data']['endtime']));
        $event->attendeeCount = $_POST['data']['attendeeCount'];
        $event->description = $_POST['data']['description'];
        
        $this->getApi()->getPmsConferenceManager()->saveConferenceEvent($event);
    }
    
    public function getSelectedEventId() {
        if(isset($_POST['data']['eventid']) && $_POST['data']['eventid']) {
            return $_POST['data']['eventid'];
        }
        $events = (array)$this->getEvents();
        if(sizeof($events) > 0) {
            return $this->getEvents()[0]->id;            
        } else {
            return "overview";
        }
    }
    
    public function deleteConference() {
        $this->getApi()->getPmsConferenceManager()->deleteConference($this->getPmsBooking()->conferenceId);
        $booking = $this->getPmsBooking();
        $booking->conferenceId = "";
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function saveEventActivities() {
        $event = $this->getSelectedEvent();
        foreach ($_POST['data']['activites'] as $activity) {
            $existing = $this->getExistingEntry($activity['activityid']);
            $existing->pmsEventId = $event->id;
            $existing->conferenceId = $this->getConference()->id;
            $existing->count = $activity['count'];
            $existing->text = $activity['text'];
            $existing->extendedText = $activity['extendedText'];
            $existing->from = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$activity['from']));
            if($activity['to']) {
                $existing->to = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$activity['to']));
            } else {
                $existing->to = null;
            }
            
            $this->getApi()->getPmsConferenceManager()->saveEventEntry($existing);
        }
    }

    /**
     * 
     * @return \core_pmsmanager_PmsConferenceItem[]
     */
    public function getEventItems() {
        if(!$this->eventItems) {
            $this->eventItems = $this->getApi()->getPmsConferenceManager()->getAllItem("-1");
        }
        
        return $this->indexList($this->eventItems);
    }

    public function loadAddEvent() {
        $this->includefile("addeventpanel");
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsConferenceEvent
     */
    public function getSelectedEvent() {
        $events = $this->getEvents();
        foreach($events as $event) {
            if($event->id == $this->getSelectedEventId()) {
                return $event;
            }
        }
        return null;
    }

    public function getExistingEntry($activityId) {
        $activities = $this->getActivities();
        
        foreach($activities as $activity) {
            if($activity->id == $activityId) {
                return $activity;
            }
        }
        return new \core_pmsmanager_PmsConferenceEventEntry();
    }

    public function createNewConference() {
        $conference = new \core_pmsmanager_PmsConference();
        $conference->meetingTitle = $_POST['data']['title'];
        $conference->forUser = $this->getPmsBooking()->userId;
        $conference->conferenceDate = $this->convertToJavaDate(strtotime($_POST['data']['conferencedate']));
        $conf = $this->getApi()->getPmsConferenceManager()->saveConference($conference);
        $booking = $this->getPmsBooking();
        $booking->conferenceId = $conf->id;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function connectToConference() {
        $booking = $this->getPmsBooking();
        $booking->conferenceId = $_POST['data']['conferenceid'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function findConference() {
        $filter = new \core_pmsmanager_PmsConferenceFilter();
        $filter->title = $_POST['data']['keyword'];
        $conferences = $this->getApi()->getPmsConferenceManager()->getAllConferences($filter);
        foreach($conferences as $conf) {
            echo "<div>" . $conf->meetingTitle . " (<span style='cursor:pointer;' class='bookinghighlightcolor'"
                    . " gs_callback='app.PmsBookingGroupRoomView.conferenceSelected'"
                    . " gsclick='connectToConference' roomid='".$this->getSelectedRoomId()."' conferenceid='".$conf->id."'>select</span>)</div>";
        }
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsConferenceEventEntry[]
     */
    public function getActivities() {
        if($this->eventEntries == null) {
            $this->eventEntries = $this->getApi()->getPmsConferenceManager()->getEventEntries($this->getSelectedEventId());
        }
        return (array)$this->eventEntries;
        
    }

    public function isConferenceView() {
        if(isset($_POST['data']['mainarea']) && $_POST['data']['mainarea'] == "conference") {
            return true;
        }
        return false;
    }

    /**
     * @return \core_pmsmanager_PmsConfiguration
     */
    public function getPmsConfiguration() {
          if($this->config == null) {
            $start = microtime(true);
            $this->config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
//            echo "Get config: " . ((microtime(true)-$start)*1000) . "<br>";
            
        }
        return $this->config;
    }

    public function displayPopUpAttributes($room, $roomId = null) {
        if($roomId != null) {
            return "method='loadBooking' gs_show_overlay='booking_room_view' id='".$roomId."' ";
        } else {
            return "method='loadBooking' gs_show_overlay='booking_room_view' id='".$room->pmsBookingRoomId. "' ";
        }
    }

    
    public function saveConferenceData() {
        $conference = $this->getApi()->getPmsConferenceManager()->getConference($_POST['data']['conferenceid']);
        $conference->meetingTitle = $_POST['data']['title'];
        $conference->conferenceDate = $this->convertToJavaDate(strtotime($_POST['data']['conferencedate']));
        $conference->contactName = $_POST['data']['contact_name'];
        $conference->contactEmail = $_POST['data']['contact_email'];
        $conference->contactPhone = $_POST['data']['contact_phone'];
        $conference->attendeeCount = $_POST['data']['attendeeCount'];
        $conference->state = $_POST['data']['state'];
        
        $this->getApi()->getPmsConferenceManager()->saveConference($conference);
    }
    
    public function addProductToEvent() {
        $eventId = $this->getSelectedEventId();
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);

        $cartItem = new \core_cartmanager_data_CartItem();
        $cartItem->product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        $cartItem->count = 1;
        
        $cartItems = array();
        $cartItems[] = $cartItem;
        
        $this->getApi()->getPmsConferenceManager()->addCartItemsToConference($booking->conferenceId, $eventId, $cartItems);
    }
    
    public function updateCartItemForPosTab() {
        $eventId = $this->getSelectedEventId();
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $_POST['data']['roomid']);
        
        if ($_POST['data']['submit'] == "delete") {
            $this->getApi()->getPmsConferenceManager()->removeCartItemFromConference($booking->conferenceId, $_POST['data']['cartitemid']);
        } else {
            $cartItem = $this->getApi()->getPmsConferenceManager()->getCartItem($booking->conferenceId, $_POST['data']['cartitemid']);
            $cartItem->count = $_POST['data']['count'];
            $cartItem->product->price = $_POST['data']['price'];
            $cartItem->product->name = $_POST['data']['name'];
            $this->getApi()->getPmsConferenceManager()->updateCartItem($booking->conferenceId, $cartItem);
        }
        
        echo $this->getApi()->getPmsConferenceManager()->getTotalPriceForCartItems($booking->conferenceId, $eventId);
    }

    public function isPgaSupportActivated() {
        // Letting bergstaden test it.
        if ($this->getFactory()->getStore()->id == "1ed4ab1f-c726-4364-bf04-8dcddb2fb2b1" || $this->getFactory()->getStore()->id == "8016f02d-95bc-4910-b600-bbea8c06bedf") {
            return true;
        }
        
        return \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isGetShopUser();
    }

    public function listTimeLine() {
        $this->includefile("conference_timeline");
    }

    public function loadFromReportView() {
        $this->loadingFromReportView = true;
    }

    public function loadingFromReportView() {
        return $this->loadingFromReportView;
    }

}
?>

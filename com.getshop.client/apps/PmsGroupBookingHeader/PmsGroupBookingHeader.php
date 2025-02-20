<?php
namespace ns_cbcf3e53_c035_43c2_a1ca_c267b4a8180f;

class PmsGroupBookingHeader extends \MarketingApplication implements \Application {
    private $currentBooking = null;
    public $notChangedError = array();
    public $addedConferenceRoom = false;
    public $defaultPrefix = false;
    
    public function getDescription() {
        
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

    public function addExistingRoomToBooking() {
        $curbooking = $this->getCurrentBooking();
        $roomId = $_POST['data']['roomid'];
        $moved = $this->getApi()->getPmsManager()->moveRoomToBooking($this->getSelectedMultilevelDomainName(), $roomId, $curbooking->id);
        if($moved) {
            echo "1";
        } else {
            echo "0";
        }
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
            echo "<span class='col choose importroom' roomid='".$room->pmsRoomId."'>Import</span>";
            echo "</div>";
        }
    }
    
    public function updatePriceByDateRange() {
        $booking = $this->getCurrentBooking();
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
        $this->currentBooking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $_SESSION['PmsSearchBooking_bookingId']);
        
        
        $pmsrooms = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $pmsrooms->clearCache();
        
    }
    
    public function createNewUser(){
        $name = $_POST['data']['name'];
        $bookingId = $this->getCurrentBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, "");
        return $this->getUserForBooking();
    }
   
    public function massUpdateRoomPrice() {
        $price = $_POST['data']['price'];
        $booking = $this->getCurrentBooking();
        foreach($booking->rooms as $room) {
            foreach($room->priceMatrix as $day => $val) {
                $room->priceMatrix->{$day} = $price;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->currentBooking = null;
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
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $bookingId = $this->getCurrentBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, $vat);
        
        return $this->getUserForBooking();
    }   
    
    public function saveUser($user) {
    }
    
    
    public function changeUser($user) {
        $booking = $this->getCurrentBooking();
        $booking->userId = $user->id;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    
    public function getUserForBooking() {
        $this->currentBooking = null;
        return $this->getApi()->getUserManager()->getUserById($this->getCurrentBooking()->userId);
    }
    
    public function startPaymentProcessAllRooms() {
        $createorder = new \core_pmsmanager_NewOrderFilter();
        $createorder->createNewOrder = true;
        $createorder->avoidOrderCreation = true;
        $bookingId = $this->getCurrentBooking()->id;
        $this->getApi()->getCartManager()->clear();
        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $bookingId, $createorder);
        
    }
    
    public function saveEvent() {
        $confdata = $this->getApi()->getPmsManager()->getConferenceData($this->getSelectedMultilevelDomainName(), $this->getCurrentBooking()->id);
        $confdata->note = $_POST['data']['note'];
        $confdata->nameOfEvent = $_POST['data']['nameOfEvent'];
        $this->getApi()->getPmsManager()->saveConferenceData($this->getSelectedMultilevelDomainName(), $confdata);
    }
    
    public function addconferenceroom() {
        $item = $_POST['data']['room'];
        $bookingId = $this->getCurrentBooking()->id;
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " " . $_POST['data']['startTime']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " " . $_POST['data']['endTime']));
        $res = $this->getApi()->getPmsManager()->addBookingItem($this->getSelectedMultilevelDomainName(), $bookingId, $item, $start, $end);
        if(!$res) {
            $this->addedConferenceRoom = true;
        } else {
            $this->addedConferenceRoom = false;
        }
        $this->currentBooking = null;
    }

    public function addAddons() {
        $booking = $this->getCurrentBooking();
        $productId = $_POST['data']['id'];
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
    
    public function removeAddons() {
        $booking = $this->getCurrentBooking();
        $productId = $_POST['data']['id'];
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
    
    public function getName() {
        return "PmsGroupBookingHeader";
    }
    
    public function addRoomToGroup() {
        $type = $_POST['data']['type'];
        $count = $_POST['data']['count'];
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $startTime = $config->defaultStart;
        $endTime = $config->defaultEnd;
        
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']. " " . $startTime));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']. " " . $endTime));
        $bookingId = $this->getCurrentBooking()->id;
        $guestInfoRoom = $_POST['data']['guestInfoOnRoom'];
        
        
        for($i = 0; $i < $count; $i++) {
            $this->getApi()->getPmsManager()->addBookingItemType($this->getSelectedMultilevelDomainName(), $bookingId, $type, $start, $end, $guestInfoRoom);
        }
        $this->currentBooking = null;
    }

    public function render() {
        $this->setBookingEngineId();
        $this->includefile("groupheader");
    }

    public function loadArea() {
        if(isset($_POST['data']['area'])) {
            $_SESSION['currentgroupbookedarea'] = $_POST['data']['area'];
        } else {
            echo "NOT FOUND";
        }
        switch($_POST['data']['area']) {
            case "owner":
                $this->includefile("groupbookerowner");
                break;
            case "guests":
                $this->includefile("guests");
                break;
            case "rooms":
                $this->includefile("rooms");
                break;
            case "payments":
                $this->includefile("payments");
                break;
            case "stay":
                $this->includefile("stay");
                break;
            case "addons":
                $this->includefile("addons");
                break;
            case "conference":
                $this->includefile("conference");
                break;
            default:
                echo "Path not exisiting yet: " . $_POST['data']['area'];
        }
    }
    
    public function removeRow() {
        $confdata = $this->getApi()->getPmsManager()->getConferenceData($this->getSelectedMultilevelDomainName(), $this->getCurrentBooking()->id);
        $day=null;
        foreach($confdata->days as $tmpday) {
            $newRows = array();
            foreach($tmpday->conferences as $conf) {
                if($conf->rowId == $_POST['data']['id']) {
                    continue;
                }
                $newRows[] = $conf;
            }
            $tmpday->conferences = $newRows;
        }
        
        $newRows = array();
        foreach($confdata->days as $day) {
            if(sizeof($day->conferences) == 0) {
                continue;
            }
            $newRows[] = $day;
        }
        $confdata->days = $newRows;
        
        
        $this->getApi()->getPmsManager()->saveConferenceData($this->getSelectedMultilevelDomainName(), $confdata);
    }
    
    public function updateActionPoint() {
        $confdata = $this->getApi()->getPmsManager()->getConferenceData($this->getSelectedMultilevelDomainName(), $this->getCurrentBooking()->id);
        foreach($confdata->days as $tmpday) {
            foreach($tmpday->conferences as $conf) {
                if($conf->rowId == $_POST['data']['rowid']) {
                    $conf->from = $_POST['data']['from'];
                    $conf->to = $_POST['data']['to'];
                    $conf->actionName = $_POST['data']['name'];
                    $conf->attendeesCount = $_POST['data']['size'];
                    $conf->place = $_POST['data']['where'];
                }
            }
        }
        $this->getApi()->getPmsManager()->saveConferenceData($this->getSelectedMultilevelDomainName(), $confdata);        
    }
    
    public function removeConferenceRoom() {
        $bookingId = $this->getCurrentBooking()->id;
        $roomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedMultilevelDomainName(), $bookingId, $roomId);
        $this->currentBooking = null;
    }
    
    public function addactionpoint() {
        $dateToAddTo = $_POST['data']['date'];

        $confdata = $this->getApi()->getPmsManager()->getConferenceData($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
        $day=null;
        foreach($confdata->days as $tmpday) {
            if(date("dmy", strtotime($dateToAddTo)) == date("dmy", strtotime($tmpday->day))) {
                $day = $tmpday;
            }
        }
        if(!$day) {
            $day = new \core_pmsmanager_ConferenceDataDay();
            $day->day = $dateToAddTo;
            $confdata->days[] = $day;

        }
        $row = $_POST['data'];
        $conferenceDataRow = new \core_pmsmanager_ConferenceDataRow();
        $conferenceDataRow->place = $row['where'];
        $conferenceDataRow->from = $row['from'];
        $conferenceDataRow->to = $row['to'];
        $conferenceDataRow->actionName = $row['name'];
        $conferenceDataRow->attendeesCount = $row['size'];
        $day->conferences[] = $conferenceDataRow;
        
        $newRows = array();
        foreach($confdata->days as $day) {
            if(sizeof($day->conferences) == 0) {
                continue;
            }
            $newRows[] = $day;
        }
        $confdata->days = $newRows;
        
        $this->getApi()->getPmsManager()->saveConferenceData($this->getSelectedMultilevelDomainName(), $confdata);
    }
    
    public function doRoomsBookedAction() {
        $action = $_POST['data']['type'];
        $bookingId = $this->getCurrentBooking()->id;
        $booking = $this->getCurrentBooking();
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
    
    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getCurrentBooking() {
        if (!$this->currentBooking) {
            $this->currentBooking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $_SESSION['PmsSearchBooking_bookingId']);
        }
        return $this->currentBooking;
    }

    public function setBookingEngineId() {
        if (isset($_GET['bookingId'])) {
            $this->currentBooking = null;
            $_SESSION['PmsSearchBooking_bookingId'] = $_GET['bookingId'];
        }
    }

    public function updateStayPeriode() {
        $booking = $this->getCurrentBooking();
        foreach($booking->rooms as $room) {
            $start = $_POST['data']['start_'.$room->pmsBookingRoomId] . " " . $_POST['data']['starttime_'.$room->pmsBookingRoomId];
            $end = $_POST['data']['end_'.$room->pmsBookingRoomId] . " " . $_POST['data']['endtime_'.$room->pmsBookingRoomId];
            
            $start = $this->convertToJavaDate(strtotime($start));
            $end = $this->convertToJavaDate(strtotime($end));
            
            $res = $this->getApi()->getPmsManager()->changeDates($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId, $booking->id, $start, $end);
            if(!$res) {
                $this->notChangedError[$room->pmsBookingRoomId] = "Unable to change date on this room";
            }
        }
        $this->currentBooking = null;
    }
    
    public function massUpdateGuests() {
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
        foreach($booking->rooms as $room) {
            $room->numberOfGuests = sizeof($_POST['data']['guests'][$room->pmsBookingRoomId]);
            $room->guests = $_POST['data']['guests'][$room->pmsBookingRoomId];
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function saveGuestInformation() {
        $booking = $this->getCurrentBooking();
        foreach($booking->rooms as $room) {
            $room->guests = $_POST['data']['rooms'][$room->pmsBookingRoomId];
            $room->numberOfGuests = sizeof($room->guests);
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
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

    public function includeSelectedArea() {
        $area = $this->getArea();
        if(!isset($_POST['data']) || !$_POST['data']) { $_POST['data'] = array(); }
        $_POST['data']['area'] = $area;
        $this->loadArea();
    }

    public function getArea() {
        $area = "owner";
        if(isset($_SESSION['currentgroupbookedarea'])) {
            $area = $_SESSION['currentgroupbookedarea'];
        }
        return $area;
    }

    public function getDefaultPrefix() {
        if($this->defaultPrefix) {
            return $this->defaultPrefix;
        }
        
        $this->defaultPrefix = $this->getFactory()->getStoreConfiguration()->defaultPrefix;
        return $this->defaultPrefix;
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

    public function updateGuestCount($booking) {
        $count = $_POST['data']['count'];
        foreach($booking->rooms as $room) {
            if(in_array($room->pmsBookingRoomId, $_POST['data']['rooms'])) {
                $room->numberOfGuests = $count;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }

}
?>

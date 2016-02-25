<?php
namespace ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a;

class PmsManagement extends \WebshopApplication implements \Application {
    private $selectedBooking;
    private $types = null;
    private $users = array();
    public $errors = array();
    private $checkedCanAdd = array();
    public $roomTable = "";
    
    public function getDescription() {
        return "Administrate all your bookings from this application";
    }
    
    public function showBookingInformation() {
        $this->includefile("bookinginformation");
    }
    
    public function showInfoBoxForBookingAtTime() {
        $time = $_POST['data']['time'] / 1000;
        $itemId = $_POST['data']['itemid'];
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate($time-100);
        $filter->endDate = $this->convertToJavaDate($time+100);
        $filter->filterType = "active";
        
        $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
        foreach($bookings as $booking) {
            foreach($booking->rooms as $room) {
                if($room->bookingItemId == $itemId) {
                    $_POST['data']['bookingid'] = $booking->id;
                }
            }
        }
        if(isset($_POST['data']['bookingid'])) {
            $this->showBookingInformation();
        } else {
            echo "Booking not found";
        }
    }
    
    public function updateInvoiceNote() {
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        $booking->invoiceNote = $_POST['data']['invoicenote'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->showBookingInformation();
    }
    
    public function saveCompany() {
        $compid = $_POST['data']['companyid'];
        $current = $this->getApi()->getUserManager()->getCompany($compid);
        $current->vatNumber = $_POST['data']['vatNumber'];
        $current->name = $_POST['data']['name'];
        $current->email = $_POST['data']['email'];
        $current->invoiceEmail = $_POST['data']['invoiceEmail'];
        $current->prefix = $_POST['data']['prefix'];
        if(!$current->address) {
            $current->address = new \core_usermanager_data_Address();
        }
        $current->address->address = $_POST['data']['address.address'];
        $current->address->postCode = $_POST['data']['address.postCode'];
        $current->address->city = $_POST['data']['address.city'];
        if(!$current->invoiceAddress) {
            $current->invoiceAddress = new \core_usermanager_data_Address();
        }
        $current->invoiceAddress->address = $_POST['data']['invoiceAddress.address'];
        $current->invoiceAddress->postCode = $_POST['data']['invoiceAddress.postCode'];
        $current->invoiceAddress->city = $_POST['data']['invoiceAddress.city'];
        
        $this->getApi()->getUserManager()->saveCompany($current);
        $this->showBookingInformation();
    }
    
    public function addComment() {
        $id = $_POST['data']['bookingid'];
        $comment = $_POST['data']['comment'];
        $this->getApi()->getPmsManager()->addComment($this->getSelectedName(), $id, $comment);
        $this->showBookingInformation();
    }
    
    public function changeCompanyOnUser() {
        $booking = $this->getSelectedBooking();
        $newCompany = null;
        $compid = $_POST['data']['companyid'];
        if($compid == "newcompany") {
            $newCompany = new \core_usermanager_data_Company();
            $newCompany->name = "New company";
            $newCompany = $this->getApi()->getUserManager()->saveCompany($newCompany);
        } else if($compid) {
            $newCompany = $this->getApi()->getUserManager()->getCompany($_POST['data']['companyid']);
        }
        $curuser = $this->getApi()->getUserManager()->getUserById($booking->userId);
        $curuser->company = array();
        if($newCompany) {
            $curuser->company[] = $newCompany->id;
        }
        $this->getApi()->getUserManager()->saveUser($curuser);
        $this->showBookingInformation();
        echo "<script>";
        echo '$(".PmsManagement .editcompanybox").fadeIn(function() {$(".editcompanybox select").chosen(); });';
        echo "</script>";
    }
    
    public function changeBookingOnEvent() {
        $booking = $this->getSelectedBooking();
        if($_POST['data']['userid'] == "newuser") {
            $user = new \core_usermanager_data_User();
            $user->fullName = "New user";
            $newUser = $this->getApi()->getUserManager()->createUser($user);
            $booking->userId = $newUser->id;
        } else {
            $booking->userId = $_POST['data']['userid'];
        }
        
//        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->showBookingInformation();

        echo "<script>";
        echo "$('.edituserbox').show();";
        echo "</script>";
        
    }
    
    public function saveUser() {
        $selected = $this->getSelectedBooking();
        $user = $this->getApi()->getUserManager()->getUserById($selected->userId);
        $user->fullName = $_POST['data']['fullName'];
        $user->prefix = $_POST['data']['prefix'];
        $user->emailAddress = $_POST['data']['emailAddress'];
        $user->cellPhone = $_POST['data']['cellPhone'];
        if(!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        $user->address->address = $_POST['data']['address.address'];
        $user->address->postCode = $_POST['data']['address.postCode'];
        $user->address->city = $_POST['data']['address.city'];
        $this->getApi()->getUserManager()->saveUser($user);
        $this->showBookingInformation();
    }
    
    public function includeEventCalendar($bookingId) {
        $instances = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstances("27e174dc-b08c-4bf7-8179-9ea8379c91da");
        if(!$instances) {
            $instances = array();
        }
        foreach($instances as $instance) {
            if(isset($instance->settings->{"engine_name"})) {
                if($instance->settings->{"engine_name"}->value == $this->getSelectedName()) {
                    $app = $this->getFactory()->getApplicationPool()->createAppInstance($instance);
                    $app->renderInBookingManagement($bookingId);
                    break;
                }
            }
        }
    }
    
    public function canBeUsed($item, $start, $end) {
        $end = strtotime($end);
        $start = strtotime($start);
        $key = $item."_".$start."_".$end;
        
        if(isset($this->checkedCanAdd[$key])) {
            return $this->checkedCanAdd[$key];
        }
        
        
        $res = $this->getApi()->getBookingEngine()->isItemAvailable($this->getSelectedName(), 
                $item, 
                $this->convertToJavaDate($start), 
                $this->convertToJavaDate($end));
        
        $this->checkedCanAdd[$key] = $res;
        return $this->checkedCanAdd[$key];
    }
    
    public function addRoomToBooking() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $bookingId = $_POST['data']['bookingid'];
        $type = $_POST['data']['item'];
        
        $errors = $this->getApi()->getPmsManager()->addBookingItemType($this->getSelectedName(), $bookingId, $type, $start, $end);
        if($errors) {
            $this->errors[] = $errors;
        }
        $this->showBookingInformation();
    }
    
    public function removeRoom() {
        $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedName(),$_POST['data']['bookingid'], $_POST['data']['roomid']);
        $this->showBookingInformation();
    }
    
    public function addTypeFilter() {
        $filter = $this->getSelectedFilter();
        $id = $_POST['data']['id'];
        
        $newfilter = array();
        
        
        if(!$filter->typeFilter || !isset($filter->typeFilter)) {
            $filter->typeFilter= array();
        }
        $add = true;
        foreach($filter->typeFilter as $tmpid) {
            if($id == $tmpid) {
                $add = false;
            } else {
                $newfilter[] = $tmpid;
            }
        }
        if($add) {
            $newfilter[] = $id;
        }
        
        $filter->typeFilter = $newfilter;
        
        
        $_SESSION['pmfilter'][$this->getSelectedName()] = serialize($filter);
    }
    
    public function sortTable() {
        $filter = $this->getSelectedFilter();
        if($filter->sorting == $_POST['data']['column'] || !$filter->sorting) {
            $filter->sorting = $_POST['data']['column'] . "_desc";
        } else {
            $filter->sorting = $_POST['data']['column'];
        }
        
        $_SESSION['pmfilter'][$this->getSelectedName()] = serialize($filter);
    }

    public function getConfigurationToUse() {
        $booking = $this->getSelectedBooking();
        $defaultRule = null;
        foreach($booking->rooms as $room) {
            if(isset($room->item) && $room->item && $room->item->rules) {
                return $room->item->rules;
            }
            if(isset($room->type) && $room->type && $room->type->rules) {
                return $room->type->rules;
            }
        }
        
        return $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($this->getSelectedName());
    }

    
    public function resetnotifications() {
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        $booking->confirmed = false;
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room->started = false;
                $room->ended = false;
                $room->notificationsSent = array();
                $room->addedToArx = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
    }
    
    public function createNewOrder() {
        $filter = new \core_pmsmanager_NewOrderFilter();
        foreach($_POST['data'] as $name => $val) {
            $filter->{$name} = $val;
        }
        $bookingId = $_POST['data']['bookingid'];
        if(isset($_POST['data']['startingFrom'])) {
            $filter->startInvoiceFrom = $this->convertToJavaDate(strtotime($_POST['data']['startingFrom']));
        }
        if(isset($_POST['data']['endingAt'])) {
            $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($_POST['data']['endingAt']));
        }
        $filter->itemId = $_POST['data']['itemid'];
        
        $this->getManager()->createOrder($this->getSelectedName(), $bookingId, $filter);
        $this->showBookingInformation();
    }
    
    public function deleteBooking() {
        $id = $_POST['data']['bookingid'];
        $this->getApi()->getPmsManager()->deleteBooking($this->getSelectedName(), $id);
    }
    
    /**
     * @return core_bookingengine_data_BookingItemType[]
     */
    public function getTypes() {
        if($this->types) {
            return $this->types;
        }
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        $this->types = $this->indexList($types);
        return $this->types;
    }
    
    public function confirmBooking() {
        $id = $_POST['data']['bookingid'];
        $type = $_POST['data']['submit'];
        $message = $_POST['data']['message'];
        if($type == "confirm") {
            $this->getManager()->confirmBooking($this->getSelectedName(), $id, $message);
        } else {
            $this->getManager()->unConfirmBooking($this->getSelectedName(), $id, $message);
        }
    }
    
    public function setItemType() {
        $error = $this->getManager()->setNewRoomType($this->getSelectedName(), $_POST['data']['roomid'], $_POST['data']['bookingid'], $_POST['data']['itemtype']);
        if($error) {
            $this->errors[] = $error;
        }
        $this->showBookingInformation();
    }
    
    public function setNewStartDate() {
        $room = $this->getRoomFromPost();
        $error = $this->getManager()->changeDates($this->getSelectedName(),
                $_POST['data']['roomid'], 
                $_POST['data']['bookingid'], 
                $this->convertToJavaDate(strtotime($_POST['data']['start'])),
                $this->convertToJavaDate(strtotime($room->date->end)));
        if($error) {
            $this->errors[] = $error;
        }
        $this->showBookingInformation();
    }
    
    public function setNewEndDate() {
        $room = $this->getRoomFromPost();
        $error = $this->getManager()->changeDates($this->getSelectedName(),
                $_POST['data']['roomid'], 
                $_POST['data']['bookingid'], 
                $this->convertToJavaDate(strtotime($room->date->start)),
                $this->convertToJavaDate(strtotime($_POST['data']['end'])));
        if($error) {
            $this->errors[] = $error;
        }
        $this->showBookingInformation();
    }
    
    /**
     * @return \core_pmsmanager_PmsBooking
     */
    public function getSelectedBooking() {
        if($this->selectedBooking) {
            return $this->selectedBooking;
        }
        $bookingid = $_POST['data']['bookingid'];
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $bookingid);
        $this->selectedBooking = $booking;
        return $booking;
    }
    
    public function getName() {
        return "PmsManagement";
    }

    public function render() {
        if($this->isEditorMode()) {
            if(!$this->getSelectedName()) {
                echo "No booking engine name set yet. please set it first";
                return;
            }
            $this->includefile("managementview");
        }
    }
    
    public function setFilter() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $filter->endDate = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $filter->filterType = $_POST['data']['filterType'];
        $filter->state = 0;
        $filter->searchWord = $_POST['data']['searchWord'];
        
        $_SESSION['pmfilter'][$this->getSelectedName()] = serialize($filter);
    }
    
    public function emptyfilter() {
        unset($_SESSION['pmfilter'][$this->getSelectedName()]);
        unset($_SESSION['pmsmanagementgroupbybooking']);        
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function groupByBooking() {
        if(isset($_SESSION['pmsmanagementgroupbybooking'])) {
            unset($_SESSION['pmsmanagementgroupbybooking']);
        } else {
            $_SESSION['pmsmanagementgroupbybooking'] = true;
        }
    }
    
    public function isGroupedByBooking() {
        if(isset($_SESSION['pmsmanagementgroupbybooking'])) {
            return true;
        }
        return false;
    }
    
    /**
     * @return \core_pmsmanager_PmsBookingFilter
     */
    public function getSelectedFilter() {
        if(!isset($_POST['event'])) {
            unset($_SESSION['pmfilter'][$this->getSelectedName()]);
        }
        if(isset($_SESSION['pmfilter'][$this->getSelectedName()])) {
            return unserialize($_SESSION['pmfilter'][$this->getSelectedName()]);
        }
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->state = 0;
        $filter->startDate = $this->formatTimeToJavaDate(time()-(86400*3));
        $filter->endDate = $this->formatTimeToJavaDate(time()+(86400*3));
        $filter->sorting = "regdate";
        return $filter;
    }

    public function getManager() {
        return $this->getApi()->getPmsManager();
    }
    
    public function setBookingItem() {
        $error = $this->getManager()->setBookingItem($this->getSelectedName(),
                $_POST['data']['roomid'], 
                $_POST['data']['bookingid'], 
                $_POST['data']['itemid']);
        if($error) {
            $this->errors[] = $error;
        }
        $this->selectedBooking = $this->getManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        $this->showBookingInformation();
    }
    
    public function changePrice() {
        $booking = $this->getSelectedBooking();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room->price = $_POST['data']['price'];
            }
        }
        
        $this->getManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = $this->getManager()->getBooking($this->getSelectedName(), $booking->id);
        $this->showBookingInformation();
    }
    
    public function changePriceType() {
        $booking = $this->getSelectedBooking();
        $booking->priceType = $_POST['data']['pricetype'];
        $this->getManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = $this->getManager()->getBooking($this->getSelectedName(), $booking->id);
        $this->showBookingInformation();
    }
    
    public function setNewPaymentType() {
        $booking = $this->getSelectedBooking();
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        $user->preferredPaymentType = $_POST['data']['newtype'];
        $this->getApi()->getUserManager()->saveUser($user);
        $this->showBookingInformation();
    }
    
    public function setGuests() {
        $guests = array();
        for($i = 0; $i < $_POST['data']['numberofguests']; $i++) {
            $guest = new \core_pmsmanager_PmsGuests();
            $guest->name = $_POST['data']['name_'.$i];
            $guest->email = $_POST['data']['email_'.$i];
            $guest->phone = $_POST['data']['phone_'.$i];
            $guest->prefix = $_POST['data']['prefix_'.$i];
            $guests[] = $guest;
        }
        
        $booking = $this->getSelectedBooking();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room->guests = $guests;
                $room->numberOfGuests = $_POST['data']['numberofguests'];
            }
        }
        
        $this->getManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = $this->getManager()->getBooking($this->getSelectedName(), $booking->id);
        $this->showBookingInformation();
    }

    public function translatePaymenttype($type, $paymentTypes) {
        foreach($paymentTypes as $paymentType) {
            $idString = str_replace("-", "_", $paymentType->id);
            if(stristr($type, $idString)) {
                return $paymentType->appName;
            }
        }
        return "";
    }

    public function translateText($type) {
        return substr($type, strpos($type, "\\")+1);
    }

    /**
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getRoomFromPost() {
        $booking =  $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid'])
                return $room;
        }
        return null;

    }
    
    public function updateItemList() {
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $this->includeAddRoomOptions($items, $start, $end);
    }

    public function includeAddRoomOptions($items, $start, $end) {
        $types = $this->getTypes();
        ?>
            <select style='float:left; margin-right: 10px;' gsname='item' class='addroomselectiontype'>
                <?php 
                foreach($types as $type) {
                    /* @var $item core_bookingengine_data_BookingItem */
                    $number = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedName(), $type->id, $start, $end);
                    if($number > 0) 
                        echo "<option value='".$type->id."'>". $type->name . "</option>";
                }
                ?>
            </select>
        <?php
    }
    
    public function showLog() {
        $filter = new \core_pmsmanager_PmsLog();
        $filter->bookingId = $_POST['data']['bookingid'];
        $log = $this->getApi()->getPmsManager()->getLogEntries($this->getSelectedName(), $filter);
        $this->printLog($log);
    }

    /**
     * @param \core_pmsmanager_PmsLog[] $entries
     */
    public function printLog($entries) {
        echo "<br><br>";
        echo "<table width='100%' cellspacing='0' cellpadding='0'>";
        echo "<tr>";
        echo "<th width='110'>Date</th>";
        echo "<th width='110'>User</th>";
        echo "<th>Logtext</th>";
        echo "</tr>";
        
        foreach($entries as $entry) {
            echo "<tr>";
            echo "<td valign='top'>" . date("d.m.Y H:i", strtotime($entry->dateEntry)) . "</td>";
            echo "<td valign='top'>" . $this->getUsersName($entry->userId) . "</td>";
            echo "<td valign='top'>" . $entry->logText . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }

    public function getUsersName($userId) {
        if(!$userId) {
            return "";
        }
        
        if(isset($this->users[$userId])) {
            return $this->users[$userId]->fullName;
        }
        
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $this->users[$userId] = $user;
        return $user->fullName;
    }

    
    /**
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getFirstBookedRoom() {
        $booking = $this->getSelectedBooking();
        if(isset($booking->rooms[0])) {
            return $booking->rooms[0];
        } else {
            return new \core_pmsmanager_PmsBookingRooms();
        }
    }

    public function getRepeatingSummary() {
        $repeat = new \ns_46b52a59_de5d_4878_aef6_13b71af2fc75\PmsBookingSummary();
        $repeat->curBooking = $this->getSelectedBooking();
        return $repeat->getRepeatingSummary();
    }
    
    
    public function addRepeatingDates() {
        $repeat = new \ns_46b52a59_de5d_4878_aef6_13b71af2fc75\PmsBookingSummary();
        $data = $repeat->createRepeatingDateObject();
        $bookingId = $this->getSelectedBooking()->id;
        $rooms = $this->getApi()->getPmsManager()->updateRepeatingDataForBooking($this->getSelectedName(), $data, $bookingId);
        if(!$rooms) {
            $rooms = array();
        }
        $roomTable = "<h1>The following dates could not be added due to fully booked</h1>";
        $roomTable .= "<table cellspacing='0' cellpadding='0' width='100%'>";
        $roomTable .= "<tr>";
        $roomTable .= "<th>Start date</th>";
        $roomTable .= "<th>End date</th>";
        $roomTable .= "</tr>";
        
        $hasDaysCantBeAdded = false;
        foreach($rooms as $room) {
            if(!$room->canBeAdded) {
                continue;
            }
            $hasDaysCantBeAdded = true;
            $roomTable .= "<tr>";
            $roomTable .= "<td>" . date("d.m.Y H:i", strtotime($room->date->start)) . "</td>";
            $roomTable .= "<td>" . date("d.m.Y H:i", strtotime($room->date->end)) . "</td>";
            $roomTable .= "</tr>";
        }
        $roomTable .= "</table>";
        
        if(!$hasDaysCantBeAdded) {
            $roomTable = "";
        }
        $this->roomTable = $roomTable;
        $this->selectedBooking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $bookingId);
        $this->showBookingInformation();
    }

    /**
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @param \core_pmsmanager_PmsBookingFilter $filter
     */
    public function isActive($room, $filter) {
        
        if(!isset($filter->filterType) || $filter->filterType == "registered" || $filter->filterType == "deleted" || $filter->filterType == "stats" || $filter->filterType == "unconfirmed") {
            return true;
        }
        
        //Same start date
        if($this->sameDay($room->date->start, $filter->startDate)) {
            return true;
        }
        
        //Same end date
        if($this->sameDay($room->date->start, $filter->endDate)) {
            return true;
        }
        
        //Same start date
        if($this->sameDay($room->date->end, $filter->startDate)) {
            return true;
        }
        
        //Same end date
        if($this->sameDay($room->date->end, $filter->endDate)) {
            return true;
        }
        
        $filterStart = strtotime($filter->startDate);
        $filterEnd = strtotime($filter->endDate);
        
        $roomStart = strtotime($room->date->start);
        $roomEnd = strtotime($room->date->end);
        
        //Start in periode selected
        if($roomStart > $filterStart && $roomStart < $filterEnd) {
            return true;
        }
        
        //Ends in periode selected
        if($roomEnd > $filterStart && $roomEnd < $filterEnd) {
            return true;
        }
        
        //Expands the whole periode.
        if($filter->filterType == "active") {
            if($roomStart < $filterStart && $roomEnd > $filterEnd) {
                return true;
            }
        }

        return false;
    }

    public function sameDay($time1, $time2) {
        return date("dmy", strtotime($time1)) == date("dmy", strtotime($time2));
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBooking $booking
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @param \core_pmsmanager_PmsBookingFilter $filter
     * @return boolean
     */
    public function containsSearchWord($row, $filter) {
        if(!isset($filter->searchWord) || !$filter->searchWord) {
            return true;
        }
        
        return stristr($row, $filter->searchWord) !== false;
    }

}
?>

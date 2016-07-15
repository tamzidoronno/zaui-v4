<?php
namespace ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a;

class PmsManagement extends \WebshopApplication implements \Application {
    private $selectedBooking;
    private $types = null;
    private $items = null;
    private $channels = null;
    private $users = array();
    public $errors = array();
    private $checkedCanAdd = array();
    public $roomTable = "";
    
    public function runProcessor() {
        $this->getApi()->getPmsManager()->processor($this->getSelectedName());
    }
    
    public function massUpdatePrices() {
        $bookingId = $_POST['data']['bookingid'];
        $prices = new \core_pmsmanager_PmsPricing();
        $prices->price_mon = $_POST['data']['price_mon'];
        $prices->price_tue = $_POST['data']['price_tue'];
        $prices->price_wed = $_POST['data']['price_wed'];
        $prices->price_thu = $_POST['data']['price_thu'];
        $prices->price_fri = $_POST['data']['price_fri'];
        $prices->price_sat = $_POST['data']['price_sat'];
        $prices->price_sun = $_POST['data']['price_sun'];
        
        $this->getApi()->getPmsManager()->massUpdatePrices($this->getSelectedName(), $prices, $bookingId);
        $this->showBookingInformation();
    }
    
    public function updateOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->status = $_POST['data']['status'];
        $order->payment->paymentType = $_POST['data']['paymenttype'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->showBookingInformation();
    }
    
    public function creditOrder() {
    $this->getApi()->getPmsInvoiceManager()->creditOrder($this->getSelectedName(), $_POST['data']['bookingid'], $_POST['data']['orderid']);
        $this->showBookingInformation();
    }
    
    public function loadDayStatistics() {
        $type = $_POST['data']['type'];
        $day = $_POST['data']['day'] . "00:00";
        if($type == "coverage") {
            $filter = new \core_pmsmanager_PmsBookingFilter();
            $filter->filterType = "active";
            $filter->startDate = $this->convertToJavaDate(strtotime($day));
            $filter->endDate = $this->convertToJavaDate(strtotime($day));

            $bookings = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedName(), $filter);
            $this->printSimpleRoomTable($bookings);
        } else {
            $filterOptions = new \core_common_FilterOptions();
            $filterOptions->startDate = $this->convertToJavaDate(strtotime($day));
            $filterOptions->endDate = $this->convertToJavaDate(strtotime($day) + 86400);
            $orders = $this->getApi()->getOrderManager()->getOrdersFiltered($filterOptions);
            $this->printOrderTable($orders->datas);
        }
    }
    
    
    public function saveCardOnRoom() {
        $roomid = $_POST['data']['roomid'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $roomid);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomid) {
                $room->code = $_POST['data']['code'];
                $room->cardformat = $_POST['data']['cardtype'];
                $room->addedToArx = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
    }
    
    public function loadTakenRoomList() {
        $bookingid = $_POST['data']['bookingid'];
        $roomid = $_POST['data']['roomid'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $roomid);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomid) {
                $start = $room->date->start;
                $end = $room->date->end;
                
                $filter = new \core_pmsmanager_PmsBookingFilter();
                $filter->filterType = "active";
                $filter->startDate = $start;
                $filter->endDate = $end;
                
                $allRooms = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedName(), $filter);
                $toPrint = "";
                foreach($allRooms as $takenroom) {
                    if($takenroom->bookingItemId == $_POST['data']['itemid']) {
                        $toPrint .= $takenroom->owner . " - " . date("d.m.Y H:i", $takenroom->start / 1000) . " - " . date("d.m.Y H:i", $takenroom->end / 1000) . "<br>";
                    }
                }
                if($toPrint) {
                    echo "<i class='fa fa-close' style='float:right; cursor:pointer;' onclick=\"$('.tiparea').hide()\"></i>";
                    echo "<b>Item is taken by</b><br>";
                    echo $toPrint;
                }
            }
        }
    }
    
    public function sendPaymentLink() {
        $orderid = $_POST['data']['orderid'];
        $bookingid = $_POST['data']['bookingid'];
        $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedName(), $orderid, $bookingid);
    }
    
    public function getDescription() {
        return "Administrate all your bookings from this application";
    }
    
    public function changeInvoicedTo() {
        $this->getApi()->getPmsManager()->changeInvoiceDate($this->getSelectedName(), $_POST['data']['roomid'], $this->convertToJavaDate(strtotime($_POST['data']['newdate'])));
        $this->showBookingInformation();
    }
    
    public function addAddon() {
        $type = $_POST['data']['type'];
        $bookingId = $_POST['data']['bookingid'];
        $roomId = $_POST['data']['roomId'];
        $added = $_POST['data']['remove'];

        $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedName(), $type, $bookingId, $roomId, $added);
        $this->showBookingInformation();
    }
    
    public function globalInvoiceCreation() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->onlyEnded = false;
        $filter->prepayment = $config->prepayment;
        if($_POST['data']['preview'] == "true") {
            $filter->avoidOrderCreation = true;
        }
        if($_POST['data']['type'] == "ended") {
            $filter->onlyEnded = true;
        }
        
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($_POST['data']['enddate']));
        $this->getApi()->getPmsManager()->createOrder($this->getSelectedName(), null, $filter);
    }
    
    public function includeOrderGenerationPreview() {
        $this->includefile("ordergenerationpreview");
    }
    
    public function showBookingInformation() {
        $this->includefile("bookinginformation");
    }
    
    public function showBookingOnBookingEngineId() {
        $bid = $_POST['data']['bid'];
        
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedName(), $bid);
        
        $_POST['data']['bookingid'] = $booking->id;
                    
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
    
    public function setNewInterval() {
        $this->getApi()->getPmsManager()->setNewCleaningIntervalOnRoom($this->getSelectedName(), 
                $_POST['data']['roomid'], 
                (int)$_POST['data']['interval']);
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
        $selected->userId = $_POST['data']['userid'];
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->fullName = $_POST['data']['fullName'];
        $user->prefix = $_POST['data']['prefix'];
        $user->emailAddress = $_POST['data']['emailAddress'];
        $user->cellPhone = $_POST['data']['cellPhone'];
        $user->accountingId = $_POST['data']['accountingId'];
        if(!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        $user->address->address = $_POST['data']['address.address'];
        $user->address->postCode = $_POST['data']['address.postCode'];
        $user->address->city = $_POST['data']['address.city'];
        $user->birthDay = $_POST['data']['birthDay'];
        $this->getApi()->getUserManager()->saveUser($user);
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $selected);
        $this->selectedBooking = null;
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
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " " . $_POST['data']['starttime']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " " . $_POST['data']['endtime']));
        $bookingId = $_POST['data']['bookingid'];
        $type = $_POST['data']['item'];
        
        $errors = $this->getApi()->getPmsManager()->addBookingItemType($this->getSelectedName(), $bookingId, $type, $start, $end);
        if($errors) {
            $this->errors[] = $errors;
        }
        $this->showBookingInformation();
    }
    
    public function removeRoom() {
        $error = $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedName(),$_POST['data']['bookingid'], $_POST['data']['roomid']);;
        if($error) {
            $this->errors[] = $error;
        }
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
//        $booking->confirmed = false;
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
//                $room->started = false;
//                $room->ended = false;
                $room->notificationsSent = array();
//                $room->addedToArx = false;
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
        if(isset($_POST['data']['endingAt'])) {
            $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($_POST['data']['endingAt']));
        }
        $filter->avoidOrderCreation = $_POST['data']['preview'] == "true";
        $filter->itemId = $_POST['data']['itemid'];
        $filter->prepayment = true;
        
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
                $this->convertToJavaDate(strtotime($_POST['data']['start']. " " . $_POST['data']['starttime'])),
                $this->convertToJavaDate(strtotime($_POST['data']['end']. " " . $_POST['data']['endtime'])));
        if(!$error) {
            $this->errors[] = "Could not update start date, due to room not available at the time being.";
        }
        
        $this->refreshSelectedBooking();
        $this->showBookingInformation();
    }
    
    public function markKeyReturned() {
        $this->getApi()->getPmsManager()->returnedKey($this->getSelectedName(), $_POST['data']['roomid']);
        $this->showBookingInformation();
    }
    
    public function sendMessage() {
        $email = $_POST['data']['email'];
        $title = $_POST['data']['title'];
        $message = $_POST['data']['message'];
        $bookingId = $_POST['data']['bookingid'];
        $this->getApi()->getPmsManager()->sendMessage($this->getSelectedName(), $bookingId, $email, $title, $message);
        $this->showBookingInformation();
    }
    
    public function setNewEndDate() {
        $room = $this->getRoomFromPost();
        $error = $this->getManager()->changeDates($this->getSelectedName(),
                $_POST['data']['roomid'], 
                $_POST['data']['bookingid'], 
                $this->convertToJavaDate(strtotime($room->date->start)),
                $this->convertToJavaDate(strtotime($_POST['data']['end'])));
        if(!$error) {
            $this->errors[] = "Could not update end date, due to room not available at the time being.";
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
    
    public function exportBookingStats() {
        $stats = $this->getManager()->getStatistics($this->getSelectedName(), $this->getSelectedFilter());
        $arr = (array)$stats->entries;
        array_unshift($arr, array_keys((array)$arr[0]));
        echo json_encode($arr);
    }
    
    public function exportSaleStats() {
        $stats = $this->getManager()->getStatistics($this->getSelectedName(), $this->getSelectedFilter());
        $arr = (array)$stats->salesEntries;
        foreach($arr as $a => $k) {
            unset($k->{'paymentTypes'});
        }
        array_unshift($arr, array_keys((array)$arr[0]));
        echo json_encode($arr);
    }
    
    public function exportBookingDataToExcel() {
        $filter = $this->getSelectedFilter();
        $res = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedName(), $filter);
        if(!$res) {
            echo "[]";
            return;
        }
        $export = array();
        foreach($res as $room) {
            $exportLine = array();
            foreach($room as $k => $val) {
                if(is_bool($val)) {
                    if($val) {
                        $val = "yes";
                    } else {
                        $val = "no";
                    }
                }
                
                if($k == "guest" || $k == "addons") {
                    $val = json_encode($val);
                    $exportLine[$k] = $val;
                } else {
                    $exportLine[$k] = $val;
                }
            }
            $export[] = $exportLine;
        }
        
        array_unshift($export, array_keys($export[0]));
        echo json_encode($export);
    }
    
    public function setFilter() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " 00:00"));
        $filter->endDate = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " 23:59"));
        $filter->filterType = $_POST['data']['filterType'];
        $filter->state = 0;
        $filter->searchWord = $_POST['data']['searchWord'];
        if(isset($_POST['data']['channel'])) {
            $filter->channel = $_POST['data']['channel'];
        }
        
        $this->setCurrentFilter($filter);
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
        $filter->startDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 00:00", time()))-(86400*3));
        $filter->endDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 23:59", time()))+(86400*3));
        $filter->sorting = "regdate";
        $filter->includeDeleted = true;
        return $filter;
    }

    public function changeTimeView() {
        $filter = $this->getSelectedFilter();
        $filter->timeInterval = $_POST['data']['view'];
        $this->setCurrentFilter($filter);
    }
    
    public function getManager() {
        return $this->getApi()->getPmsManager();
    }
    
    public function setBookingItem() {
        $error = $this->getManager()->setBookingItem($this->getSelectedName(),
                $_POST['data']['roomid'], 
                $_POST['data']['bookingid'], 
                $_POST['data']['itemid'],
                $_POST['data']['clicksubmit'] == "split");
        if($error) {
            $this->errors[] = $error;
        }
        $this->selectedBooking = $this->getManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        $this->showBookingInformation();
    }
    
    public function checkInGuest() {
        $booking = $this->getSelectedBooking();
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                $r->checkedin = true;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        $this->showBookingInformation();
    }
    
    public function checkOutGuest() {
        $booking = $this->getSelectedBooking();
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                $r->checkedout = true;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        $this->showBookingInformation();
    }

    public function changePrice() {
        $booking = $this->getSelectedBooking();
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                if($_POST['data']['pricematrix'] == "no") {
                    $room->price = $_POST['data']['price'];
                } else {
                    foreach($_POST['data'] as $key => $val) {
                        if(stristr($key, "matrixprice_")) {
                            $time = str_replace("matrixprice_", "", $key);
                            $pricematrix[$time] = $val;
                        }
                    }
                    ksort($pricematrix);
                    $newmatrix = array();
                    $avg = 0;
                    foreach($pricematrix as $k => $val) {
                        $newmatrix[date("d-m-Y",$k)] = $val;
                        $avg += $val;
                    }
                    $room->priceMatrix = $newmatrix;
                    $room->price = $avg / sizeof($pricematrix);
                }
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
            $guest->prefix = str_replace("+", "", $guest->prefix);
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

    public function createPaymentTypeText($app) {
        $idString = str_replace("-", "_", $app->id);
        return "ns_" . $idString ."\\" . $app->appName;
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
        $default = $_POST['data']['selectedtype'];
        $this->includeAddRoomOptions($items, $start, $end, $default);
    }

    public function includeAddRoomOptions($items, $start, $end, $defaultType) {
        $types = $this->getTypes();
        ?>
            <select style='margin-right: 10px;' gsname='item' class='addroomselectiontype'>
                <?php 
                foreach($types as $type) {
                    /* @var $item core_bookingengine_data_BookingItem */
                    $number = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedName(), $type->id, $start, $end);
                    $selected = "";
                    if($type->id == $defaultType) {
                        $selected = "SELECTED";
                    }
                    if($number > 0) {
                        echo "<option value='".$type->id."' $selected>". $type->name . "</option>";
                    } else {
                        echo "<option value='".$type->id."' $selected>". $type->name . " (".$this->__w("Occupied").") </option>";
                    }
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
        echo "<th width='110'></th>";
        echo "</tr>";
        
        foreach($entries as $entry) {
            echo "<tr>";
            echo "<td valign='top'>" . date("d.m.Y H:i", strtotime($entry->dateEntry)) . "</td>";
            echo "<td valign='top'>" . $this->getUsersName($entry->userId) . "</td>";
            echo "<td valign='top'>" . $entry->logText . "</td>";
            $item = "";
            if($entry->bookingItemId) {
                $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $entry->bookingItemId)->bookingItemName;
            }
            echo "<td valign='top'>" . $item . "</td>";
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
    
    public function hasAccountingTransfer() {
        $app = $this->getApi()->getStoreApplicationPool()->getApplication("932810f4-5bd1-4b56-a259-d5bd2e071be1");
        return $app;
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
     * @param \core_pmsmanager_PmsBooking $booking
     */
    public function isActive($room, $filter, $booking) {
        if(!$room || !isset($room->date)) {
            return true;
        }

        if($room->deleted && @!$filter->includeDeleted) {
            return false;
        }
        
        if(isset($filter->searchWord) && $filter->searchWord) {
            return true;
        }
        
        $filterStart = strtotime($filter->startDate);
        $filterEnd = strtotime($filter->endDate);
        
        $roomStart = strtotime($room->date->start);
        $roomEnd = strtotime($room->date->end);
        
        if(!isset($filter->filterType) || $filter->filterType == "registered" || $filter->filterType == "deleted" || $filter->filterType == "stats" || $filter->filterType == "uncofirmed") {
            $roomStart = strtotime($booking->rowCreatedDate);
            $roomEnd = strtotime($booking->rowCreatedDate);
        }

        //Same start date
        if($this->sameDay($roomStart, $filter->startDate)) {
            return true;
        }
        
        //Same end date
        if($this->sameDay($roomStart, $filter->endDate)) {
            return true;
        }
        
        //Same start date
        if($this->sameDay($roomEnd, $filter->startDate)) {
            return true;
        }
        
        //Same end date
        if($this->sameDay($roomEnd, $filter->endDate)) {
            return true;
        }
        
        
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
        return date("dmy", $time1) == date("dmy", strtotime($time2));
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
        $tocheck = strtolower(strip_tags($row));
        return mb_stristr($tocheck, strtolower($filter->searchWord), false, "UTF-8");
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBooking $booking
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @return type
     */
    public function includeLegacyStuffForSemlagerhotell($booking, $room) {
        if($this->getFactory()->getStore()->id != "c444ff66-8df2-4cbb-8bbe-dc1587ea00b7") {
            return;
        }
        $bookingId = $booking->id;
        $userId = $booking->userId;
        $engine = $this->getSelectedName();
        echo "<span style='color:blue; cursor:pointer;' onClick=\"window.open('/scripts/generateContract.php?userid=$userId&bookingId=$bookingId&engine=$engine&roomid=".$room->pmsBookingRoomId."');\"><i class='fa fa-file-pdf-o'></i> kontrakt - </span>";
    }
    
    public function undeleteBooking() {
        $bookingId = $_POST['data']['bookingid'];
        $this->getApi()->getPmsManager()->undeleteBooking($this->getSelectedName(), $bookingId);
        $this->showBookingInformation();
    }

    public function includeDeleted() {
        $filter = $this->getSelectedFilter();
        $filter->includeDeleted = $_POST['data']['incdelete'] == "true";
        $_SESSION['pmfilter'][$this->getSelectedName()] = serialize($filter);
    }
    
    public function isIncludeDeleted() {
        return $this->getSelectedFilter()->includeDeleted;
    }

    /**
     * 
     * @param type $roomid
     * @return \core_pmsmanager_PmsBooking
     */
    public function findBookingFromRoom($roomid) {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoomIgnoreDeleted($this->getSelectedName(), $roomid);
        return $booking;
    }

    public function findUser($userId) {
        if(isset($this->users[$userId])) {
            return $this->users[$userId];
        }
        
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $this->users[$userId] = $user;
        return $user;
    }

    public function getItems() {
        if($this->items) {
            return $this->items;
        }
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        $this->items = $this->indexList($items);
        return $this->items;
    }
    
    public function updateAddons() {
        $addons = $_POST['data']['addons'];
        $bookingid = $_POST['data']['bookingid'];
        $toAdd = array();
        foreach($addons as $id => $addon) {
            $add = new \core_pmsmanager_PmsBookingAddonItem();
            $add->addonId = $id;
            $add->count = $addon['count'];
            $add->price = $addon['price'];
            $toAdd[] = $add;
        }
        $this->getApi()->getPmsManager()->updateAddons($this->getSelectedName(), $toAdd, $bookingid);
        $this->showBookingInformation();
    }

    public function refreshSelectedBooking() {
        $bookingid = $_POST['data']['bookingid'];
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $bookingid);
        $this->selectedBooking = $booking;
    }

    /**
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @param type $addonType
     */
    public function hasAddon($room, $addonType) {
        foreach($room->addons as $addon) {
            if($addon->addonType == $addonType) {
                return true;
            }
        }
        return false;
    }
    
    public function splitToSingleBooking() {
        $roomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->splitBooking($this->getSelectedName(), $roomId);
        $this->showBookingInformation();
    }

    public function hasBreakfastAddon() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        foreach($config->addonConfiguration as $addon) {
            if($addon->addonType == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param type $id
     * @return \core_bookingengine_data_BookingItemType
     */
    public function getTypeById($id) {
        $types = $this->getTypes();
        return $types[$id];
    }

    /**
     * @param type $itemId
     * @return \core_bookingengine_data_BookingItem
     */
    public function getItemById($itemId) {
        $items = $this->getItems();
        if(isset($items[$itemId])) {
            return $items[$itemId];
        }
        return null;
    }

    public function getChannelMatrix() {
        $channels = $this->getApi()->getPmsManager()->getChannelMatrix($this->getSelectedName());
        if($this->channels) {
            return $this->channels;
        }
        $this->channels = $channels;
        return $channels;
    }

    public function getTotalSlept($saleStats) {
        $total = 0;
        foreach($saleStats->entries as $entry) {
            $total = $entry->{'totalPrice'};
        }
        return $total;
    }

    public function printSimpleRoomTable($bookings) {
        echo "<table cellspacing='0' cellpadding='0' width='100%'>";
        echo "<tr>";
        echo "<th>Room</th>";
        echo "<th>Owner</th>";
        echo "<th>Checkin</th>";
        echo "<th>Checkout</th>";
        echo "<th>Daily price</th>";
        echo "<th>State</th>";
        echo "</tr>";
        
        $totalprice = 0;
        foreach($bookings as $booking) {
            /* @var $booking \core_pmsmanager_PmsRoomSimple */
            $price = round($booking->price);
            $totalprice += $price;
            echo "<tr class='moreinformationaboutbooking' style='cursor:pointer;' bookingid='".$booking->bookingId."'>";
            echo "<td>" . $booking->room . "</td>";
            echo "<td>" . $booking->owner . "</td>";
            echo "<td>" . date("d.m.Y", $booking->start/1000) . "</td>";
            echo "<td>" . date("d.m.Y", $booking->end/1000) . "</td>";
            echo "<td>" . $price . "</td>";
            echo "<td>" . $booking->progressState . "</td>";
            echo "</tr>";
        }
        echo "<tr>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td>$totalprice</td>";
        echo "<td></td>";
        echo "</tr>";
        echo "</table>";
    }

    public function printOrderTable($orders) {
        echo "<table cellspacing='0' cellpadding='0' width='100%'>";
        echo "<tr>";
        echo "<th width='10'>Orderid</th>";
        echo "<th>Date</th>";
        echo "<th>Owner</th>";
        echo "<th>Orderlines</th>";
        echo "<th>Total</th>";
        echo "<th>Status</th>";
        echo "</tr>";
        
        $states[1] = "Created";
        $states[2] = "Waiting for payment";
        $states[3] = "Payment failed";
        $states[4] = "Completed";
        $states[5] = "Canceled";
        $states[6] = "Sent";
        $states[7] = "Payment completed";
        $states[8] = "Collection failed";
        $states[9] = "Need collecting";
        $states[10] = "Send to invoice";
        
        $totalprice = 0;
        foreach($orders as $order) {
            if($order->status != 7) {
                continue;
            }
            if($order->testOrder) {
                continue;
            }
            $price = $this->getApi()->getOrderManager()->getTotalAmountExTaxes($order);
            $totalprice += $price;
            echo "<tr >";
            echo "<td width='10'>" . $order->incrementOrderId . "</td>";
            echo "<td>" . date("d.m.Y", strtotime($order->rowCreatedDate)) . "</td>";
            echo "<td>" . $this->getApi()->getUserManager()->getUserById($order->userId)->fullName  . "</td>";
            echo "<td>" . sizeof($order->cart->items) . "</td>";
            echo "<td>" . round($price) . "</td>";
            echo "<td>" . $states[$order->status] . "</td>";
            echo "</tr>";
        }
        echo "<tr>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td>".round($totalprice)."</td>";
        echo "<td></td>";
        echo "</tr>";
        echo "</table>";
    }

    public function getCreatedOrders() {
        $items = array();
        
        
        $cartmgr = $this->getApi()->getCartManager();
        $cart = $cartmgr->getCart();

        $totalEx = 0;
        $total = 0;
        $totalCount = 0;
        foreach($cart->items as $item) {
            $booking = $this->findBookingFromRoom($item->product->externalReferenceId);
            $user = $this->findUser($booking->userId);

            $room = "";
            foreach($booking->rooms as $r) {
                if($r->pmsBookingRoomId == $item->product->externalReferenceId) {
                    $room = $r;
                }
            }
            $priceEx = round($item->product->priceExTaxes);
            $price = round($item->product->price, 1);
            $totalEx += ($priceEx * $item->count);
            $total += ($price * $item->count);
            $totalCount += $item->count;
            $res = array();
            $res['id'] = $booking->id;
            $res['start'] = date("d.m.Y H:i", strtotime($room->date->start));
            $res['end'] = date("d.m.Y H:i", strtotime($room->date->end));
            $res['fullname'] = $user->fullName;
            $res['invoicedto'] = date("d.m.Y H:i", strtotime($room->invoicedTo));
            $res['additionalmetadata'] = $item->product->additionalMetaData;
            $res['itemstart'] = date("d.m.Y H:i", strtotime($item->startDate));
            $res['itemend'] = date("d.m.Y H:i", strtotime($item->endDate));
            $res['productname'] = $item->product->name;
            $res['metadata'] = $item->product->metaData;
            $res['price'] = $price;
            $res['priceex'] = $priceEx;
            $res['roomprice'] = $room->price;
            $res['count'] = $item->count;
            $items[] = $res;
        }
        
        $totalarr = array();
        $totalarr['id'] = "";
        $totalarr['start'] = "";
        $totalarr['end'] = "";
        $totalarr['fullname'] = "Total";
        $totalarr['invoicedto'] = "";
        $totalarr['additionalmetadata'] = "";
        $totalarr['itemstart'] = "";
        $totalarr['itemend'] = "";
        $totalarr['productname'] = "";
        $totalarr['metadata'] = "";
        $totalarr['price'] = $total;
        $totalarr['priceex'] = $totalEx;
        $totalarr['roomprice'] = "";
        $totalarr['count'] = $totalCount;
        
        $items[] = $totalarr;
        return $items;
    }
    
    public function getOrderInvoiceArray() {
        $arr = $this->getCreatedOrders();
        array_unshift($arr, array_keys($arr[0]));
       echo json_encode($arr);
    }

    public function setCurrentFilter($filter) {
        $_SESSION['pmfilter'][$this->getSelectedName()] = serialize($filter);
    }

    public function getDayText($date, $timeinterval) {
        if($timeinterval == "monthly") { return date("m.Y", strtotime($date)); }
        if($timeinterval == "yearly") { return date("Y", strtotime($date)); }
        if($timeinterval == "weekly") { return date("d.m.Y", strtotime($date)) . "<br>" . date("d.m.Y", strtotime($date)+(86400*7)); }
        return date("d.m.Y", strtotime($date));
    }

    public function isPaymentType($type, $app) {
        $id = str_replace("-", "_", $app->id);
        if(stristr($type, $id)) {
            return true;
        }
        return false;
    }

}
?>

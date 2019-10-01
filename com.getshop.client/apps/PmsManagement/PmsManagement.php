<?php
namespace ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a;

class PmsManagement extends \WebshopApplication implements \Application {
    private $selectedBooking;
    private $ordersOnBooking;
    private $types = null;
    private $items = null;
    private $channels = null;
    private $users = array();
    public $errors = array();
    public $selectedUser;
    private $checkedCanAdd = array();
    public $roomTable = "";
    public $fastAddedCode = null;
    private $fetchedBookings = array();
    public $showBookersData = false;
    public $config;
    
    public function loadBookingOrdersRoom() {
        $this->includefile("ordersforroom");
    }
    
    public function toggleDeleteComment() {
        $booking = $this->getSelectedBooking();
        foreach($booking->comments as $key => $val) {
            if($key == $_POST['data']['time']) {
                $booking->comments->{$key}->deleted = !$booking->comments->{$key}->deleted;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
    }

    public function completeQuickReservation() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        $currentBooking->userId = "quickreservation";
        $currentBooking->quickReservation = true;
        $currentBooking->avoidCreateInvoice = true;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $currentBooking);
        $res = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedName());
        if(!$res) {
            echo "<div style='border: solid 1px; background-color:red; padding: 10px; font-size: 16px; color:#fff;'>";
            echo "<i class='fa fa-warning'></i> ";
            echo "Unable to comply, your selection is not possible.";
            echo "</div>";
            $this->loadReserveRoomInformation();
        } else {
            $_POST['data']['bookingid'] = $currentBooking->id;
            $this->showBookingInformation();
        }
        
    }
    
    public function changeTypeOnRoom() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());

        $newRoomList = array();
        foreach($currentBooking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                if($_POST['data']['typeid'] == "waiting_list") {
                    $room->bookingItemTypeId = "";
                    $room->addedToWaitingList = true;
                } else {
                    $room->bookingItemTypeId = $_POST['data']['typeid'];
                }
            }
        }
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $currentBooking);
        $this->loadReserveRoomInformation();
    }
    
    public function removeRoomFromCurrentBooking() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());

        $newRoomList = array();
        foreach($currentBooking->rooms as $room) {
            if($room->pmsBookingRoomId != $_POST['data']['clicksubmit']) {
                $newRoomList[] = $room;
            }
        }
        $currentBooking->rooms = $newRoomList;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $currentBooking);
        $this->loadReserveRoomInformation();
    }

    public function loadReserveRoomInformation() {
        $this->includefile("reserveroompanel");
    }
    
    public function addRooms() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());

        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        
        $number = $_POST['data']['numberofrooms'];
        for($i = 0; $i < $number; $i++) {
            $room = new \core_pmsmanager_PmsBookingRooms();
            $room->date = new \core_pmsmanager_PmsBookingDateRange();
            $room->date->start = $this->convertToJavaDate(strtotime($_POST['data']['start']. " " . $config->defaultStart));
            $room->date->end = $this->convertToJavaDate(strtotime($_POST['data']['end']. " " . $config->defaultEnd));
            $currentBooking->rooms[] = $room;
        }
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $currentBooking);
        $this->loadReserveRoomInformation();
    }
    
    public function accountsExport() {
        $users = $this->getusersTable();
        $rows = array();
        
        $heading = array();
        $heading[] = "Customer id";
        $heading[] = "Name";
        $heading[] = "Customer type";
        $heading[] = "Number of bookings";
        $heading[] = "Number of orders";
        $heading[] = "Last booking";
        $heading[] = "Payment type";
        $heading[] = "Suspended";
        $heading[] = "Has discount";
        $rows[] = $heading;
        
        foreach($users as $user) {
            $row = array();
            $row[] = $user->customerId;
            $row[] = $user->name;
            $row[] = $user->customerType;
            $row[] = $user->numberOfBookings;
            $row[] = $user->numberOfOrders;
            $row[] = $user->latestBooking;
            $row[] = $user->preferredPaymentType;
            $row[] = $user->suspended ? "YES" : "NO";
            $row[] = $user->hasDiscount ? "YES" : "NO";
            $rows[] = $row;
        }
        echo json_encode($rows);
    }
    
    public function setAccountsSubFilter() {
        $_SESSION['accountsSubFilter'] = json_encode($_POST['data']);
    }
    
    public function getAccountsSubFilter() {
        if(isset($_SESSION['accountsSubFilter'])) {
            return json_decode($_SESSION['accountsSubFilter'], true);
        }
        return array();
    }
    
    public function doAccountAction() {
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "user_") && $val == "true") {
                $userId = str_replace("user_", "", $key);
                $user = $this->getApi()->getUserManager()->getUserById($userId);
                if($_POST['data']['action'] === "disableaccounts") {
                    $user->suspended = !$user->suspended;
                    $this->getApi()->getUserManager()->saveUser($user);
                }
            }
        }
    }
    
    public function setBookingType() {
        $bookingId = $_POST['data']['bookingid'];
        $roomid = $_POST['data']['roomid'];
        $newType = $_POST['data']['newtype'];
        
        $res = $this->getApi()->getPmsManager()->setNewRoomType($this->getSelectedName(), $roomid, $bookingId, $newType);
        echo "<div style='color:red;padding-top:10px; padding-bottom: 10px;'>" . $res . "</div>";
        $this->showBookingInformation();
    }
    
    public function loadBookingTypes() {
        
        $booking = $this->getSelectedBooking();
        $selectedRoom = null;
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $selectedRoom = $room;
            }
        }
        
        echo "<b>Change room type</b><i class='fa fa-times' style='float:right; cursor:pointer;' onclick='$(\".changebookingtypepanel\").hide()'></i><br>";
        echo "<input type='hidden' gsname='bookingid' value='".$this->getSelectedBooking()->id."'>";
        echo "<input type='hidden' gsname='roomid' value='".$_POST['data']['roomid']."'>";
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        echo "<select gsname='newtype'>";
        foreach($types as $type) {
            $count = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedName(), $type->id, $room->date->start, $room->date->end);
            echo "<option value='".$type->id."'>".$type->name." ($count available)</option>";
        }
        echo "</select>";
        echo "<input type='button' value='Change' gstype='submitToInfoBox'>";
    }
    
    public function loadExistingUserList() {
        $userlist = $this->getApi()->getUserManager()->getAllUsersSimple();
        echo "<div style='padding-top: 20px;'></div>";
        echo "<select class='selectuserlist' style='width:100%;'>";
        echo "<option value=''>Change customer</option>";
        foreach($userlist as $user) {
            if(!$user->fullname) {
                continue;
            }
            echo "<option value='".$user->id."'>" . $user->fullname . " (" . $user->email . ")</option>";
        }
        echo "</select>";
        ?>
        <script>
        $('.selectuserlist').chosen();
        $('.selectuserlist').on('change', function() {
            var id = $(this).val();
            var event = thundashop.Ajax.createEvent('','changeUserOnBooking', $(this), {
                "userid" : id,
                "bookingid" : $('#openedbookingid').val()
            });
            thundashop.common.showInformationBoxNew(event);
        });
        </script>
        <?php
    }
    
    public function changeUserOnBooking() {
        $booking = $this->getSelectedBooking();
        $booking->userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        $this->showBookingInformation();
    }
    
    public function searchExistingCustomer() {
        $name = $_POST['data']['name'];
        $users = $this->getApi()->getUserManager()->findUsers($name);
        if(sizeof($users) > 0) {
            echo "<div style='margin-top: 20px; border-bottom: solid 1px; padding-bottom: 10px; margin-bottom: 10px;text-align:center;'>An existing user exist</div>";
            foreach($users as $user) {
                echo "<div class='existinguserselection' userid='".$user->id."' style='font-size:12px;'>";
                echo "<i class='fa fa-arrow-right' style='float:right; color:#bbb;'></i>";
                if($user->companyObject) {
                    echo "<b>" . $user->companyObject->vatNumber . "</b><bR>";
                }
                echo $user->fullName . "<bR>";
                echo $user->address->address . "<br>";
                echo $user->address->postCode . " " . $user->address->city . "<br>";
                echo $user->emailAddress;
                echo "</div>";
            }
        }
    }
    
    public function createNewUserOnBooking() {
        $orgid = $_POST['data']['orgId'];
        $name = $_POST['data']['name'];
        $bookingId = $_POST['data']['bookingid'];
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedName(),$bookingId, $name, $orgid);
        
        $this->renderEditUserView();
    }
    
    public function createNewBooking() {
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());
        $booking->userId = $_POST['data']['userid'];
        $booking->sessionId = "";
        $booking->confirmed = true;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $_POST['data']['bookingid'] = $booking->id;
        $this->showBookingInformation();
    }
    
    public function saveAccountInformation() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->fullName = $_POST['data']['name'];
        $user->address->address = $_POST['data']['adress'];
        $user->address->city = $_POST['data']['city'];
        $user->address->postCode = $_POST['data']['postcode'];
        $user->address->countrycode = $_POST['data']['countrycode'];
        $user->emailAddress = $_POST['data']['email'];
        $user->emailAddressToInvoice = $_POST['data']['invoiceemail'];
        $user->autoConfirmBookings = $_POST['data']['autoConfirmBookings'];
        $this->getApi()->getUserManager()->saveUser($user);
        
        if($user->companyObject) {
            $user->companyObject->name = $_POST['data']['name'];
            $this->getApi()->getUserManager()->saveCompany($user->companyObject);
        }
        
    }
    
    public function addAdvancedAddons() {
        $addonId = $_POST['data']['addonId'];
        $pmsRoomId = $_POST['data']['roomid'];
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $addonToUse = null;
        foreach($config->addonConfiguration as $addonConf) {
            if($addonConf->addonId == $addonId) {
                $addonToUse = $addonConf;
                break;
            }
        }
        
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "rowindex_") && $val == "true") {
                $addon = new \core_pmsmanager_PmsBookingAddonItem();
                $index = str_replace("rowindex_", "", $key);
                $addon->count = $_POST['data']['count_'.$index];
                $addon->date = $this->convertToJavaDate(strtotime($_POST['data']['date_'.$index]));
                $addon->price = $_POST['data']['price_'.$index];
                $addon->name = $_POST['data']['name'];
                $addon->productId = $addonToUse->productId;
                
                $this->getApi()->getPmsManager()->addAddonToRoom($this->getSelectedName(), $addon, $pmsRoomId);
            }
        }
        
        $this->showBookingInformation();
    }
    
    public function loadEditCartItemOnOrder() {
        $this->includefile("editcartitemonexistingorder");
    }
    
    public function deleteItemFromCart() {
        $items = array();
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        foreach($order->cart->items as $item) {
            if($item->cartItemId == $_POST['data']['cartitemid']) {
                continue;
            }
            $items[] = $item;
        }
        $order->cart->items = $items;
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function updateCartItemRow() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $startDate = null;
        $endDate = null;
        foreach($order->cart->items as $item) {
            if($item->cartItemId != $_POST['data']['cartitemid']) {
                continue;
            }
            
            $total = 0;
            $count = 0;
            //Update price matrix.
            foreach($_POST['data'] as $key => $val) {
                if(stristr($key, "matrixprice_")) {
                    $day = str_replace("matrixprice_", "", $key);
                    if(isset($_POST['data']['deletematrix_'.$day]) && $_POST['data']['deletematrix_'.$day] == "true") {
                        unset($item->priceMatrix->{$day});
                        continue;
                    }
                    if($startDate == null || $startDate > strtotime($day)) {
                        $startDate = strtotime($day);
                    }
                    if($endDate == null || $endDate < strtotime($day)) {
                        $endDate = strtotime($day);
                    }
                    $item->priceMatrix->{$day} = $val;
                    $total += $val;
                    $count++;
                }
            }
            
            //Update addon prices.
            $newAddonsList = array();
            $substractItemCount = 0;
            foreach($_POST['data'] as $key => $val) {
                if(stristr($key, "itemcount_")) {
                    $addonId = str_replace("itemcount_", "", $key);
                    $itemprice = $_POST['data']['itemprice_'.$addonId];
                    
                    foreach($item->itemsAdded as $addonItem) {
                        if($addonItem->addonId == $addonId) {
                                                
                            if(isset($_POST['data']['deleteitem_'.$addonId]) && $_POST['data']['deleteitem_'.$addonId] == "true") {
                                $substractItemCount -= $addonItem->count;
                                continue;
                            }
                            $count += $val;
                            $total += ($itemprice * $val);
                            
                            $addonItem->count = $val;
                            $addonItem->price = $itemprice;
                            
                            if($startDate == null || $startDate > strtotime($addonItem->date)) {
                                $startDate = strtotime($addonItem->date);
                            }
                            if($endDate == null || $endDate < strtotime($addonItem->date)) {
                                $endDate = strtotime($addonItem->date);
                            }
                            $newAddonsList[] = $addonItem;
                        }
                    }
                }
            }
            $item->itemsAdded = $newAddonsList;
            $item->count += $substractItemCount;
            
            if($count > 0) {
                $item->product->price = $total / $count;
                $item->count = $count;
            }
            
            if($item->product->id != $_POST['data']['productid']) {
                $newProduct = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
                $item->product->id = $newProduct->id;
                $item->product->taxGroupObject = $newProduct->taxGroupObject;
                $item->product->taxes = $newProduct->taxes;
            }
            $item->startDate = $this->convertToJavaDate($startDate);
            $item->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y", $endDate).' +1 day'));
            $item->product->additionalMetaData = $_POST['data']['roomnumber'];
            $item->product->metaData = $_POST['data']['roomname'];
            $item->product->name = $_POST['data']['productname'];
        }
        
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function connectItemsToRoom() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        
        foreach($order->cart->items as $item) {
            if(!$item->product->externalReferenceId) {
                $item->product->externalReferenceId = $_POST['data']['roomid'];
            }
        }
        $this->getApi()->getOrderManager()->saveOrder($order);
        
        $this->orderToDisplay = $order;
        $this->includefile("detailedorderinformation");
    }
    
    public function updateInvoiceNoteOnOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->invoiceNote = $_POST['data']['note'];
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function toggleNonRefundable() {
        $_POST['data']['bookingid'] = $_POST['data']['clicksubmit'];
        $booking = $this->getSelectedBooking();
        $newState = true;
        foreach($booking->rooms as $r) {
            if($r->nonrefundable) { $newState = false; }
        }
        
        foreach($booking->rooms as $r) {
            $r->nonrefundable = $newState;
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        $this->showBookingInformation();
    }
    
    public function getUserSettingsOrder() {
        return 1;
    }
    
    public function markBookingAsNoShow() {
        $this->getApi()->getWubookManager()->markNoShow($this->getSelectedName(), $_POST['data']['wubookid']);
    }
    
    public function sendSms() {
        $guestId = $_POST['data']['guestid'];
        $message = $_POST['data']['message'];
        $this->getApi()->getPmsManager()->sendSmsToGuest($this->getSelectedName(), $guestId, $message);
        $this->showBookingInformation();
    }
    
    public function removeOrderFromBooking() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        if(!@$order->closed) {
            $order->cart->items = array();
            $this->getApi()->getOrderManager()->saveOrder($order);
            
            $booking = $this->getSelectedBooking();
            $newArray = array();
            foreach($booking->orderIds as $ordid) {
                if($order->id == $ordid) {
                    continue;
                }
                $newArray[] = $ordid;
            }
            $booking->orderIds = $newArray;
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
            $this->selectedBooking = null;
        }
        $this->showBookingInformation();
    }
    
    public function loadAdditionalInformationForRoom() {
        $config = $this->getConfig();
        if($config->bookingProfile == "hotel"){
            $this->includefile("additionalinformationforroom");
        }else{
            $this->includefile("additionalinformationfornonhotel");
        }
    }
    
    public function markRoomCleaned() {
        $room = $this->getSelectedPmsRoom();
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedName(), $room->bookingItemId);
        $this->includefile("additionalinformationforroom");
    }
        public function markRoomCleanedwithoutlog() {
        $room = $this->getSelectedPmsRoom();
        $this->getApi()->getPmsManager()->markRoomAsCleanedWithoutLogging($this->getSelectedName(), $room->bookingItemId);
        $this->includefile("additionalinformationforroom");
    }
    public function forceBlockAccess() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $_POST['data']['roomid']);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room->blocked = true;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        
        echo "<br><center><b><i class='fa fa-check'></i> User has been blocked<br></center><br></b>";
        $this->loadAdditionalInformationForRoom();
    }
    
    public function forceUnBlockAccess() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $_POST['data']['roomid']);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room->blocked = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        
        echo "<br><center><b><i class='fa fa-check'></i> User has been blocked<br></center><br></b>";
        $this->loadAdditionalInformationForRoom();
    }
    
    public function forceUnGrantAccess() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $_POST['data']['roomid']);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room->forceAccess = false;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        echo "<br><center><b><i class='fa fa-check'></i> Forced access has been removed from user<br></center><br></b>";
        $this->loadAdditionalInformationForRoom();
    }
    
    public function forceGrantAccess() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $_POST['data']['roomid']);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room->forceAccess = true;
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        echo "<br><center><b><i class='fa fa-check'></i> User has been forced access<br></center><br></b>";
        $this->loadAdditionalInformationForRoom();
    }
    public function undoLastCleaning() {
        $room = $this->getSelectedPmsRoom();
        $this->getApi()->getPmsManager()->undoLastCleaning($this->getSelectedName(), $room->bookingItemId);
        $this->includefile("additionalinformationforroom");
    }
    
    public function resendCode() {
        $prefix = $_POST['data']['prefix'];
        $phoneNumber = $_POST['data']['phone'];
        $roomId = $_POST['data']['roomid'];
        
        $this->getApi()->getPmsManager()->sendCode($this->getSelectedName(),$prefix,$phoneNumber,$roomId);
        echo "<br><center><b><i class='fa fa-check'></i> Code has been sent<br></center><br></b>";
        $this->loadAdditionalInformationForRoom();
    }
    
    public function renewCode() {
        $this->getApi()->getPmsManager()->generateNewCodeForRoom($this->getSelectedName(), $_POST['data']['roomid']);
        echo "<br><center><b><i class='fa fa-check'></i> Code has been renewed<br></center><br></b>";
        $this->loadAdditionalInformationForRoom();
    }
    
    public function updateRecieptEmailOnOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->recieptEmail = $_POST['data']['newEmail'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->showBookingInformation();
    }
    
    public function saveOrderSettingsOnBooking() {
        $booking = $this->getSelectedBooking();
        $booking->dueDays = $_POST['data']['duedays'];
        $booking->periodesToCreateOrderOn = $_POST['data']['periodesToCreateOrderOn'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        $this->showBookingInformation();
    }
    
    public function toggleFilterVersion() {
        if(!isset($_SESSION['toggleOldFilterVersion'])) {
            $_SESSION['toggleOldFilterVersion'] = true;
        } else {
            unset($_SESSION['toggleOldFilterVersion']);
        }
    }
    
    public function saveCoverageSettings() {
        $filter = $this->getSelectedFilter();
        $this->getApi()->getPmsManager()->saveFilter($this->getSelectedName(), "coverage", $filter);
    }
    
    public function loadStatsForDay() {
        $this->includefile("orderstatsday");
    }
    
    public function loadOrderStatsForEntryCell() {
        $this->includefile("orderstatsresultentrycell");
    }
    
    public function saveConferenceData() {
        $conferenceData = new \core_pmsmanager_ConferenceData();
        $conferenceData->bookingId = $_POST['data']['bookingid'];
        $conferenceData->note = $_POST['data']['note'];
        $conferenceData->days = array();
        
        foreach ($_POST['data']['days'] as $postday) {
            $day = new \core_pmsmanager_ConferenceDataDay();
            $day->conferences = array();
            $day->day = $postday['day'];
            
            foreach ($postday['rows'] as $row) {
                $conferenceDataRow = new \core_pmsmanager_ConferenceDataRow();

                $conferenceDataRow->place = $row['place'];
                $conferenceDataRow->from = $row['from'];
                $conferenceDataRow->to = $row['to'];
                $conferenceDataRow->actionName = $row['actionName'];
                $conferenceDataRow->attendeesCount = $row['attendeesCount'];
                $conferenceDataRow->rowId = $row['id'];
                $day->conferences[] = $conferenceDataRow;
            }
            
            $conferenceData->days[] = $day;
        }
        
        $this->getApi()->getPmsManager()->saveConferenceData($this->getSelectedName(), $conferenceData);
    }
    
    public function sendBookingInformationRadioButton(){
        $bookingid = $_POST['data']['bookingid'];
        $radio = $_POST['data']['radiobutton'];
        $field = $_POST['data']['field'];
        
        $booking = $this->getSelectedBooking();
        $booking->registrationData->resultAdded->{$field} = $radio;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        
        $this->showBookingInformation();
    }
    public function sendupdateBookingInformationDropdown(){
        $bookingId = $_POST['data']['bookingid'];
        $dropdown = $_POST['data']['dropdown'];
        $field = $_POST['data']['field'];
        
        $booking = $this->getSelectedBooking();
        $booking->registrationData->resultAdded->{$field} = $dropdown;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        
        $this->showBookingInformation();
    }
    
    public function saveUploadedUserFile() {
        $content = strstr($_POST['data']['fileBase64'], "base64,");
        $rawContent = $_POST['data']['fileBase64'];
        $contentType = substr($rawContent, 0, strpos($rawContent, ";base64,"));
        if($contentType) {
            $contentType = str_replace("data:", "", $contentType);
            $contentType = trim($contentType);
        }

        $content = str_replace("base64,", "", $content);
        $content = base64_decode($content);
        $imgId = \FileUpload::storeFile($content);
        
        $entry = new \core_usermanager_data_UploadedFiles();
        $entry->fileName = $_POST['data']['fileName'];
        $entry->fileId = $imgId;
        $entry->createdDate = $this->convertToJavaDate(time());
        $entry->contentType = $contentType;
        
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userId']);
        $user->files[] = $entry;
        $this->getApi()->getUserManager()->saveUser($user);
        
    }
    
    public function removeAddonsFromRoom() {
        $roomId = $_POST['data']['roomId'];
        foreach($_POST['data']['idstoremove'] as $id) {
            $this->getApi()->getPmsManager()->removeAddonFromRoomById($this->getSelectedName(), $id, $roomId);
        }
        $this->showBookingInformation();
    }
    
    public function saveAddonsCountAndPrice() {
        $id = $_POST['data']['productId'];
        $roomId = $_POST['data']['roomId'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $roomId);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomId) {
                foreach($room->addons as $addon) {
                    if(isset($_POST['data'][$addon->addonId."_count"])) {
                        $addon->count = $_POST['data'][$addon->addonId."_count"];
                        $addon->price = $_POST['data'][$addon->addonId."_price"];
                    }
                }
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        
        $_POST['data']['bookingid'] = $booking->id;
        $this->showBookingInformation();
    }
    
    public function loadEventListForRoom() {
        $id = $_POST['data']['productId'];
        $roomId = $_POST['data']['roomId'];
        echo "<span gstype='form' method='saveAddonsCountAndPrice'>";
        echo "<input type='hidden' value='$id' gsname='productId'>";
        echo "<input type='hidden' value='$roomId' gsname='roomId'>";
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $roomId);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomId) {
                echo "<table cellspacing='0' cellpadding='0'>";
                echo "<tr>";
                echo "<td><input type='checkbox' class='selectalladdons'></td>";
                echo "<td colspan='3'>" . $this->getApi()->getProductManager()->getProduct($id)->name . "</td>";
                echo "<td></td>";
                echo "</tr>";
                $result = array();
                foreach($room->addons as $addon) {
                    if($addon->productId == $id) {
                        $row = "";
                        $row .= "<tr>";
                        $row .= "<td><input type='checkbox' class='addontoremove' addonid='".$addon->addonId."'></td>";
                        $row .= "<td> ".date("d.m.Y", strtotime($addon->date))."</td>";
                        $row .= "<td><input type='text' value='" . $addon->count . "' style='width:30px' gsname='".$addon->addonId."_count'></td>";
                        $row .= "<td><input type='text' value='" . $addon->price . "' style='width:50px' gsname='".$addon->addonId."_price'></td>";
                        $row .= "<td>".$addon->name."</td>";
                        $row .= "</tr>";
                        $result[strtotime($addon->date)][] = $row;
                    }
                }
                ksort($result);
                foreach($result as $row) {
                    foreach($row as $res) {
                        echo $res;
                    }
                }
                echo "<tr>";
                echo "<td align='center'><i class='fa fa-trash-o removeAddonsFromRoom' title='Remove selected addons' style='cursor:pointer;'></i> </td>";
                echo "<td></td>";
                echo "<td align='center' onclick='$(this).closest(\".addonsadded\").fadeOut()' style='cursor:pointer;'>Close</td>";
                echo "<td align='center' gstype='submitToInfoBox' style='cursor:pointer;'>Save</td>";
                echo "<td></td>";
                echo "</tr>";
                echo "</table>";
            }
        }
        echo "</span>";
    }
    
    public function resendConfirmation() {
        $email = $_POST['data']['email'];
        $bookingId = $_POST['data']['bookingid'];
        $this->getApi()->getPmsManager()->sendConfirmation($this->getSelectedName(), $email, $bookingId, "");
        echo "<div style='border: solid 1px; padding: 10px; margin-bottom: 10px;'>";
        echo "<i class='fa fa-info'></i> Confirmation has been sent.";
        echo "</div>";
        echo "<script>$('.informationbox-outer').scrollTop(0);</script>";
        $this->showBookingInformation();
    }
    
    
    public function updatecleaningoptions() {
        $roomid = $_POST['data']['roomid'];
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $roomid);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomid) {
                $room->cleaningComment = $_POST['data']['cleaningcomment'];
                $room->date->cleaningDate = $this->convertToJavaDate(strtotime($_POST['data']['cleaningdate']));
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        
        $this->getApi()->getPmsManager()->setNewCleaningIntervalOnRoom($this->getSelectedName(), 
                $_POST['data']['roomid'], 
                (int)$_POST['data']['cleaninginterval']);
        
        $this->showBookingInformation();
    }
    
    public function addAddonsToRoom() {
        $config = $this->getConfig();
        $roomId = $_POST['data']['roomid'];
        foreach($config->addonConfiguration as $addonItem) {
            if($addonItem->productId == $_POST['data']['clicksubmit']) {
//                $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedName(), $addonItem->addonType, $roomId, true);
                $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedName(), $addonItem->addonType, $roomId, false);
                break;
            }
        }
        
        $this->showBookingInformation();
    }
    
    public function setSendInvoiceAfter() {
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        $booking->createOrderAfterStay = $_POST['data']['checked'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
    }
    
    public function removeCreateOrderAfterStay() {
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        $booking->createOrderAfterStay = false;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->showBookingInformation();
    }
    public function doRoomsBookedAction() {
        $action = $_POST['data']['action'];
        $bookingId = $_POST['data']['bookingid'];
        $booking = $this->getSelectedBooking();
        if($action == "delete") {
            foreach($_POST['data']['rooms'] as $roomid) {
                $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedName(), $bookingId, $roomid);
            }
        } else if($action == "split") {
            $this->getApi()->getPmsManager()->splitBooking($this->getSelectedName(), $_POST['data']['rooms']);
        } else if($action == "singlepayments" || $action == "singlepaymentsnosend") {
            $this->getApi()->getPmsInvoiceManager()->removeOrderLinesOnOrdersForBooking($this->getSelectedName(), $bookingId, $_POST['data']['rooms']);
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

                $newOrderId = $this->getManager()->createOrder($this->getSelectedName(), $bookingId, $filter);
                if($action != "singlepaymentsnosend") {
                    $email = $room->guests[0]->email;
                    $prefix = $room->guests[0]->prefix;
                    $phone = $room->guests[0]->phone;
                    $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedName(), $newOrderId, $bookingId, $email, $prefix, $phone);
                }
            }
        }
        $this->selectedBooking = null;

        $this->showBookingInformation();
    }
    
    public function markTest() {
        $booking = $this->getSelectedBooking();
        if(!$booking->testReservation) {
            $booking->testReservation = true;
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
            $this->selectedBooking = null;
            $this->showBookingInformation();
            foreach($booking->orderIds as $orderId) {
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                $order->testOrder = true;
            }
        } else {
            $booking->testReservation = false;
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
            $this->selectedBooking = null;
            $this->showBookingInformation();
            foreach($booking->orderIds as $orderId) {
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                $order->testOrder = false;
                $this->getApi()->getOrderManager()->saveOrder($order);
            }
        }
    }
    
    public function renderEditUserView() {
        $this->includefile("edituserview");
    }
    
    
    public function editUserOnOrder() {
        $this->includefile("edituserview");
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function renderUserSettings($user) {
        $this->selectedUser = $user;
        $this->includefile("pmsusersettings");
    }
    
    public function saveDiscounts() {
        $engine = $_POST['engine'];
        $userId = $_POST['userid'];
        $items = $this->getApi()->getBookingEngine()->getBookingItemTypes($engine);
        $curConfig = $this->getApi()->getPmsInvoiceManager()->getDiscountsForUser($engine, $userId);
        $curConfig->discounts = array();
        foreach($items as $item) {
            if($_POST[$item->id]) {
            $curConfig->discounts[$item->id] = $_POST[$item->id];
            }
        }
        $curConfig->discountType = $_POST['discounttype'];
        $curConfig->supportInvoiceAfter = $_POST['supportInvoiceAfter'] == "true";
        $this->getApi()->getPmsInvoiceManager()->saveDiscounts($engine, $curConfig);
    }
    
    
    public function loadOrderStats() {
        $this->includefile("orderstatsresult");
    }
    
    public function addProductToCart() {
        $item = $this->getApi()->getCartManager()->addProductWithSource($_POST['data']['productid'], 1, "pmsquickproduct");
        $this->selectedBooking = null;
        $this->printSingleCartItem($item, null);
    }
    
    public function displayCreateNewIncomeReportFilter() {
        $this->includefile("newincomereportfilter");
    }
    
    public function createNewIncomeReportFilter() {
        $filter = new \core_pmsmanager_PmsOrderStatsFilter();
        $filter->name = $_POST['data']['filtername'];
        $filter->includeVirtual = $_POST['data']['virtualorders'] == "true";
        $filter->priceType = $_POST['data']['taxtype'];
        $filter->shiftHours = $_POST['data']['shifthour'];
        $filter->displayType = $_POST['data']['periodisation'];
        $filter->savedPaymentMethod = $_POST['data']['paymentmethod'];
        
        $this->getApi()->getPmsInvoiceManager()->saveStatisticsFilter($this->getSelectedName(), $filter);
    }
    
    public function loadOrderInfoOnBooking() {
         $states = array();
        $states['0'] = "All";
        $states['1'] = "Created";
        $states['2'] = "Waiting for payment";
        $states['3'] = "Payment failed";
        $states['4'] = "Completed";
        $states['5'] = "Canceled";
        $states['6'] = "Sent";
        $states['7'] = "Payment completed";
        $states['8'] = "Collection failed";
        $states['9'] = "Need collecting";
        $states['10'] = "Send to invoice";
        
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
        echo "<table cellspacing='0'>";
        echo "<tR>";
        echo "<th>Created date</th>";
        echo "<th>Orderid</th>";
        echo "<th>Price</th>";
        echo "<th>Status</th>";
        echo "<th>Paid for</th>";
        echo "<th>Failed payment <i class='fa fa-close' onclick='$(\".orderinfobox\").hide();' style='cursor:pointer;'></i></th>";
        echo "</tr>";
        
        foreach($booking->orderIds as $orderId) {
            $order = $this->getApi()->getOrderManager()->getOrder($orderId);
            $total = $this->getApi()->getOrderManager()->getTotalAmount($order);
            echo "<tr>";
            echo "<td>" . date("d.m.Y H:i", strtotime($order->rowCreatedDate)) . "</td>";
            echo "<td>" . $order->incrementOrderId . " <a class='showorderbutton' orderid='".$order->id."'> - open</a></td>";
            echo "<td>" . $total . "</td>";
            echo "<td>" . $states[$order->status] . "</td>";
            if(@$order->closed) {
                echo "<td></td>";
                echo "<td></td>";
            } else {
                echo "<td><input type='button' value='Mark paid' gsclick='markOrder' gsarg1='".$order->id."' gsarg2='paidfor' gsarg3='".$booking->id."'></td>";
                echo "<td><input type='button' value='Mark failed' gsclick='markOrder' gsarg1='".$order->id."' gsarg2='failed' gsarg3='".$booking->id."'></td>";
            }
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function markOrder() {
        $userName = $this->getApi()->getUserManager()->getLoggedOnUser()->fullName;
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $_POST['data']['gsarg3']);
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['gsarg1']);
        if($_POST['data']['gsarg2'] == "paidfor") {
            $this->getApi()->getPmsInvoiceManager()->markOrderAsPaid($this->getSelectedName(), $booking->id, $order->id);
        } else {
            $order->status = 3;
            $order->payment->transactionLog->{time()*1000} = "Failed marking as paid for, by: " .$userName;
            $this->getApi()->getOrderManager()->saveOrder($order);
            $this->getApi()->getWubookManager()->markCCInvalid($this->getSelectedName(), $booking->wubookreservationid);
            $this->getApi()->getPmsManager()->failedChargeCard($this->getSelectedName(), $order->id, $booking->id);
        }
    }
    
    public function processVerifonePayment() {
          $this->getApi()->getVerifoneManager()->chargeOrder($_POST['data']['orderid'], 0, false);
//        echo "TEST";
    }
    
    public function runProcessor() {
        $this->getApi()->getPmsManager()->processor($this->getSelectedName());
        $this->getApi()->getPmsManager()->hourlyProcessor($this->getSelectedName());
        $this->getApi()->getPmsManager()->checkIfGuestHasArrived($this->getSelectedName());
    }
    
    public function updateRegistrationData() {
        $booking = $this->getSelectedBooking();
        $booking->registrationData->resultAdded->{$_POST['data']['field']} = $_POST['data']['newval'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
    }
    
    public function sendInvoice() {
        $email = $_POST['data']['bookerEmail'];
        $bookingId = $_POST['data']['bookingid'];
        $orderid = $_POST['data']['orderid'];
        $res = $this->getApi()->getPmsInvoiceManager()->sendRecieptOrInvoice($this->getSelectedName(), $orderid, $email, $bookingId);

        echo "<div style='border: solid 1px; padding: 10px; margin-bottom: 10px;'>";
        if(!$res) {
            echo "<i class='fa fa-info'></i> Invoice / receipt has been sent.";
        } else {
            echo "<i class='fa fa-info'></i> Invoice / receipt where <b>NOT</b> sent due to the following:" . $res;
        }
        echo "</div>";
        echo "<script>$('.informationbox-outer').scrollTop(0);</script>";

        $this->showBookingInformation();
    }
    
    public function fastCheckIn() {
        $starttime = strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']);
        $endtime = strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']);
        $room = $_POST['data']['roomitem'];
        
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedName(), $room);
        
        $user = $this->getApi()->getUserManager()->getUserById("fastchecking_user");
        if(!$user) {
            $user = new \core_usermanager_data_User();
            $user->fullName = "Fast checkin user";
            $user->id = "fastchecking_user";
            $this->getApi()->getUserManager()->saveUser($user);
        }
        
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());
        $booking->userId = $user->id;
        $booking->avoidCreateInvoice = true;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
        
        $this->getApi()->getPmsManager()->addBookingItem($this->getSelectedName(), 
                $booking->id, 
                $room,
                $this->convertToJavaDate($starttime), 
                $this->convertToJavaDate($endtime));
        $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedName());
        
        $this->runProcessor();
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
    
    
    public function exportUserDataToExcel() {
        $result = array();
        
        $filter = $this->getSelectedFilter();
        
        $rooms = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedName(), $filter);
        
        $users = array();
        foreach($rooms as $room) {
            if(in_array($room->userId, $users)) {
                continue;
            }
            $users[] = $room->userId;
        }
        
        $heading = array();
        $heading[] = "Userid";
        $heading[] = "Name";
        $heading[] = "Address";
        $heading[] = "Postcode";
        $heading[] = "city";
        $heading[] = "Birth date";
        $heading[] = "Org number";
        $heading[] = "Email";
        $heading[] = "Phone";
        $heading[] = "Vat number";
        
        $result[] = $heading;
        
        foreach($users as $id) {
            $line = array();
            $user = $this->getApi()->getUserManager()->getUserById($id);
            $accountingId = $user->accountingId;
            if(!$accountingId) {
                $accountingId = $user->customerId;
            }
            $line[] = $accountingId;
            $line[] = $user->fullName;
            $line[] = $user->address->address;
            $line[] = $user->address->postCode;
            $line[] = $user->address->city;
            $line[] = $user->birthDay;
            $line[] = $user->emailAddress;
            $line[] = $user->cellPhone;
            $arr = (array)$user->companyObject;
            if(!empty($user->company)) {
                $line[] = $user->companyObject->vatNumber;
            } else {
                $line[] = "";
            }
            $result[] = $line;
        }
        
        echo json_encode($result);
    }
    
    public function updateOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->payment->paymentType = $_POST['data']['clicksubmit'];
        if(stristr($_POST['data']['clicksubmit'], "invoice")) {
            $order->rowCreatedDate = $this->convertToJavaDate(time());
        }
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->showBookingInformation();
    }

    public function markPaid() {
        $dateDate = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $this->getApi()->getOrderManager()->markAsPaid($_POST['data']['orderid'], $dateDate, 0.0);
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
            
            $included = json_decode($_POST['data']['included']);
            $toPrint = array();
            $toExclude = array();
            foreach ($bookings as $booking) {
                if(!in_array($booking->pmsRoomId, $included)) {
                    $toExclude[] = $booking;
                    continue;
                }
                $toPrint[] = $booking;
            }

            echo "<b>Rooms included in the statistics</b><br>";
            $this->printSimpleRoomTable($toPrint, $day);
            echo "<br><br><b>Other rooms not included in the statistics</b><br>";
            echo "* Test bookings and not paid which is before today is not included in the statistics";
            $this->printSimpleRoomTable($toExclude, $day);
            echo "<br>";
            echo "<b>Orders included</b><br>";
            $orders = json_decode($_POST['data']['ordersincluded']);
            echo "<table cellspacing='0' cellpadding='0'>";
            echo "<tr><th>Order id</th><th>ex taxes</th><th>inc taxes</th><th>User</th></tr>";
            foreach($orders as $orderId) {
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                echo "<tr>";
                echo "<td>" . $order->incrementOrderId . "</td>";
                echo "<td>" . $this->getApi()->getOrderManager()->getTotalAmountExTaxes($order) . "</td>";
                echo "<td>" . $this->getApi()->getOrderManager()->getTotalAmount($order) . "</td>";
                echo "<td>" . $this->getApi()->getUserManager()->getUserById($order->userId)->fullName . "</td>";
                echo "</tr>";
            }
            echo "</table>";
        } else {
            $filterOptions = new \core_common_FilterOptions();
            $filterOptions->startDate = $this->convertToJavaDate(strtotime($day));
            $filterOptions->endDate = $this->convertToJavaDate(strtotime($day) + 86400);
            $filterOptions->pageSize = 223234234;
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
        if(!$_POST['data']['itemid']) {
            return;
        }
        $bookingid = $_POST['data']['bookingid'];
        $roomid = $_POST['data']['roomid'];
        $newRoom = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $_POST['data']['itemid']);

        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $roomid);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomid) {
                $name = $this->getSelectedName();
                $type = $room->bookingItemTypeId;
                $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
                $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
                
                $available = (array)$this->getApi()->getBookingEngine()->getAvailbleItemsWithBookingConsidered($name, $newRoom->bookingItemTypeId, $start, $end, $room->bookingId);
                $incUnnassigned = false;
                if(sizeof($available) == 0) {
                    echo "<b><i>This room can not be selected since unassiged bookings are depending on it:</i><br></b>";
                    $incUnnassigned = true;
                }
                $filter = new \core_pmsmanager_PmsBookingFilter();
                $filter->filterType = "active";
                $filter->startDate = $start;
                $filter->endDate = $end;
                
                $allRooms = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedName(), $filter);
                $toPrint = "";
                foreach($allRooms as $takenroom) {
                    if($takenroom->bookingItemId == $_POST['data']['itemid'] || ($incUnnassigned && !$takenroom->bookingItemId)) {
                        $toPrint .= $takenroom->owner . " - " . date("d.m.Y H:i", $takenroom->start / 1000) . " - " . date("d.m.Y H:i", $takenroom->end / 1000) . ", <span class='openbooking moreinformationaboutbooking' roomid='".$takenroom->pmsRoomId."' bookingid='".$takenroom->bookingId."' style='color:blue; cursor:pointer;'>open</span><br>";
                    }
                }
                if($toPrint) {
                    echo "<i class='fa fa-close' style='float:right; cursor:pointer;' onclick=\"$('.tiparea').hide()\"></i>";
                    if(sizeof($available) > 0) {
                        echo "<b>Item is taken by</b><br>";
                    }
                    echo $toPrint;
                }
            }
        }
    }
    
    public function sendPaymentLink() {
        $orderid = $_POST['data']['orderid'];
        $bookingid = $_POST['data']['bookingid'];
        $email = $_POST['data']['bookerEmail'];
        $prefix = $_POST['data']['bookerPrefix'];
        $phone = $_POST['data']['bookerPhone'];
        $msg = $_POST['data']['smsMessage'];

        echo "<div style='border: solid 1px; padding: 10px; margin-bottom: 10px;'>";
        echo "<i class='fa fa-info'></i> Paymentlink has been sent.";
        echo "<script>$('.informationbox-outer').scrollTop(0);</script>";
        echo "</div>";
        
        $this->getApi()->getPmsManager()->sendPaymentLinkWithText($this->getSelectedName(), $orderid, $bookingid, $email, $prefix, $phone, $msg);
        $this->showBookingInformation();
    }
    
    public function getDescription() {
        return "Administrate all your bookings from this application";
    }
    
    public function changeInvoicedTo() {
        $this->getApi()->getPmsManager()->changeInvoiceDate($this->getSelectedName(), $_POST['data']['roomid'], $this->convertToJavaDate(strtotime($_POST['data']['newdate'])));
        $this->showBookingInformation();
    }
    
    public function createNewUser() {
        $newUser = new \core_pmsmanager_PmsNewUser();
        $newUser->name = $_POST['data']['name'];
        $newUser->email = $_POST['data']['email'];
        $newUser->orgId = $_POST['data']['orgid'];
        $newUser->password = $_POST['data']['password'];
        $newUser->userlevel = $_POST['data']['userlevel'];
        if(!$newUser->name || !$newUser->email) {
            return;
        }
        $user = $this->getApi()->getPmsManager()->createUser($this->getSelectedName(), $newUser);        
        
        $_POST['data']['type'] = "subtype_accountoverview";
        $_POST['data']['userid'] = $user->id;
        $this->setQuickFilter();
    }
    
    public function addAddon() {
        $type = $_POST['data']['type'];
        $bookingId = $_POST['data']['bookingid'];
        $roomId = $_POST['data']['roomId'];
        $added = $_POST['data']['remove'];
        
        if(!$roomId) {
            $roomId = $bookingId;
        }

        $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedName(), $type, $roomId, $added);
        $this->showBookingInformation();
    }
    
    public function globalInvoiceCreation() {
        $config = $this->getConfig();
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->onlyEnded = false;
        $filter->prepayment = $config->prepayment;
        $filter->createNewOrder = true;
        if($config->increaseUnits > 0) {
            $filter->increaseUnits = $config->increaseUnits;
        }
        if($_POST['data']['preview'] == "true") {
            $filter->avoidOrderCreation = true;
        }
        if($_POST['data']['type'] == "ended") {
            $filter->onlyEnded = true;
        }
        if($_POST['data']['chargeCardAfter']) {
            $filter->chargeCardAfter = $this->convertToJavaDate(strtotime($_POST['data']['chargeCardAfter']));
        }
        
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($_POST['data']['enddate']));
        $this->getApi()->getPmsManager()->createOrder($this->getSelectedName(), null, $filter);
    }
    
    public function includeOrderGenerationPreview() {
        $this->includefile("ordergenerationpreview");
    }
    
    public function createExcelForAddons() {
        $bookingItems = $this->getItems();
        $emptyRow = array();
        
        $rows = array();
        $booking = $this->getSelectedBooking();
        $types = $this->getTypes();
        $bookingItems = $this->getItems();

        $items = array();
        $items[1] = "BREAKFAST";
        $items[2] = "PARKING";
        $items[3] = "LATECHECKOUT";
        $items[4] = "EARLYCHECKIN";
        $items[5] = "EXTRABED";
        $items[6] = "CANCELATION FEE";
        $items[7] = "EXTRA CHILDBED";
        
        foreach ($booking->rooms as $room) {
            if($room->deleted) {
                continue;
            }
            
            $roomName = $types[$room->bookingItemTypeId]->name;
            if($room->bookingItemId) {
                $roomName .= " (" . $bookingItems[$room->bookingItemId]->bookingItemName . ") " . $room->guests[0]->name;
            }
            
            $guestName = "";
            if(isset($room->guests[0])) {
                $guestName = $room->guests[0]->name;
            }
            
            $rows[] = array();
            
            $row = array();
            $row[] = " $guestName " . $roomName . " - " . date("d.m.Y H:i" , strtotime($room->date->start)) . " - " . date("d.m.Y H:i" , strtotime($room->date->end));
            $rows[] = $row;
            
            if(sizeof($room->addons) == 0) {
                $row = array();
                $row[] = "No addons for this room";
                $rows[] = $row;
                
            } else {
                foreach($room->addons as $addon) {
                    $name = "";
                    
                    if($addon->productId) {
                        $name = $this->getApi()->getProductManager()->getProduct($addon->productId)->name;
                    }
                    
                    if(isset($items[$addon->addonType])) {
                        $name = $items[$addon->addonType];
                    }
                    
                    $row = array();
                    $row[] = $name;
                    $row[] = $roomName;
                    $row[] = $addon->count;
                    $row[] = $addon->price;
                    $row[] = date("d.m.Y, H:i", strtotime($addon->date));
                    $row[] = $addon->isIncludedInRoomPrice ? "IRM" : "NO";
                    $rows[] = $row;
                }

            }
        }
        
        echo json_encode($rows);
        die();
    }
    
   public function loadBookingDataArea() {
       $area = $_POST['data']['area'];
       $_SESSION['lastloadedarea'] = $area;
       switch($area) {
           case "roomsbooked":
           case "addons":
           case "messagearea":
           case "orderinformation":
           case "orderpreview":
           case "carddata":
           case "functions":
           case "otherbookingoptions":
               $this->includefile($area);
               break;
           default:
               echo "File not found";
               break;
       }
   } 
   
   public function saveOtherBookingOptions() {
       $booking = $this->getSelectedBooking();
       $booking->priceType = $_POST['data']['priceType'];
       $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
       $this->selectedBooking = null;
       $this->showBookingInformation();
   }
   
   public function setNewPasswordOnUser() {
       $userId = $_POST['data']['userid'];
       $password = $_POST['data']['password'];
       $this->getApi()->getUserManager()->updatePasswordSecure($userId, $password);
   }
   
    public function showBookingInformation() {
        $this->includefile("bookinginformation");
    }
    
    public function updatePasswordSettings() {
        if($_POST['data']['password']) {
            $this->getApi()->getUserManager()->updatePasswordSecure($_POST['data']['userid'], $_POST['data']['password']);
        }
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->referenceKey = $_POST['data']['reference'];
        $this->getApi()->getUserManager()->saveUser($user);
        
    }
    
    public function saveDiscountPreferences() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->preferredPaymentType = $_POST['data']['preferredPaymentType'];
        $user->showExTaxes = $_POST['data']['showExTaxes'] == "true";
        $discount = $this->getApi()->getPmsInvoiceManager()->getDiscountsForUser($this->getSelectedName(), $user->id);
        $discount->supportInvoiceAfter = $_POST['data']['createAfterStay'] == "true";
        $discount->discountType = 0;
        $discount->pricePlan = $_POST['data']['pricePlan'];
        $discount->attachedDiscountCode = $_POST['data']['attachedDiscountCode'];
        
        if($_POST['data']['discounttype'] == "fixedprice") {
            $discount->discountType = 1;
        }
        foreach($_POST['data'] as $index => $val) {
            if(stristr($index, "discount_")) {
                $room = str_replace("discount_", "", $index);
                $discount->discounts->{$room} = $val;
            }
        }
        $this->getApi()->getPmsInvoiceManager()->saveDiscounts($this->getSelectedName(), $discount);
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function showBookingOnBookingEngineId() {
        $bid = $_POST['data']['bid'];
        
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedName(), $bid);
        foreach($booking->rooms as $room) {
            if($room->bookingId == $bid) {
                $_POST['data']['roomid'] = $room->pmsBookingRoomId;
            }
        }
        $_POST['data']['bookingid'] = $booking->id;
                    
        if(isset($_POST['data']['bookingid'])) {
            $this->showBookingInformation();
        } else {
            echo "Booking not found";
        }
    }
    
    public function updateDueDaysOnOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->dueDays = $_POST['data']['days'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->showBookingInformation();
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
    
    
    public function createPeriodeOrder() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . "15:00"));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . "12:00"));
        $amount = str_replace(",", ".", $_POST['data']['amount']);
        $this->getApi()->getPmsInvoiceManager()->createPeriodeInvoice($this->getSelectedName(), $start, $end, $amount, $_POST['data']['bookingid']);
        $this->showBookingInformation();
    }
    
    public function addComment() {
        $id = $_POST['data']['bookingid'];
        $comment = $_POST['data']['comment'];
        if($comment) {
            $this->getApi()->getPmsManager()->addComment($this->getSelectedName(), $id, $comment);
        }
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
        $userId = "";
        if($_POST['data']['userid'] == "newuser") {
            $user = new \core_usermanager_data_User();
            $user->fullName = "New user";
            $newUser = $this->getApi()->getUserManager()->createUser($user);
            $userId = $newUser->id;
        } else {
            $userId = $_POST['data']['userid'];
        }
        $type = $_POST['data']['type'];
        if($type == "order") {
            $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
            $order->userId = $userId;
            $this->getApi()->getOrderManager()->saveOrder($order);
        } else {
            $booking->userId = $userId;
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
            $this->selectedBooking = null;
        }
        
        $this->renderEditUserView();
    }
    
    public function saveUser() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
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
        $user->address->countrycode = $_POST['data']['countryCode'];
        $user->birthDay = $_POST['data']['birthDay'];
        $user->relationship = $_POST['data']['relationship'];
        $user->preferredPaymentType = $_POST['data']['preferredpaymenttype'];
        $this->getApi()->getUserManager()->saveUser($user);
        
        $booking = $this->getSelectedBooking();
        $booking->countryCode = $user->address->countrycode;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        
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
        $guestInfoRoom = $_POST['data']['guestinfofromroom'];
        
        for($i = 0; $i < $_POST['data']['itemcount']; $i++) {
            $errors = $this->getApi()->getPmsManager()->addBookingItemType($this->getSelectedName(), $bookingId, $type, $start, $end, $guestInfoRoom);
            if($errors) {
                $this->errors[] = $errors;
            }
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
    
    public function createNewOrderAdvanced() {
        
        $items = $_POST['data']['items'];
        $users = $_POST['data']['user'];

        $cart = $this->getApi()->getCartManager()->getCart();
        $itemsToAdd = array();
        $end = null;
        foreach($cart->items as $cartItem) {
            $found = false;
            foreach($items as $item) {
                if($cartItem->cartItemId == $item['itemid']) {
                    if($item['start']) {
                        $cartItem->startDate = $this->convertToJavaDate(strtotime($item['start']));
                    }
                    if($item['end']) {
                        $cartItem->endDate = $this->convertToJavaDate(strtotime($item['end']));
                    }
                    $cartItem->count = $item['count'];
                    $cartItem->product->name = $item['name'];
                    $cartItem->product->price = $item['price'];
                    $this->getApi()->getCartManager()->updateCartItem($cartItem);
                    $itemsToAdd[] = $cartItem->cartItemId;
                    
                    if($end == null || $end < strtotime($item['end'])) {
                        $end = strtotime($item['end']);
                    }
                    
                }
            }
        }
        
        if(isset($users['userid']) && $users['userid']) {
            $user = $this->getApi()->getUserManager()->getUserById($users['userid']);
        } else {
            $user = new \core_usermanager_data_User();
            $user = $this->getApi()->getUserManager()->createUser($user);
        }
        
        $user->fullName = $users['name'];
        $user->emailAddress = $users['email'];
        $user->prefix = $users['prefix'];
        $user->cellPhone = $users['phone'];
        
        $user->address->address = $users['address'];
        $user->address->postCode = $users['postcode'];
        $user->address->city = $users['city'];
        $user->address->countryname = $users['country'];
        
        $this->getApi()->getUserManager()->saveUser($user);
        
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->itemsToCreate = $itemsToAdd;
        $filter->userId = $user->id;
        $filter->endInvoiceAt = $this->convertToJavaDate($end);
        $filter->createNewOrder = true;
        $filter->totalAmount = $_POST['data']['user']['totalamount'];
        
        $orderId = $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedName(), $_POST['data']['bookingid'], $filter);
        
        $instances = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        $instances = $this->indexList($instances);
        foreach($instances as $instance) {
            if(strtolower($instance->appName) == strtolower($users['paymenttype'])) {
                $instanceToUse = $instance;
                break;
            }
        }
        
        $text = $this->createPaymentTypeText($instanceToUse);
        $order = $this->getApi()->getOrderManager()->getOrder($orderId);
        $order->payment->paymentType = $text;
        $order->dueDays = $_POST['data']['dueDays'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        
        echo "Order has been created, what would you like to do <br>";
        echo "<div gstype='form' method='doAction'>";
        echo "<input type='hidden' gsname='bookingid' value='".$_POST['data']['bookingid']."'>";
        echo "<input type='hidden' gsname='orderid' value='".$order->id."'><br>";
        echo "Email : <input type='text' style='width: 220px;' gsname='email' placeholder='Email' value='".$users['email']."'><br>";
        echo "Phone : <input type='text' style='width: 70px;' gsname='prefix' placeholder='prefix' value='".$users['prefix']."'>";
        echo "<input type='text' style='width: 150px;' gsname='phone' placeholder='phone' value='".$users['phone']."'><br><br>";
        if($users['paymenttype'] == "InvoicePayment") {
            echo "<span class='continueorderbutton' gstype='submitToInfoBox' gsvalue='sendInvoice'>Send invoice</span>";
        }
        if($users['paymenttype'] == "Epay" || $users['paymenttype'] == "Dibs") {
            echo "<span class='continueorderbutton' gstype='submitToInfoBox' gsvalue='sendPaymentLink'>Send payment link</span>";
        } else {
            echo "<span class='continueorderbutton' gstype='submitToInfoBox' gsvalue='markPaid'>Mark order as paid</span>";
            echo "<span class='continueorderbutton' gstype='submitToInfoBox' gsvalue='markPaidSendReciept'>Mark order as paid and send receipt</span>";
        }
        echo "<br><span class='continueorderbutton' gstype='submitToInfoBox' gsvalue='refresh'>Continue</span>";
        echo "</div>";
    }
    
    public function doAction() {
        $orderId = $_POST['data']['orderid'];
        $bookingId = $_POST['data']['bookingid'];
        $email = $_POST['data']['email'];
        $phone = $_POST['data']['phone'];
        $prefix = $_POST['data']['prefix'];
        if($_POST['data']['clicksubmit'] == "sendInvoice") {
            $this->getApi()->getPmsInvoiceManager()->sendRecieptOrInvoice($this->getSelectedName(), $orderId, $email, $bookingId);
        }
        if($_POST['data']['clicksubmit'] == "sendPaymentLink") {
            $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedName(), $orderId, $bookingId, $email, $prefix, $phone);            
        }
        if($_POST['data']['clicksubmit'] == "markPaid" || $_POST['data']['clicksubmit'] == "markPaidSendReciept") {
            $order = $this->getApi()->getOrderManager()->getOrder($orderId);
            $order->status = 7;
            $this->getApi()->getOrderManager()->saveOrder($order);
        }
        if($_POST['data']['clicksubmit'] == "markPaidSendReciept") {
            $this->getApi()->getPmsInvoiceManager()->sendRecieptOrInvoice($this->getSelectedName(), $orderId, $email, $bookingId);
        }
        $this->showBookingInformation();
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
        $filter->pmsRoomId = $_POST['data']['roomId'];
        $filter->prepayment = true;
        $filter->createNewOrder = true;
        $filter->addToOrderId = $_POST['data']['appendToOrderId'];
        
        $this->getManager()->createOrder($this->getSelectedName(), $bookingId, $filter);
        $this->getManager()->processor($this->getSelectedName());
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

        $this->setLastSelectedRoom($_POST['data']['roomid']);       
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
        
        if (isset($_POST['data']['bookingid'])) {
            $bookingid = $_POST['data']['bookingid'];
            $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $bookingid);
            $this->selectedBooking = $booking;
            return $booking;
        }
        
        return null;
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
        
        if($this->getApi()->getStoreManager()->getMyStore()->id == "cd94ea1c-01a1-49aa-8a24-836a87a67d3b") {
            $res = (array)$this->getApi()->getPmsInvoiceManager()->validateAllInvoiceToDates($this->getSelectedName());
            foreach($res as $r) {
                echo "<b>$r</b><br>";
            }
        }
    }
    
    public function deleteDefinedFilter() {
        $this->getApi()->getPmsManager()->deletePmsFilter($this->getSelectedName(), $_POST['data']['name']);
    }
    
    public function setQuickFilter() {
        if(stristr($_POST['data']['type'], "stats_")) {
            $stattype = explode("_",$_POST['data']['type']);
            $stattype = $stattype[1];
            $_POST['data']['type'] = "stats";
        }
        if(stristr($_POST['data']['type'], "subtype_")) {
            $filter = $this->getSelectedFilter();
            $filter->filterSubType = str_replace("subtype_", "", $_POST['data']['type']);
            if(isset($_POST['data']['userid'])) {
                $filter->userId = $_POST['data']['userid'];
            } else {
                $filter->userId = "";
            }
            $this->setCurrentFilter($filter);
            return;
        }
        $this->emptyfilter();
        if(stristr($_POST['data']['type'], "defined_")) {
            $filterName = str_replace("defined_","", $_POST['data']['type']);
            $filter = $this->getApi()->getPmsManager()->getPmsBookingFilter($this->getSelectedName(), $filterName);
            $filter->startDate = $this->getApi()->getPmsManager()->convertTextDate($this->getSelectedName(), $filter->startDateAsText);
            $filter->endDate = $this->getApi()->getPmsManager()->convertTextDate($this->getSelectedName(), $filter->endDateAsText);
        } else {
            $filter = $this->getSelectedFilter();
            $filter->filterType = $_POST['data']['type'];
            $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time())));
            $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time())));
            if($filter->filterType == "stats" || $filter->filterType == "orderstats") {
                if($stattype == "thismonth") {
                    $filter->startDate = $this->convertToJavaDate(strtotime(date("01.m.Y 00:00", strtotime($filter->startDate))));
                    $filter->endDate = $this->convertToJavaDate(strtotime(date("t.m.Y 23:59", strtotime($filter->endDate))));
                }
                if($stattype == "nextmonth") {
                    $filter->startDate = $this->convertToJavaDate(strtotime(date("01.m.Y 00:00", strtotime(date("d.m.Y", strtotime($filter->startDate)) . " +1month"))));
                    $filter->endDate = $this->convertToJavaDate(strtotime(date("t.m.Y 23:59", strtotime(date("d.m.Y", strtotime($filter->endDate)) . " +1month"))));
                }
                if($stattype == "prevmonth") {
                    $filter->startDate = $this->convertToJavaDate(strtotime(date("01.m.Y 00:00", strtotime(date("d.m.Y", strtotime($filter->startDate)) . " -1month"))));
                    $filter->endDate = $this->convertToJavaDate(strtotime(date("t.m.Y 23:59", strtotime(date("d.m.Y", strtotime($filter->endDate)) . " -1month"))));
                }
                if($stattype == "thisyear") {
                    $filter->startDate = $this->convertToJavaDate(strtotime(date("01.01.Y 00:00", strtotime($filter->startDate))));
                    $filter->endDate = $this->convertToJavaDate(strtotime(date("t.12.Y 23:59", strtotime($filter->endDate))));
                    $filter->timeInterval = "monthly";
                }
                if($stattype == "pastyear") {
                    $filter->startDate = $this->convertToJavaDate(strtotime(date("01.01.Y 00:00", strtotime(date("d.m.Y", strtotime($filter->startDate)) . " -1year"))));
                    $filter->endDate = $this->convertToJavaDate(strtotime(date("t.12.Y 23:59", strtotime(date("d.m.Y", strtotime($filter->endDate)) . " -1year"))));
                    $filter->timeInterval = "monthly";
                }
            }

            if($_POST['data']['type'] == "stats") {
                $savedFilter = $this->getApi()->getPmsManager()->getPmsBookingFilter($this->getSelectedName(), "coverage");
                if($savedFilter) {
                    $savedFilter->startDate = $filter->startDate;
                    $savedFilter->endDate = $filter->endDate;
                    $savedFilter->timeInterval = $filter->timeInterval;
                    $filter = $savedFilter;
                }
            }
        }
        
        $this->setCurrentFilter($filter);
    }
    
    public function loadCoverage() {
    }
    
    public function exportBookingStats() {
        $filter = $this->getSelectedFilter();
        $stats = $this->getManager()->getStatistics($this->getSelectedName(), $this->getSelectedFilter());
        $arr = (array)$stats->entries;
        
        $matrix = array();
        
        $heading = array();
        $heading[] = "Dato";
        $heading[] = "Spear rooms";
        $heading[] = "Rented rooms";
        $heading[] = "Guests";
        $heading[] = "Avg.Price";
        $heading[] = "Total";
        $heading[] = "Budget";
        $heading[] = "Coverage";
        $matrix[] = $heading;
        
        foreach($arr as $entry) {
            $row = array();
            if ($entry->date) {
                $row[] = $this->getDayText($entry->date, $filter->timeInterval);
            } else {
                $row[] = "Sum";
            }
            
            $row[] = $entry->spearRooms;
            $row[] = $entry->roomsRentedOut;
            $row[] = $entry->guestCount;
            $row[] = $entry->avgPrice;
            $row[] = $entry->totalPrice;
            $row[] = $entry->bugdet;
            $row[] = $entry->coverage;
            $matrix[] = $row;
        }
        echo json_encode($matrix);
    }
    
    public function moveCard() {
        $userId = $_POST['data']['userid'];
        $cardId = $_POST['data']['cardid'];
        
        $users = $this->getApi()->getUserManager()->getAllUsers();
        $toMove = null;
        foreach($users as $user) {
            $found = false;
            $cardList = array();
            foreach($user->savedCards as $card) {
                if($card->id == $cardId) {
                    $found = true;
                    $toMove = $card;
                    continue;
                }
            }
            if($found) {
                $user->savedCards = $cardList;
                $this->getApi()->getUserManager()->saveUser($user);
            }
        }
        
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $user->savedCards[] = $toMove;
        $this->getApi()->getUserManager()->saveUser($user);
        
        $this->printCards($userId);
    }
    
    public function deleteCard() {
        $userId = $_POST['data']['userid'];
        $cardId = $_POST['data']['cardid'];
        
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $index = 0;
        $cardList = array();
        foreach($user->savedCards as $card) {
            if($card->id != $cardId) {
                $cardList[] = $card;
            }
            $user->savedCards = $cardList;
            $this->getApi()->getUserManager()->saveUser($user);
            $index++;
        }
        $this->printCards($userId);
    }
    
    public function searchForCard() {
        $searchword = $_POST['data']['searchword'];
        $userId = $_POST['data']['userid'];
        if(!$searchword) {
            return;
        }
        $users = $this->getApi()->getUserManager()->getAllUsers();
        foreach($users as $user) {
            foreach($user->savedCards as $card) {
                $text = json_encode($card);
                if(stristr($text, $searchword)) {
                    echo "<div style='border-bottom: solid 1px; padding-bottom: 3px; padding-top: 3px;'>";
                    echo $card->mask . " - " . $card->expireMonth . "/" . $card->expireYear;
                    echo "<span style='float:right; color:blue; cursor:pointer;' class='movecardbutton' cardid='".$card->id."' userid='".$userId."'>fetch</span>";
                    echo "</div>";
                }
            }
        }
        ?>
        <script>
            $('.movecardbutton').on('click', function() {
                var event = thundashop.Ajax.createEvent('','moveCard',$(this),{
                    "type" : "subtype_accountoverview",
                    "userid" : $(this).attr('userid'),
                    "cardid" : $(this).attr('cardid')
                });
                thundashop.Ajax.postWithCallBack(event, function(res) {
                    $('.cardlist').html(res);
                    $('.importcardpanel').hide();
                });
            });
        </script>
        <?php
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
    
    public function exportBookingExpediaDataToExcel() {
        $filter = $this->getSelectedFilter();
        $res = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedName(), $filter);
        if(!$res) {
            echo "[]";
            return;
        }
        $export = array();
        foreach($res as $room) {
            foreach($room->orderIds as $orderId) {
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                if(stristr($order->payment->paymentType, "expedia")) {
                    $start = $order->cart->items[0]->startDate;
                    $end = $order->cart->items[0]->endDate;
                    $totalEx = round($this->getApi()->getOrderManager()->getTotalAmountExTaxes($order));
                    $total = round($this->getApi()->getOrderManager()->getTotalAmount($order));
                    $row = array();
                    $row[] = $room->guest[0]->name;
                    $row[] = $order->incrementOrderId;
                    $row[] = $room->wubookchannelid;
                    $row[] = date("m/d/Y", strtotime($start));
                    $row[] = date("m/d/Y", strtotime($end));
                    $row[] = round((strtotime($end) - strtotime($start))/86400);
                    $row[] = $totalEx;
                    $row[] = $total - $totalEx;
                    $row[] = $total;
                    $export[] = $row;
                }
            }
        }
        echo json_encode($export);
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
                if($k == "bookingId") { continue; }
                if($k == "pmsRoomId") { continue; }
                if($k == "bookingItemId") { continue; }
                if($k == "bookingTypeId") { continue; }
                
                if(is_bool($val)) {
                    if($val) {
                        $val = "yes";
                    } else {
                        $val = "no";
                    }
                }
                
                if($k == "start" || $k == "end") {
                    $val = date("d.m.Y H:i", $val/1000);
                }
                
                if($k == "addons") {
                    $exportLine[$k] = json_encode($val);
                } else if($k == "orderIds") {
                    $exportLine[$k] = join(",", $val);
                } else if($k == "guest") {
                    $toAdd = "";
                    foreach($val as $guest) {
                        $toAdd = $guest->name;
                        if($guest->phone) {
                            $toAdd .= "+" . $guest->prefix . $guest->phone . " ";
                        }
                        if($guest->email) {
                            $toAdd .= $guest->email;
                        }
                    }
                    $exportLine[$k] = $toAdd;
                } else {
                    $exportLine[$k] = $val;
                }
            }
            
            $exportLine['totalex'] = 0.0;
            $exportLine['totalinc'] = 0.0;
            $ids = array();
            foreach($room->orderIds as $orderId) {
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                $ids[] = $order->incrementOrderId;
                $exportLine['totalex'] += $this->getApi()->getOrderManager()->getTotalAmountExTaxes($order);
                $exportLine['totalinc'] += $this->getApi()->getOrderManager()->getTotalAmount($order);
            }
            $exportLine['orderIds'] = join("," , $ids);
            $export[] = $exportLine;
        }
        
        array_unshift($export, array_keys($export[0]));
        echo json_encode($export);
    }
    
    public function setFilter() {
        $current = $this->getSelectedFilter();
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " 00:00"));
        $filter->endDate = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " 23:59"));
        $filter->filterType = $_POST['data']['filterType'];
        $filter->state = 0;
        $filter->filterSubType = $current->filterSubType;
        $filter->includeVirtual = $_POST['data']['include_virtual_filter'] == "true";
        $filter->searchWord = $_POST['data']['searchWord'];
        $filter->groupByBooking = $_POST['data']['groupByBooking'] == "true";
        if(isset($_POST['data']['channel'])) {
            $filter->channel = $_POST['data']['channel'];
        }
        $filter->typeFilter = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key,"filter_") && $val == "true") {
                $filter->typeFilter[] = str_replace("filter_", "", $key);
            }
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
        $filter = $this->getSelectedFilter();
        $filter->groupByBooking = !$filter->groupByBooking;
        $this->setCurrentFilter($filter);
    }
    
    public function isGroupedByBooking() {
        return $this->getSelectedFilter()->groupByBooking;
    }
    
    /**
     * @return \core_pmsmanager_PmsBookingFilter
     */
    public function getSelectedFilter() {
        if(!isset($_POST['event'])) {
            unset($_SESSION['pmfilter'][$this->getSelectedName()]);
        }
        if(isset($_SESSION['pmfilter'][$this->getSelectedName()])) {
            $filter = unserialize($_SESSION['pmfilter'][$this->getSelectedName()]);
            if($filter->searchWord) {
                $filter->includeDeleted = true;
            }
            return $filter;
        }

        $config = $this->getConfig();
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->state = 0;
        $filter->startDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 00:00", time()))-(86400*$config->defaultNumberOfDaysBack));
        $filter->endDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 00:00", time()))+86300);
        $filter->sorting = "regdate";
        if($config->bookingProfile == "conferense") {
            $filter->groupByBooking = true;
        }
        if(isset($_SESSION['pmfilter'][$this->getSelectedName()]) && $_SESSION['pmfilter'][$this->getSelectedName()]) {
            $filter->includeDeleted = true;
        }
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
        $error = $this->getManager()->setBookingItemAndDate($this->getSelectedName(),
                $_POST['data']['roomid'], 
                $_POST['data']['itemid'],
                $_POST['data']['clicksubmit'] == "split",
                $this->convertToJavaDate(strtotime($_POST['data']['start']. " " . $_POST['data']['starttime'])),
                $this->convertToJavaDate(strtotime($_POST['data']['end']. " " . $_POST['data']['endtime'])));
        if($error) {
            $this->errors[] = $error;
        }
        $this->setLastSelectedRoom($_POST['data']['roomid']);
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
        $this->setLastSelectedRoom($_POST['data']['roomid']);
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        $this->showBookingInformation();
    }
    
    public function checkOutGuest() {
        $this->getApi()->getPmsManager()->checkOutRoom($this->getSelectedName(), $_POST['data']['roomid']);
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
            } else {
                if($_POST['data']['pricematrix'] != "no") {
                    $newArray = array();
                    foreach($room->priceMatrix as $key => $val) {
                        $newArray[$key] = $val;
                    }
                    $room->priceMatrix = $newArray;
                }
            }
            if(isset($_POST['data']['clicksubmit']) && $_POST['data']['clicksubmit'] == "setPeriodePrice") {
                if($_POST['data']['roomid'] == $room->pmsBookingRoomId) {
                    $this->updatePriceMatrixWithPeriodePrices($room);
                }
            }
        }
        
        
        $this->setLastSelectedRoom($_POST['data']['roomid']);
        
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
        
        $bookingId = $_POST['data']['bookingid'];
        $roomId = $_POST['data']['roomid'];
        $this->getManager()->setGuestOnRoom($this->getSelectedName(), $guests, $bookingId, $roomId);
        
        $this->setLastSelectedRoom($_POST['data']['roomid']);
        
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
            <select style='width: 200px;' gsname='item' class='addroomselectiontype'>
                <?php 
                foreach($types as $type) {
                    /* @var $item core_bookingengine_data_BookingItem */
                    if(!$defaultType) {
                        $defaultType = $type->id;
                    }
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
        $this->addRoomCount($start, $end, $defaultType);
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
        if(!$entries) {
            echo "<br><br><center>No log entries found.</center><br><br>";
            return;
        }
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
            echo "<td valign='top'>" . date("d.m.Y H:i:s", strtotime($entry->dateEntry)) . "</td>";
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
        if(!$app) {
            $app = $this->getApi()->getStoreApplicationPool()->getApplication("13270e94-258d-408d-b9a1-0ed3bbb1f6c9");
        }
        return $app;
    }
    
    public function getChannels() {
        return $this->getConfig()->channelConfiguration;
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
        
        if(isset($filter->filterType) && $filter->filterType == "inhouse") {
            if($room->checkedin && !$room->checkedout) {
                return true;
            }
            return false;
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
            $add->isIncludedInRoomPrice = false;
            if($addon['includedInRoomPrice'] == "true") {
                $add->isIncludedInRoomPrice = true;
            }
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
        $count = 0;
        if(isset($room->addons)) {
            foreach($room->addons as $addon) {
                if($addon->addonType == $addonType) {
                    $count += $addon->count;
                }
            }
        }
        return $count;
    }
    
    public function tryAddToBookingEngine() {
       $this->getApi()->getPmsManager()->tryAddToEngine($this->getSelectedName(), $_POST['data']['id']);
    }
    
    public function splitToSingleBooking() {
        $roomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->splitBooking($this->getSelectedName(), $roomId);
        $this->showBookingInformation();
    }

    public function hasBreakfastAddon() {
        $config = $this->getConfig();
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

    public function printSimpleRoomTable($bookings, $day) {
        echo "<table cellspacing='0' cellpadding='0' width='100%'>";
        echo "<tr>";
        echo "<th>Room</th>";
        echo "<th>Owner</th>";
        echo "<th>Checkin</th>";
        echo "<th>Checkout</th>";
        echo "<th>Daily price</th>";
        echo "<th>State</th>";
        echo "<th>Test</th>";
        echo "</tr>";
        
        $totalprice = 0;
        foreach($bookings as $booking) {
            /* @var $booking \core_pmsmanager_PmsRoomSimple */
            if(date("d.m.Y", $booking->end/1000) == date("d.m.Y", strtotime($day))) {
                continue;
            }
            $price = $booking->price;
            $totalprice += $price;
            echo "<tr class='moreinformationaboutbooking' style='cursor:pointer;' bookingid='".$booking->bookingId."'>";
            echo "<td>" . $booking->room . "</td>";
            echo "<td>" . $booking->owner . "</td>";
            echo "<td>" . date("d.m.Y H:i", $booking->start/1000) . "</td>";
            echo "<td>" . date("d.m.Y H:i", $booking->end/1000) . "</td>";
            echo "<td>" . round($price) . "</td>";
            echo "<td>" . $booking->progressState . "</td>";
            if($booking->testReservation) {
                echo "<td>yes</td>";
            } else {
                echo "<td>no</td>";
            }
            echo "</tr>";
        }
        echo "<tr>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td>".round($totalprice)."</td>";
        echo "<td></td>";
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
            if($order->status == 3) {
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
        
        $start = $_POST['data']['day'] . "00:00";
        $end = $_POST['data']['day'] . "23:59";
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->filterType = "registered";
        $filter->startDate = $this->convertToJavaDate(strtotime($start));
        $filter->endDate = $this->convertToJavaDate(strtotime($end));
        
        $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
        echo "<br>Bookings registered<br><br>";
        echo "<table cellspacing='0' cellpadding='0'>";
        echo "<tr>";
        echo "<th>Created date</th>";
        echo "<th>Owner</th>";
        echo "<th>Value</th>";
        echo "</tr>";
        
        $total = 0;
        foreach($bookings as $booking) {
            $total += $booking->totalPrice;
            $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
            echo "<tr>";
            echo "<td>" . date("d.m.Y H:i", strtotime($booking->rowCreatedDate)) . "</td>";
            echo "<td>" . $user->fullName . "</td>";
            echo "<td>" . $booking->totalPrice . "</td>";
            echo "</tr>";
        }
        echo "<tr>";
        echo "<td></td>";
        echo "<td></td>";
        echo "<td>$total</td>";
        echo "</tr>";
        echo "</table>";
    }

    public function loadEditBookingItem() {
        $this->includefile("editbookingitem");
    }
    
    public function getCreatedOrders($includeBookingData) {
        $items = array();
        
        
        $cartmgr = $this->getApi()->getCartManager();
        $cart = $cartmgr->getCart();

        $totalEx = 0;
        $total = 0;
        $totalCount = 0;
        foreach($cart->items as $item) {
            if(!$item) {
                continue;
            }
            $priceEx = round($item->product->priceExTaxes,2);
            $price = round($item->product->price, 2);
            $totalEx += ($priceEx * $item->count);
            $total += ($price * $item->count);
            $totalCount += $item->count;
            $res = array();
            $res['additionalmetadata'] = $item->product->additionalMetaData;
            $res['itemstart'] = "";
            if($item->startDate) {
                $res['itemstart'] = date("d.m.Y H:i", strtotime($item->startDate));
            }
            $res['itemend'] = "";
            if($item->endDate) {
                $res['itemend'] = date("d.m.Y H:i", strtotime($item->endDate));
            }
            $res['productname'] = $item->product->name;
            $res['metadata'] = $item->product->metaData;
            $res['price'] = $price;
            $res['priceex'] = $priceEx;
            $res['count'] = $item->count;
            $res['groupedById'] = $item->groupedById;
            if($includeBookingData) {
                $booking = $this->findBookingFromRoom($item->product->externalReferenceId);
                $user = $this->findUser($booking->userId);

                $room = "";
                foreach($booking->rooms as $r) {
                    if($r->pmsBookingRoomId == $item->product->externalReferenceId) {
                        $room = $r;
                    }
                }
                
                $res['id'] = $booking->id;
                $res['fullname'] = $user->fullName;
                $res['start'] = date("d.m.Y H:i", strtotime($room->date->start));
                $res['end'] = date("d.m.Y H:i", strtotime($room->date->end));
                $res['invoicedto'] = date("d.m.Y H:i", strtotime($room->invoicedTo));
            } else {
                $res['id'] = "";
                $res['fullname'] = "";
                $res['start'] = "";
                $res['end'] = "";
                $res['invoicedto'] = "";
            }

            
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
        $arr = $this->getCreatedOrders(true);
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

    public function saveSelectedFields() {
        $fields = array();
        foreach($_POST['data'] as $key => $val) {
            if($_POST['data'][$key] === "true") {
                $fields[str_replace("fieldtoset_", "", $key)] = "";
            }
        }
        $this->setConfigurationSetting("selected_fields", json_encode($fields));
    }
    
    public function getSelectedFields($allFieldsToPrint) {
        $selected = $this->getConfigurationSetting("selected_fields");
        if(!$selected) {
            $default = array();
            $default['actions'] = $allFieldsToPrint['actions'];
            $default['regdate'] = $allFieldsToPrint['regdate'];
            $default['periode'] = $allFieldsToPrint['periode'];
            $default['visitor'] = $allFieldsToPrint['visitor'];
            $default['room'] = $allFieldsToPrint['room'];
            $default['price'] = $allFieldsToPrint['price'];
            $default['state'] = $allFieldsToPrint['state'];
            return $default;
        } else {
            $sel = json_decode($selected,true);
            foreach($sel as $key => $val) {
                $sel[$key] = $allFieldsToPrint[$key];
            }
            return $sel;
        }
    }

    public function getBooking($bookingId) {
        if(isset($this->fetchedBookings[$bookingId])) {
           $booking = $this->fetchedBookings[$bookingId];
        } else {
            $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $bookingId);
            $this->fetchedBookings[$bookingId] = $booking;
        }
        return $booking;
    }

    public function getLastSelectedRoom() {
        if(isset($_POST['data']['roomid'])) {
            $_SESSION['lastselectedroom'] = $_POST['data']['roomid'];
        }
        if(isset($_SESSION['lastselectedroom'])) {
            return $_SESSION['lastselectedroom'];
        }
        return "";
    }
    
    
    public function setLastSelectedRoom($roomId) {
        $_SESSION['lastselectedroom'] = $roomId;
    }


    public function getTotalAddons($saleStats) {
        $addonsResult = array();
        foreach($saleStats->entries as $entries) {
            foreach($entries->addonsCount as $addonId => $val) {
                @$addonsResult[$addonId]['count'] += $val;
            }
            foreach($entries->addonsPrice as $addonId => $val) {
                @$addonsResult[$addonId]['price'] += $val;
            }
        }
        
        return $addonsResult;
    }

    
    public function includeManagementViewResult() {
        $config = $this->getConfig();
        $filter = $this->getSelectedFilter();
        if($filter->filterSubType == "customerlist") {
            $this->includefile("accountstable");
        } else if($filter->filterSubType == "accountoverview") {
            $this->includefile("accountoverview");
        } else if($filter->filterType == "stats") {
            $this->includefile("statistics");
        } else if($filter->filterType == "summary") {
            if($config->bookingProfile == "conferense") {
                $this->includefile("summaryconference");
            } else {
                $this->includefile("summary");
            }
        } else if($filter->filterType == "unbilled") {
            $this->includefile("unbilled");
        } else if($filter->filterType == "invoicecustomers") {
            $this->includefile("invoicecustomers");
        } else if($filter->filterType == "orderstats") {
            $this->includefile("orderstats");
        } else {
            if(isset($_SESSION['toggleOldFilterVersion'])) {
                $this->includefile("managementviewtable");
            } else {
                $this->includefile("managementviewtablenew");
            }
            if($config->includeGlobalOrderCreationPanel) {
                $this->includefile("generateinvoicepanel");
            }
        }

    }

    public function removePaymentMethod() {
        $filter = $this->getOrderStatsFilter();
        $newMethods = array();
        foreach($filter->methods as $method) {
            if($method->{'paymentMethod'}."-".$method->{'paymentStatus'} != $_POST['data']['toRemove']) {
                $newMethods[] = $method;
            }
        }
        $filter->methods = $newMethods;
        $_SESSION['pmsorderstatsfilter'] = serialize($filter);
        $this->includefile("orderstatsresult");
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsOrderStatsFilter
     */
    public function getOrderStatsFilter() {
        $filter = new \core_pmsmanager_PmsOrderStatsFilter();
        $filter->priceType = "extaxes";
        $filter->savedPaymentMethod = "allmethods";
        $filter->displayType = "dayslept";
        
        if(isset($_SESSION['orderstatsfilterset']) && $_SESSION['orderstatsfilterset']) {
            $savedFilters = $this->getApi()->getPmsInvoiceManager()->getAllStatisticsFilters($this->getSelectedName());
            foreach($savedFilters as $tmpFilter) {
                if($tmpFilter->name == $_SESSION['orderstatsfilterset']) {
                    $filter = $tmpFilter;
                }
            }
        }
        
        $filter = $this->addPaymentTypeToFilter($filter);
        $filter->start = $this->getSelectedFilter()->startDate;
        $filter->end = $this->getSelectedFilter()->endDate;
 
        return $filter;
        
    }
    
    public function setneworderstatsfilter() {
        $_SESSION['orderstatsfilterset'] = $_POST['data']['filtertype'];
    }
    
    public function deleteOrderStatsFilter() {
        $this->getApi()->getPmsInvoiceManager()->deleteStatisticsFilter($this->getSelectedName(), $_POST['data']['id']);
    }

    public function addRoomCount($start, $end, $typeId) {
        $number = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedName(), $typeId, $start, $end);

        if($number <= 0) {
            return;
        }
        echo "<select gsname='itemcount'>";
        for($i = 1;$i <= $number; $i++) {
            echo "<option value='$i'>$i</option>";
        }
        echo "</select>";
    }
    
    public function updateInvoiceNoteOnBooking() {
        echo "Information has been updated.";
        $booking = $this->getSelectedBooking();
        $booking->invoiceNote = $_POST['data']['invoiceNote'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
        $this->selectedBooking = null;
        $this->showBookingInformation();
    }
    
    public function fastordercreation() {
        $appendToOrders = array();
        
        if(stristr($_POST['data']['appendToOrderId'], ",")) {
            $appendToOrders = explode(",", $_POST['data']['appendToOrderId']);
        } else {
            $appendToOrders[] = $_POST['data']['appendToOrderId'];
        }
        
        foreach($appendToOrders as $appendToOrder) {
            $filter = new \core_pmsmanager_NewOrderFilter();
            $bookingId = $_POST['data']['bookingid'];
            $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $bookingId);
            if(isset($_POST['data']['invoicenoteinfo'])) {
                $booking->invoiceNote = $_POST['data']['invoicenoteinfo'];
                $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
            }
            $filter->avoidOrderCreation = false;
            $filter->prepayment = true;
            $filter->createNewOrder = true;
            $filter->addToOrderId = $appendToOrder;
            if($this->supportNewDaily()) {
                $filter->pmsRoomIds = $this->getSelectedRooms();
                if($this->getSavedOrderDate("start")) {
                    $filter->startInvoiceAt = $this->convertToJavaDate(strtotime($this->getSavedOrderDate("start")));
                }
                if($this->getSavedOrderDate("end")) {
                    $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($this->getSavedOrderDate("end")));
                }
            }
            $filter->paymentType = $_POST['data']['paymenttype'];

            if($appendToOrder) {
                $this->getApi()->getPmsInvoiceManager()->clearOrder($this->getSelectedName(), $bookingId, $appendToOrder);
            }

            $instanceToUse = null;
            $instances = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
            $ptype = $_POST['data']['paymenttype'];

            $savedcard = null;
            if(stristr($ptype, "savedcard_")) {
                $cardid = str_replace("savedcard_", "", $ptype);
                $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
                foreach($user->savedCards as $card) {
                    if($cardid == $card->id) {
                        $savedcard = $card;
                    }
                }
                if(!$savedcard) {
                    echo "Failed to find saved card.";
                } else {
                    $ptype = $savedcard->savedByVendor;
                }
            }

            foreach($instances as $instance) {
                if(strtolower($instance->appName) == strtolower($ptype)) {
                    $instanceToUse = $instance;
                    break;
                }
            }
            $this->paymentLinkSent = false;
            $orderId = $this->getManager()->createOrder($this->getSelectedName(), $bookingId, $filter);
            if($orderId) {
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                if(!isset($appendToOrder) || !$appendToOrder) {
                    $order->avoidAutoSending = true;
                }
                $this->getApi()->getOrderManager()->saveOrder($order);


                $this->getManager()->processor($this->getSelectedName());

                if($_POST['data']['paymenttype'] != "70ace3f0-3981-11e3-aa6e-0800200c9a66" && 
                        (isset($_POST['data']['sendpaymentlink']) && $_POST['data']['sendpaymentlink'] === "true") &&
                        !$savedcard) {
                    $email = $_POST['data']['paymentlinkemail'];
                    $phone = $_POST['data']['paymentlinkphone'];
                    $prefix = $_POST['data']['paymentlinkprefix'];
                    $smsText = $_POST['data']['smsText'];
                    $this->getApi()->getPmsManager()->sendPaymentLinkWithText($this->getSelectedName(), $orderId, $bookingId, $email, $prefix, $phone, $smsText);
                    $this->paymentLinkSent = true;
                }

                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                $order->invoiceNote = $_POST['data']['invoicenoteinfo'];
                $this->getApi()->getOrderManager()->saveOrder($order);
            }

            if(isset($savedcard) && $savedcard) {
                if($this->getApi()->getOrderManager()->payWithCard($order->id, $savedcard->id)) {
                    echo "<i class='fa fa-check'></i> Successfully paid with saved card subscription id : " . $savedcard->card . " (" . $savedcard->mask . ")";
                } else {
                    echo "<i class='fa fa-warning'></i> Failed to pay with card  subscription id : " . $savedcard->card . " (" . $savedcard->mask;
                }
            }
        }
        $this->selectedBooking = null;
        
        $this->showBookingInformation();
    }
    
    public function loadOrderInformation() {
        $this->orderToDisplay = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $this->includefile("detailedorderinformation");
    }

    public function createOrderPreview($booking, $config) {
        $endDate = $this->getEndDateForBooking();
        foreach($booking->rooms as $room) {
            if($endDate < strtotime($room->date->end)) {
                $endDate = strtotime($room->date->end);
            }
        }
        if(isset($_POST['data']['endingAt'])) {
            $endDate = strtotime($_POST['data']['endingAt']);
        }
        $invoiceRoomId = "";
        if(isset($_POST['data']['roomId'])) {
            $invoiceRoomId = $_POST['data']['roomId'];
        }

        $endingAt = date("d.m.Y", $endDate);

        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->onlyEnded = false;
        $filter->prepayment = $config->prepayment;
        $filter->avoidOrderCreation = true;
        $filter->pmsRoomId = $invoiceRoomId;
        $filter->endInvoiceAt = $this->convertToJavaDate($endDate);
        if($this->supportNewDaily()) {
            $filter->pmsRoomIds = $this->getSelectedRooms();
            if($this->getSavedOrderDate("start")) {
                $filter->startInvoiceAt = $this->convertToJavaDate(strtotime($this->getSavedOrderDate("start")));
            }
            if($this->getSavedOrderDate("end")) {
                $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($this->getSavedOrderDate("end")));
            }
        }

        $this->getApi()->getPmsManager()->createOrder($this->getSelectedName(), $booking->id, $filter);
    }

    public function createOrderPreviewUnsettledAmount($booking, $config) {
        $endDate = $this->getEndDateForSpecifiedBooking($booking);
        
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->onlyEnded = false;
        $filter->prepayment = $config->prepayment;
        $filter->avoidOrderCreation = true;

        $filter->endInvoiceAt = $this->convertToJavaDate($endDate);
        $this->getApi()->getPmsManager()->createOrder($this->getSelectedName(), $booking->id, $filter);
    }

    public function getOrdersForSelectedBooking() {
        $booking = $this->getSelectedBooking();
        if(isset($this->selectedOrders)) {
            return $this->selectedOrders;
        }
        $result = array();
        foreach($booking->orderIds as $orderId) {
            $order = $this->getApi()->getOrderManager()->getOrder($orderId);
            $result[$order->incrementOrderId] = $order;
        }
        if(sizeof($result) > 0) {
            ksort($result);
        }
        $this->selectedOrders = $result;
        return $result;
    }
    
    public function detachOrderFromBooking() {
        $booking = $this->getSelectedBooking();
        $this->getApi()->getPmsManager()->detachOrderFromBooking($this->getSelectedName(), $booking->id, $_POST['data']['orderid']);
    }

    /**
     * 
     * @return \core_pmsmanager_PmsAdditionalItemInformation[]
     */
    public function getAdditionalInfoList() {
        if($this->additionalList) {
            return $this->additionalList;
        }
        $additional = $this->getApi()->getPmsManager()->getAllAdditionalInformationOnRooms($this->getSelectedName());
        foreach($additional as $add) {
            $additional[$add->itemId] = $add;
        }
        $this->additionalList = $additional;
        return $additional;

    }

    public function translatePtype($tmpType) {
        $types = array();
        $types['ExpediaPayment'] = "Expedia";
        $types['InvoicePayment'] = "Invoice";
        $types['PayOnDelivery'] = "Cash";
        if(isset($types[$tmpType])) {
            return $types[$tmpType];
        }
        return $tmpType;
    }

    public function clearCurrentBooking() {
        $this->selectedBooking = null;
    }

    public function printGuests($guests) {
        if(sizeof((array)$guests) == 0) {
            echo "No guest registered";
        }
        foreach ($guests as $guest) {
            if(isset($guest)) {
                echo (isset($guest->name) && trim($guest->name)) ? $guest->name : "No name";
            }
            echo "<br>";
        }
    }
    
    public function downloadOrderStatsMatrixToExcel() {
        echo json_encode($this->buildResultMatrix(false));
    }

    public function buildResultMatrix($includeDownload) {
        
        $filter = $this->getOrderStatsFilter();
        $filter->includeVirtual = $this->getSelectedFilter()->includeVirtual;

        $result = $this->getApi()->getPmsInvoiceManager()->generateStatistics($this->getSelectedName(), $filter);
        $_SESSION['currentOrderStatsResult'] = serialize($result);
        
        $products = $this->getApi()->getProductManager()->getAllProductsLight();
        $products = $this->indexList($products);
        $resultMatrix = array();
        $priceType = $filter->priceType;
        $total = 0;
        $productIds = array();
//        foreach($products as $prod) {
//            echo $prod->name . " - " . $prod->id. "<br>";
//        }
        foreach($result->entries as $entry) {
            $prices = $entry->priceExOrders;
            if($priceType == "extaxes") {
                $prices = $entry->priceEx;
            }

            foreach($prices as $id => $val) {
                if(!isset($productIds[$id])) {
                    $productIds[$id] = 0;
                }
                $productIds[$id] += $val;
                $total += $val;
            }
        }
        
        foreach($productIds as $prodId => $val) {
            if($val == 0) {
                unset($productIds[$prodId]);
            }
        }

        $row = array();
        if($includeDownload) {
            $row[] = "<i class='fa fa-file-excel-o' style='cursor:pointer;' gs_downloadexcelreport='downloadOrderStatsMatrixToExcel' title='Download to excel' gs_filename='bookingdataexport' ></i> Day";
        } else {
            $row[] = "Day";
        }
        $headerRowWithProductId = array();
        foreach($productIds as $id => $null) {
            if(isset($products[$id])) {
                $row[] = $products[$id]->name;
                $headerRowWithProductId[$id] = $products[$id]->name;
            } else {
                $row[] = "Deleted product";
                $headerRowWithProductId[$id] = "Deleted product";
            }
        }
        $row[] = "Total";
        $headerRow = $row;

        $index = 0;
        foreach($result->entries as $entry) {
            $row = array();
            $row[] = date("d.m.Y", strtotime($entry->day));
            $totalRow = 0;

            foreach($productIds as $id => $price) {
                $prices = $entry->priceInc;
                if($priceType == "extaxes") {
                    $prices = $entry->priceEx;
                }
                if(!isset($prices->{$id})) {
                    $row[] = "0";
                } else {
                    $row[] = round($prices->{$id});
                    $totalRow+= round($prices->{$id}, 2);
                }
            }
            $row[] = $totalRow;
            $resultMatrix[$index] = $row;
            $index++;
        }
        
        $sumBottom=array();
        foreach($resultMatrix as $row) {
            foreach($row as $idx => $field) {
                if(!isset($sumBottom[$idx])) {
                    $sumBottom[$idx] = 0;
                }
                if(is_numeric($field)) {
                    $sumBottom[$idx] += $field;
                }
            }
        }
        $tmpSum= array();
        foreach($sumBottom as $val) {
            $tmpSum[] = $val;
        }
        $tmpSum[0] = "";
        $resultMatrix[$index] = $tmpSum;

        
        $row = array();
        $row[] = round($total);
        foreach($productIds as $id => $price) {
            if(isset($price)) {
                $row[] = round($price);
            } else {
                $row[] = 0;
            }
        }
        asort($headerRow);
        asort($headerRowWithProductId);
        
        $_SESSION['currentOrderStatsResultHeader'] = serialize($headerRowWithProductId);
        
        $correctHeader = array();
        $correctHeader[0] = $headerRow[0];
        $totalIdx = "";
        foreach($headerRow as $idx => $val) {
            if($idx == 0) {
                continue;
            }
            if($val == "Total") {
                $totalIdx = $idx;
                continue;
            }
            $correctHeader[$idx] = $val;
        }

        $correctHeader[$totalIdx] = "Total";
        $headerRow = $correctHeader;
        
        $sortedMatrix = array();
        $sortedMatrix['header'] = $correctHeader;
        foreach($resultMatrix as $rowidx => $row) {
            $newRow = array();
            foreach($headerRow as $idx => $name) {
                $newRow[] = $row[$idx];
            }
            $sortedMatrix[$rowidx] = $newRow;
        }
        
        $accounts = array();
        foreach($productIds as $id => $val) {
           $product = $this->getApi()->getProductManager()->getProduct($id);
           if(!$product) {
               $product = $this->getApi()->getProductManager()->getDeletedProduct($id);
           }
           if(!isset($accounts[$product->accountingAccount])) {
               $accounts[$product->accountingAccount] = 0;
           }
           $accounts[$product->accountingAccount] += $val;
        }
        foreach($accounts as $account => $val) {
            $row = array();
            $row[] = $account;
            $row[] = round($val, 2);
            
            $sortedMatrix[] = $row;
        } 
        
        return $sortedMatrix;
    }

    function delete_col(&$array, $key) {
        return array_walk($array, function (&$v, $array) use ($key) {
            if(sizeof($v) > 2) {
                unset($v[$key]);
            }
        });
    }
    
    public function setPaymentMethodFilter() {
        $this->setIncomeFilter($_POST['data']['type']);
    }
    
    /**
     * @param \core_pmsmanager_PmsOrderStatsFilter $filter
     */
    public function addPaymentTypeToFilter($filter) {
        
        $paymentMethods = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();

        foreach ($paymentMethods as $key => $method) {
            if($filter->savedPaymentMethod != "allmethods" && $method->id != $filter->savedPaymentMethod) {
                continue;
            }
            
            $id = $method->id;
            if($id == "70ace3f0-3981-11e3-aa6e-0800200c9a66" || $id == "cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba") {
                //Invoices and samlefaktura needs to include everything
                $method = new \core_pmsmanager_PmsPaymentMethods();
                $method->paymentMethod = $id;
                $method->paymentStatus = 0;
                $filter->methods[] = $method;
                continue;
            }
            $method = new \core_pmsmanager_PmsPaymentMethods();
            $method->paymentMethod = $id;
            $method->paymentStatus = 7;
            $filter->methods[] = $method;
            
            $method = new \core_pmsmanager_PmsPaymentMethods();
            $method->paymentMethod = $id;
            $method->paymentStatus = -9;
            $filter->methods[] = $method;
        }
        
        if($filter->savedPaymentMethod == "transferredtoaccounting") {
            $method = new \core_pmsmanager_PmsPaymentMethods();
            $method->paymentMethod = "";
            $method->paymentStatus = -10;
            $filter->methods[] = $method;
        }
        
        return $filter;
    }

    public function mightInclude($area) {
        if(isset($_SESSION['lastloadedarea']) && $_SESSION['lastloadedarea'] == $area) {
            $this->includefile($area);
        } else if($area == "roomsbooked") {
            $this->includefile($area);
        }
    }

    public function isVirtual() {
        return $this->getSelectedFilter()->includeVirtual;
    }

    public function includeOrderCreationPanel() {
        $this->includefile("ordercreationpanel");
    }
    
    public function loadCartItems() {
        $this->includefile("ordergenerationcartitems");
    }
    
    public function editAddonAndPriceMatrixOnCartItem() {
        $this->includefile("editcartitemmatrix");
    }
    
    public function saveItemOnOrder() {
        foreach($this->getApi()->getCartManager()->getCart()->items as $item) {
            if($item->cartItemId == $_POST['data']['itemid']) {
                $item->priceMatrix = $_POST['data']['matrixdata'];
                foreach($item->itemsAdded as $addonItem) {
                    if(isset($_POST['data']['addondata'][$addonItem->addonId]['price'])) {
                        $addonItem->price = $_POST['data']['addondata'][$addonItem->addonId]['price'];
                        $addonItem->count = $_POST['data']['addondata'][$addonItem->addonId]['count'];
                    }
                }
                $this->getApi()->getCartManager()->updateCartItem($item);
            }
        }
    }

    public function printSingleCartItem($item, $lastSelectedId) {
        
        $disabled = "";
        if(sizeof($item->priceMatrix) > 0 || $item->itemsAdded) {
            $disabled = "DISABLED";
        }
        echo "<div selectionroomrow='".$lastSelectedId."' class='cartitemselectionrow'>";
        echo "<input type='checkbox' class='itemselection'>";
        echo "<input type='hidden' gsname='itemid' value='".$item->cartItemId."'>";
        if(isset($lastSelectedId)) {
            echo "<input type='hidden' gsname='pmsRoomId' value='". $lastSelectedId . "' style='width:120px;'> ";
        }

        $start = "";
        if(isset($item->startDate)) {
            $start =  date("d.m.Y H:i", strtotime($item->startDate));
        }
        $end = "";
        if(isset($item->startDate)) {
            $end =  date("d.m.Y H:i", strtotime($item->endDate));
        }
        echo "<input type='txt' gsname='start' value='".$start . "' style='width:120px;'> ";
        echo "<input type='txt' gsname='end' value='$end' style='width:120px;'> ";
        echo "<input type='txt' gsname='count' style='width: 25px;text-align:center;' value='". $item->count . "' style='width:120px;' class='cartcount' $disabled> ";
        echo "<input type='txt' gsname='name' value='". $item->product->name . "' style='width:500px;' class='itemname'> ";
        echo "<span class='loadEditAddonAndPriceMatrix'></span>";
        echo "<input type='txt' gsname='price' style='width: 60px;' class='cartprice' value='". $item->product->price . "' style='width:120px;' $disabled>";
        if($disabled) {
            echo " <i class='fa fa-edit editaddonpricematrix'></i>";
        }
        echo "</div>";
    }

    public function createSpecialFilter() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->filterName = $_POST['data']['name'];
        $filter->filterType = $_POST['data']['searchtype'];
        $filter->startDateAsText = $_POST['data']['startDate'];
        $filter->endDateAsText = $_POST['data']['endDate'];
        
        foreach($_POST['data'] as $key => $val) {
            if($val !== "true") {
                continue;
            }
            if(stristr($key, "typetoinclude_")) {
                $item = str_replace("typetoinclude_", "", $key);
                $filter->typeFilter[] = $item;
            }
        }
        
        $this->getApi()->getPmsManager()->saveFilter($this->getSelectedName(), $filter->filterName, $filter);
    }
    
    public function getSearchTypes() {
        $config = $this->getConfig();
        $searchtypes = array();
        $searchtypes['registered'] = "Registered";
        $searchtypes['active'] = "Active";
        $searchtypes['uncofirmed'] = "Unconfirmed";
        $searchtypes['checkin'] = "Checking in";
        $searchtypes['checkout'] = "Checking out";
        $searchtypes['waiting'] = "Waiting";
        $searchtypes['activecheckin'] = "Checkin + stayover";
        $searchtypes['activecheckout'] = "Checkout + stayover";
        $searchtypes['inhouse'] = "Inhouse";
        $searchtypes['stats'] = "Coverage";
        $searchtypes['summary'] = "Summary";
        if($config->requirePayments) {
            $searchtypes['orderstats'] = "Income report";
            $searchtypes['afterstayorder'] = "Order created after stay";
            $searchtypes['unsettled'] = "Bookings with unsettled amounts";
        }
        if($config->bookingProfile == "storage" || $config->bookingProfile == "subscription") {
            $searchtypes['requestedending'] = "Requested to be ended";
        }
        return $searchtypes;
    }

    public function translateFields($field) {
        $fields = $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($this->getSelectedName());
        
        foreach($fields->data as $t => $f) {
            if($f->name == $field) {
                return $f->title;
            }
        }
        return "Non translatable";
    }

    /**
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getSelectedPmsRoom() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $_POST['data']['roomid']);
        $room = null;
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                $room = $r;
                break;
            }
        }
        return $room;
    }

    /**
     * @param \core_pmsmanager_PmsAdditionalItemInformation[] $addons
     */
    public function createAddonText($room) {
        $products = $this->getAllProducts();
        $typesAdded = array();
        $total = 0;
        foreach($room->addons as $addon) {
            if($addon->addonType == 1) {
                continue;
            }
            if(!isset($typesAdded[$addon->productId])) {
                $typesAdded[$addon->productId]=0;
            }
            $typesAdded[$addon->productId] += $addon->count;
            $total += ($addon->price * $addon->count);
        }
        $res = array();
        foreach($typesAdded as $prodId => $val) {
            if(isset($products[$prodId])) {
                $title = $val . " x " . $products[$prodId]->name;
            } else {
                $title = $val . " x deleted";
            }
            if(isset($products[$prodId]->name)) {
                $name = $this->getFirstWords($products[$prodId]->name);
                $res[] = "<span title='$title' style='cursor:pointer;'>($name)</span>";
            } else {
                $res[] = "<span title='$title' style='cursor:pointer;'>(deleted product)</span>";
            }
        }
        $text = "";
        $i = 0;
        if(sizeof($res) > 0) {
            $text = join("," , $res);
        }

        return $text;
    }

    public function getAllProducts() {
        if(!isset($this->allProducts) || !$this->allProducts) {
            $this->allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        }
        
        return $this->allProducts;
    }

    public function getFirstWords($sentence) {
        if(!trim($sentence)) {
            return "";
        }
        $words = explode(" ", $sentence);
        $acronym = "";

        foreach ($words as $w) {
          $acronym .= mb_substr($w, 0, 1, "UTF-8");
        }

        return strtoupper($acronym);
    }

    public function supportNewDaily() {
        $booking = $this->getSelectedBooking();
        if ($booking == null)
            return false;
        
        return $this->getApi()->getPmsInvoiceManager()->supportsDailyPmsInvoiceing($this->getSelectedName(), $booking->id);
    }

    public function getEndDateForBooking() {
        $booking = $this->getSelectedBooking();
        return $this->getEndDateForSpecifiedBooking($booking);
    }

    public function loadUnsettledAmount() {
        if(isset($_POST['data']['roomIdsSelected'])) {
            $_SESSION['savedSelectedRooms'] = json_encode($_POST['data']['roomIdsSelected']);
        } else {
            unset($_SESSION['savedSelectedRooms']);
        }
        $this->includefile("warningunsettledorders");
    }
    
    public function changeOrderDatePeriode() {
        $_SESSION['SAVED_ORDER_DATE'][$_POST['data']['type']] = $_POST['data']['newDate'];
        $this->includefile("warningunsettledorders");
    }
    
    public function getSavedOrderDate($type) {
        if(isset($_SESSION['SAVED_ORDER_DATE'][$type])) {
            return $_SESSION['SAVED_ORDER_DATE'][$type];
        }
        return null;
    }
    
    public function getSelectedRooms() {
        if(!isset($_SESSION['savedSelectedRooms'])) {
            return array();
        }
        return (array)json_decode($_SESSION['savedSelectedRooms']);
    }
    
    public function getStartDateForBooking() {
        $start = null;
        $booking = $this->getSelectedBooking();
        foreach($booking->rooms as $room) {
            if($start == null || $start > strtotime($room->date->start)) {
                $start = strtotime($room->date->start);
            }
        }
        
        
        foreach($booking->orderIds as $orderId) {
            $order = $this->getApi()->getOrderManager()->getOrder($orderId);
            foreach($order->cart->items as $item) {
                if($start == null || $start > strtotime($item->startDate)) {
                    $start = strtotime($item->startDate);
                }
            }
        }
        
        return $start;
    }

    public function clearSavedBookingData() {
        unset($_SESSION['SAVED_ORDER_DATE']);
        unset($_SESSION['savedSelectedRooms']);
    }

    public function getTotalForRoomCreatedOnOrders($roomId) {
        $orders = $this->getOrdersOnBooking();
        $total = 0;
        foreach($orders as $order) {
            foreach($order->cart->items as $item) {
                if($item->product->externalReferenceId == $roomId) {
                    $total += $item->count * $item->product->price;
                }
            }
        }
        
        return $total;
    }

    public function getOrdersOnBooking() {
        if($this->ordersOnBooking) {
            return $this->ordersOnBooking;
        }
        $booking = $this->getSelectedBooking();
        $orders = array();
        foreach($booking->orderIds as $orderId) {
            $orders[] = $this->getApi()->getOrderManager()->getOrder($orderId);
        }
        
        $this->ordersOnBooking = $orders;
        return $orders;
    }

    /**
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function updatePriceMatrixWithPeriodePrices($room) {
//        echo "<pre>";
//        print_r($_POST['data']);
//        print_r($room->priceMatrix);
//        echo "</pre>";

        $amount = $_POST['data']['periodePrice'];
        switch($_POST['data']['periodePriceType']) {
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
                            $room->priceMatrix[$day] = $avg;
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
                            $room->priceMatrix[$day] = $avg;
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
    }

    public function showDeletedRooms() {
        return false;
    }

    public function getEndDateForSpecifiedBooking($booking) {
        $end = null;
        foreach($booking->rooms as $room) {
            if($end == null || $end < strtotime($room->date->end)) {
                $end = strtotime($room->date->end);
            }
            foreach($room->addons as $addon) {
                if($end == null || $end < strtotime($addon->date)) {
                    $end = strtotime($addon->date);
                }
            }
        }
        
        foreach($booking->orderIds as $orderId) {
            $order = $this->getApi()->getOrderManager()->getOrder($orderId);
            foreach($order->cart->items as $item) {
                if($end == null || $end < strtotime($item->endDate)) {
                    $end = strtotime($item->endDate);
                }
            }
        }
        
        if(!$end) {
            $end = time();
        }
        
        return $end;
    }

    /**
     * @return \core_pmsmanager_PmsConfiguration
     */
    public function getConfig() {
        if(isset($this->config) && $this->config) {
            return $this->config;
        }
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $this->config = $config;
        return $this->config;
    }
    
    public function startAddonToRoom() {
        $this->includefile("addaddonstoroomstep2");
    }
    
    public function loadAddonList() {
        $this->includefile("addaddonstoroomstep1");
    }
    public function loadAddonsToBeAddedPreview() {
        $this->includefile("addaddonstoroomstepDatePreview");
    }
    

    public function sortConfig($addonConfigs, $products) {
        $sortArray = array();
        foreach($addonConfigs as $addonToPrint) {
            /* @var $addonToPrint core_pmsmanager_PmsBookingAddonItem */
            if(!isset($products[$addonToPrint->productId])) {
                continue;
            }
            $name = $products[$addonToPrint->productId]->name;
            $sortArray[$addonToPrint->addonId] = $name;
        }
        asort($sortArray);
        $result = array();
        foreach($sortArray as $addonId => $name) {
            foreach($addonConfigs as $item) {
                if($item->addonId == $addonId) {
                    $result[] = $item;
                }
            }
        }
        
        return $result;
    }

    public function loadAddonToAddToRoomPreview() {
        $list = "";
    }

    public function getDatesToAdd($start, $end, $room) {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $addonItem = null;
        foreach($config->addonConfiguration as $id => $item) {
            if($item->addonId == $_POST['data']['addonId']) {
                $addonItem = $item;
            }
        }
        
        $dates = array();
        $result = array();
        
        if($_POST['data']['periode_selection'] == "startofstay") {
            $dates[] = $start;
        }
        if($_POST['data']['periode_selection'] == "endofstay") {
            $dates[] = $end;
        }
        if($_POST['data']['periode_selection'] == "single") {
            $dates[] = time();
        }
        if($_POST['data']['periode_selection'] == "repeat_weekly") {
            $data = new \stdClass();
            $data->firstEvent = new \stdClass();
            $data->firstEvent->start = $this->convertToJavaDate($start+60);
            $data->firstEvent->end = $this->convertToJavaDate($start+60);
            $data->repeatPeride = 1;
            $data->endingAt = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", $end)));
            
            $data->repeatMonday = $_POST['data']['repeat_mon'] == "true";
            $data->repeatTuesday = $_POST['data']['repeat_tue'] == "true";
            $data->repeatWednesday = $_POST['data']['repeat_wed'] == "true";
            $data->repeatThursday = $_POST['data']['repeat_thu'] == "true";
            $data->repeatFriday = $_POST['data']['repeat_fri'] == "true";
            $data->repeatSaturday = $_POST['data']['repeat_sat'] == "true";
            $data->repeatSunday = $_POST['data']['repeat_sun'] == "true";
            $data->repeatEachTime = 1;
            $data->avoidFirstEvent = true;
            
            $res = $this->getApi()->getPmsManager()->generateRepeatDateRanges($this->getSelectedName(), $data);
            foreach($res as $r) {
                $dates[] = strtotime($r->start);
            }
        }
        if($_POST['data']['periode_selection'] == "repeat_monthly") {
            $data = new \stdClass();
            $data->firstEvent = new \stdClass();
            $data->firstEvent->start = $this->convertToJavaDate($start);
            $data->firstEvent->end = $this->convertToJavaDate($start);
            $data->repeatPeride = 2;
            $data->endingAt = $this->convertToJavaDate($end);
            
            $res = $this->getApi()->getPmsManager()->generateRepeatDateRanges($this->getSelectedName(), $data);
            foreach($res as $r) {
                $dates[] = strtotime($r->start);
            }
        }
        if($_POST['data']['periode_selection'] == "alldays") {
            $toAdd = $start;
            while(true) {
                $dates[] = $toAdd;
                $toAdd = strtotime('+1 day', $toAdd);
                if($toAdd > $end || (date("d.m.Y", $toAdd) == date("d.m.Y", $end))) {
                    break;
                }
            }
        }
        
        
        foreach($dates as $date) {
            $object = new \stdClass();
            $object->count = $addonItem->count;
            if($_POST['data']['isperguest'] == "true") {
                $object->count = $room->numberOfGuests;
            }
            $object->price = $addonItem->price;
            $result[$date] = $object;
        }
        
        return $result;
    }

    public function printCards($userId) {
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        foreach($user->savedCards as $card) {
            echo "<i class='fa fa-trash-o deletecardbutton' cardid='".$card->id."' userid='$userId'></i> " . $card->mask . " - " . $card->expireMonth . "/" . $card->expireYear . "<br>";
        }
    }

    public function getusersTable() {
        $users = $this->getApi()->getPmsManager()->getAllUsers($this->getSelectedName(), $this->getSelectedFilter());
        $filter = $this->getAccountsSubFilter();
        $newUsers = array();
        $methods = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        foreach($users as $user) {
            if(isset($filter['companiesOnly']) && $filter['companiesOnly'] == "true" && $user->customerType !== "Company" && $user->customerType !== "Subuser") {
                continue;
            }
            if(isset($filter['companiesOnly']) && $user->numberOfBookings < $filter['numberOfBookings']) {
                continue;
            }
            if(isset($filter['lastBooked']) && $filter['lastBooked'] && strtotime($filter['lastBooked']) > strtotime($user->latestBooking)) {
                continue;
            }
            $user->preferredPaymentTypeReadable = "default";
            if(sizeof((array)$methods) > 0) {
                foreach($methods as $met) {
                    if($met->id == $user->preferredPaymentType) {
                        $user->preferredPaymentTypeReadable = $met->appName;
                    }
                }
            }

            
            $newUsers[] = $user;
        }
        return $newUsers;
    }

    public function getChartData($entries, $filter, $type) {
        $result = array();
        if($type == "roomguest") {
            $axis = array();
            $axis[] = "Day";
            $axis[] = "Utleid";
            $axis[] = "Gjester";
            $result[] = $axis;
        }
        if($type == "revpar") {
            $axis = array();
            $axis[] = "Day";
            $axis[] = "AvgPrice";
            $axis[] = "RevPar";
            $result[] = $axis;
        }
        if($type == "income") {
            $axis = array();
            $axis[] = "Day";
            $axis[] = "Total";
            $result[] = $axis;
        }
        if($type == "coverage") {
            $axis = array();
            $axis[] = "Day";
            $axis[] = "Coverage";
            $result[] = $axis;
        }
        
        foreach($entries as $entry) {
            /* @var $entry \core_pmsmanager_StatisticsEntry */
            if($entry->date) {
                $day = $this->getDayText($entry->date, $filter->timeInterval);
                $day = str_replace(".2016", "", $day);
                $day = str_replace(".2017", "", $day);
                $day = str_replace(".2018", "", $day);
                $day = str_replace(".2019", "", $day);
                $day = str_replace(".2020", "", $day);
                $day = str_replace(".2021", "", $day);
                $day = str_replace("<br>", " - ", $day);
                
                $row = array();
                if($type == "roomguest") {
                    $row[] = $day;
                    $row[] = $entry->roomsRentedOut;
                    $row[] = $entry->guestCount;
                }
                if($type == "revpar") {
                    $row[] = $day;
                    $row[] = $entry->avgPrice;
                    $row[] = $entry->revPar;
                }
                if($type == "income") {
                    $row[] = $day;
                    $row[] = $entry->totalPrice;
                }
                if($type == "coverage") {
                    $row[] = $day;
                    $row[] = $entry->coverage;
                }
                
                $result[] = $row;
            }
        }
        
        return json_encode($result);
    }

    /**
     * @param \core_ordermanager_data_Order $order
     */
    public function getChargeDate($order) {
        $start = null;
        $config = $this->getManager()->getConfiguration($this->getSelectedName());
        foreach($order->cart->items as $item) {
            if($start == null || $start > strtotime($item->startDate)) {
                $start = strtotime($item->startDate);
            }
        }
        return strtotime(date("d.m.Y", $start) . " -".$config->numberOfDaysToSendPaymentLinkAheadOfStay."days");
    }

}
?>

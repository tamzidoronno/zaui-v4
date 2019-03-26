<?php
namespace ns_74220775_43f4_41de_9d6e_64a189d17e35;

class PmsNewBooking extends \WebshopApplication implements \Application {
    public $canNotAddConferenceRoom = false;
    private $products = array();
    /* @var $this->selectedBooking \core_pmsmanager_PmsBooking */
    private $selectedBooking;
    
    
    public function getDescription() {
        
    }
    
    public function formatNumberOfGuests($row) {
        $guests = $row->numberOfGuests;
        return "<input type='txt' class='gsniceinput1 guestcounter' roomid='".$row->roomid."' value='". $guests . "'>";
    }
    
    public function formatRoomPrice($row) {
        $guests = $row->price;
        $disabled = "";
        if($this->useDefaultPrices()) {
            $disabled = "DISABLED";
        }
        return "<input type='txt' class='gsniceinput1 roomprice' roomid='".$row->roomid."' value='". $guests . "' $disabled>";
    }
    
    public function updateGuestCount() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                $r->numberOfGuests = $_POST['data']['count'];
                $r->guests = $this->createGuestList($r);
            }
        }
        
        $keepPrices = !$this->useDefaultPrices();
        
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, $keepPrices);
        $this->getApi()->getPmsManager()->setDefaultAddons($this->getSelectedMultilevelDomainName(), $booking->id);
        
        $this->printRowPriceUpdate();
    }
    
    
    public function updateRoomPrice() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                foreach($r->priceMatrix as $day => $price) {
                    $r->priceMatrix->{$day} = $_POST['data']['newprice'];
                }
                $r->price = $_POST['data']['newprice'];
            }
        }
        
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, true);
        $this->printRowPriceUpdate();
    }
    
    public function setDiscountCode() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->couponCode = $_POST['data']['code'];
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
        $codes = $this->getApi()->getCartManager()->getCoupons();
        $found = false;
        foreach($codes as $code) {
            if($booking->couponCode == $code->code) {
                $found = true;
            }
        }
        if($found) {
            echo "yes";
        } else {
            echo "no";
        }
    }

    public function searchExistingCustomer() {
        $users = $this->getApi()->getUserManager()->findUsers($_POST['data']['searchword']);
        echo "<table>";
        foreach($users as $user) {
            echo "<tr>";
            echo "<td>" . $user->fullName . "</td>";
            echo "<td>" . $user->emailAddress . "</td>";
            echo "<td>" ."(" . $user->prefix. ")". $user->cellPhone . "</td>";
            echo "<td><span class='shop_button' gstype='clicksubmit' method='completeBookingByExistingUser' gsvalue='".$user->id."' gsname='id'>Select</span></td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function completeBookingByExistingUser() {
        $this->completeByUser($_POST['data']['id']);
    }
    
    public function searchbrreg() {
        $company = $this->getApi()->getUtilManager()->getCompaniesFromBrReg($_POST['data']['name']);
        echo json_encode($company);
    } 
   
    public function getName() {
        return "PmsNewBooking";
    }
    
    public function addConferenceRoom() {
        $start = strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']);
        $end = strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']);
        
        $start = $this->convertToJavaDate($start);
        $end = $this->convertToJavaDate($end);
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $_POST['data']['item']);
        
        $booking = new \core_bookingengine_data_Booking();
        $booking->bookingItemId = $item->id;
        $booking->bookingItemTypeId = $item->bookingItemTypeId;
        $booking->startDate = $start;
        $booking->endDate = $end;

        if(!$this->getApi()->getBookingEngine()->canAddBooking($this->getSelectedMultilevelDomainName(), $booking)) {
            $this->canNotAddConferenceRoom = true;
        } else {
            $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
            $room = new \core_pmsmanager_PmsBookingRooms();
            $room->date = new \core_pmsmanager_PmsBookingDateRange();
            $room->date->start = $start;
            $room->date->end = $end;
            $room->numberOfGuests = 1;
            $room->bookingItemId = $item->id;
            $room->bookingItemTypeId = $item->bookingItemTypeId;
            $room->priceMatrix = $this->getApi()->getPmsInvoiceManager()->calculatePriceMatrix($this->getSelectedMultilevelDomainName(), $currentBooking, $room);
            $currentBooking->rooms[] = $room;
            $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
        }
    }
    
    public function render() {
        if($_SERVER['PHP_SELF'] == "/json.php" || isset($_SESSION['firstloadpage'])) {
            $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
            $app->setStartDate(date("d.m.Y", time()));
            $app->setEndDate(date("d.m.Y", time()+86400));
        }
        
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedMultilevelDomainName());
        if(sizeof($items) == 0) {
            $this->includefile("noroomscreatedyet");
            return;
        }
        
        if(isset($this->msg)) {
            echo $this->msg;
        }
        $conferncerooms = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), 1);
        $hasConferenceClass = "";
        if(sizeof($conferncerooms) > 0) {
            $hasConferenceClass = "hasconference";
        }
        
//        if($this->getApi()->getStoreManager()->getMyStore()->id == "93fadf31-1039-4196-9e4f-77c3be0ef8d1") {
            echo "<div style='text-align:center; margin-top: 30px; margin-bottom: 30px;'>";
            echo "<div style='margin:auto; background-color:green; color:#fff; display:inline-block; width: 800px; padding: 10px; font-size:20px;'>The next version of this page is possible to find <a href='/pms.php?page=048e2e10-1be3-4d77-a235-4b47e3ebfaab'>here</a><br>";
            echo "It will replace this page at 19.04.2019, so please make sure you look at it<br>All feedback for of the new page can be sent to post@getshop.com</div>";
            echo "</div>";
//        }
        
        echo "<div style='max-width:1500px; margin:auto;' class='dontExpand'>";
        echo "<div class='addnewroom $hasConferenceClass'>";
        $this->includefile("newbooking");
        echo "</div>";
        if($hasConferenceClass) {
            echo "<div class='conferenceroom'>";
            $this->includefile("conferencerooms");
            echo "</div>";
        }
        echo "<div class='availablerooms $hasConferenceClass'>";
        $this->showAvailableRooms();
        echo "</div>";
        echo "<div style='clear:both;'></div>";
        $this->includefile("roomsadded");
        echo "<div style='clear:both;'></div>";
        echo "</div>";
    }
    
    public function registerprivateperson() {
        $registerUser = new \RegistrateUsers();
        $this->completeByUser($registerUser->registerprivateperson($this->getApi()));
    }
    
    public function showAvailableRooms() {
        $this->includefile("availablerooms");
    }
    
    public function reloadAvailability() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $app->setStartDate($_POST['data']['start']);
        $app->setEndDate($_POST['data']['end']);
        $this->includefile("availablerooms");
    }
    
    public function checkClosedRooms() {
        $this->includefile("closedwarning");
    }
    
    public function completequickreservation() {
        $this->completeByUser($this->createSetUser());
        $this->clearUseDefaultPrices();
    }
    
    private function completeByUser($userId) {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $currentBooking->userId = $userId;
        $currentBooking->quickReservation = true;
        $currentBooking->avoidCreateInvoice = true;
        $keepPrices = !$this->useDefaultPrices();
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $currentBooking, $keepPrices);
        $res = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedMultilevelDomainName());
        unset($_SESSION['currentgroupbookedarea']);
        if(!$res) {
            $this->msg = "";
            $this->msg .= "<div style='border: solid 1px; background-color:red; padding: 10px; font-size: 16px; color:#fff;'>";
            $this->msg .= "<i class='fa fa-warning'></i> ";
            $this->msg .= "Unable to comply, your selection is not possible.";
            $this->msg .= "</div>";
        } else {
            $bookingEngineId = "";
            foreach($res->rooms as $room) {
                $bookingEngineId = $room->bookingId;
                if($bookingEngineId) {
                    break;
                }
            }
            $this->createAndSendPaymentLink($currentBooking); 
            $this->msg = "";
            $this->msg .= "<script>";
            $this->msg .= "window.location.href='/pms.php?page=a90a9031-b67d-4d98-b034-f8c201a8f496&loadBooking=".$res->id."';";
            $this->msg .= "</script>";
        }
    }
    
    public function removeRoom() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $newRoomList = array();
        foreach($currentBooking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['id']) {
                continue;
            }
            $newRoomList[] = $room;
        }
        $currentBooking->rooms = $newRoomList;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
    }
    
    public function getStartDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getStartDate());
    }
    
    public function loadResult() {
        
    }
    
    public function getEndDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getEndDate());
    }
    
    public function addRoom() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $app->setStartDate($_POST['data']['from']);
        $app->setEndDate($_POST['data']['to']);
        
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $currentBooking->couponCode = $_POST['data']['discountcode'];
        
        foreach($currentBooking->rooms as $room) {
            $room->addedToWaitingList = false;
        }
        
        $prefix = sizeof($currentBooking->rooms);
        foreach($types as $type) {
            $from = $_POST['data']['from'];
            $to = $_POST['data']['to'];
            $numberOfRooms = $_POST['data']['count_'.$type->id];

            $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
            $start = $this->convertToJavaDate(strtotime($from. " " . $config->defaultStart));
            $end = $this->convertToJavaDate(strtotime($to. " " . $config->defaultEnd));

            $available = $this->getNumberOfAvailableForType($type, $currentBooking, $start, $end);
            for($i = 0; $i < $numberOfRooms; $i++) {
                $room = new \core_pmsmanager_PmsBookingRooms();
                $room->date = new \core_pmsmanager_PmsBookingDateRange();
                $room->date->start = $start;
                $room->date->end = $end;
                $room->bookingItemTypeId = $type->id;
                $room->numberOfGuests = 1;
                $room->guests = $this->createGuestList($room);
                if($i >= $available) {
                    $room->addedToWaitingList = true;
                }
                $room->priceMatrix = $this->getApi()->getPmsInvoiceManager()->calculatePriceMatrix($this->getSelectedMultilevelDomainName(), $currentBooking, $room);
                $avg = 0;
                $days = 0;
                foreach($room->priceMatrix as $d => $price) {
                    $days++;
                    $avg += $price;
                }
                if($avg != 0) {
                    $room->price = $avg / $days;
                } else {
                    $room->price = 0;
                }
                $currentBooking->rooms[] = $room;
            }
        }
        
        $currentBooking->setGuestsSameAsBooker = false;
        
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $currentBooking, true);
        $this->getApi()->getPmsManager()->setDefaultAddons($this->getSelectedMultilevelDomainName(), $currentBooking->id);
    }

    public function saveGuestInformation() {
        $room = $this->getRoom($_POST['data']['roomid']);
        /* @var $booking core_pmsmanager_PmsBooking */
        $booking = $this->selectedBooking;
        $guestData = null;
        foreach($booking->rooms as $room) {
            foreach($room->guests as $guest) {
                if($guest->guestId == $_POST['data']['guestid']) {
                    $guestData = $guest;
                    $guest->email = $_POST['data']['guestinfo']['email'];
                    $guest->prefix = $_POST['data']['guestinfo']['prefix'];
                    $guest->name = $_POST['data']['guestinfo']['name'];
                    $guest->phone = $_POST['data']['guestinfo']['phone'];
                }
            }
        }
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
        $related = (array)$this->getApi()->getPmsManager()->findRelatedGuests($this->getSelectedMultilevelDomainName(), $guestData);
        $i = 0;
        foreach($related as $rel) {
            echo "<span class='guestrowhintentry' userid='".$rel->userId."' email='".$rel->guest->email."' phone='".$rel->guest->phone."' prefix='".$rel->guest->prefix."' name='".$rel->guest->name."'>Name: " . $rel->guest->name .
                    "<br>Phone: (" . $rel->guest->prefix . ")" . $rel->guest->phone .
                    "<br>E-mail: " . $rel->guest->email .
                    "<br>Company: " . $rel->userName.
                    "</span>";
            $i++;
            if($i > 10) {break; }
        }
    }
    
    public function addSuggestedUser() {
        $userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->addSuggestedUserToBooking($this->getSelectedMultilevelDomainName(), $userId);
        $this->printSuggestedUsers();
    }
    
    public function addAddonToBooking() {
        $productId = $_POST['data']['productid'];
        $curbooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        if($_POST['data']['remove'] == "true") {
            foreach($curbooking->rooms as $room) {
                $pmsRoomId = $room->pmsBookingRoomId;
                $this->getApi()->getPmsManager()->removeProductFromRoom($this->getSelectedMultilevelDomainName(), $pmsRoomId,$productId);
            }
        } else {
            foreach($curbooking->rooms as $room) {
                $pmsRoomId = $room->pmsBookingRoomId;
                $this->getApi()->getPmsManager()->addProductToRoom($this->getSelectedMultilevelDomainName(), $productId, $pmsRoomId, -1);
            }
        }
        
    }
    
    public function removeAllSelectedRooms() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->rooms = array();
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function getNumberOfAvailableForType($type,$current,$start,$end) {
        $size = $this->getApi()->getPmsManager()->getNumberOfAvailable($this->getSelectedMultilevelDomainName(), $type->id, $start, $end, false);

        foreach($current->rooms as $room) {
            if($room->bookingItemTypeId == $type->id) {
                $size--;
            }
        }
        return $size;
    }
    
    public function createNewCompanyCustomer() {
        $registerUser = new \RegistrateUsers();
        $user = $registerUser->createNewCompanyCustomer($this->getApi());
        if(!$user) {
            $this->msg = "";
            $this->msg .= "<div style='border: solid 1px; background-color:red; padding: 10px; font-size: 16px; color:#fff;'>";
            $this->msg .= "<i class='fa fa-warning'></i> ";
            $this->msg .= "Unable to comply, the company you tried to create already exists.";
            $this->msg .= "</div>";
            $this->createdCustomerFailed = true;
        } else {
            unset($_SESSION['currentgroupbookedarea']);
            $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
            $currentBooking->userId = $user->id;
            $currentBooking->quickReservation = true;
            $currentBooking->avoidCreateInvoice = true;
            $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
            $res = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedMultilevelDomainName());
            $this->msg = "";
            $this->msg .= "<script>";
            $this->msg .= "window.location.href='/pms.php?page=a90a9031-b67d-4d98-b034-f8c201a8f496&loadBooking=".$res->id."';";
            $this->msg .= "</script>";
        }
    }
    
    public function createSetUser() {
        $name = "";
        if(isset($_POST['data']['nameofholder'])) {
            $name = $_POST['data']['nameofholder'];
        }
        if(!$name) {
            return "quickreservation";
        }
        
        $user = $this->getApi()->getUserManager()->getUserById($name);
        if($user) {
            return $user->id;
        }
        
        $user = new \core_usermanager_data_User();
        $user->fullName = $name;
        $user = $this->getApi()->getUserManager()->createUser($user);
        return $user->id;
    }
    
    public function checkForExisiting() {
        $reg = new \RegistrateUsers();
        $reg->checkForExisiting($this->getApi());
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBooking $booking
     * @param type $addon
     * @return boolean
     */
    public function isAddedAddon($booking, $addon) {
        foreach($booking->rooms as $room) {
            foreach($room->addons as $addedaddon) {
                if($addedaddon->productId == $addon->productId) {
                    return true;
                }
            }
        }
        return false;        
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


    /**
     * 
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @return string
     */
    public function createAcronymAddonsRow($room) {
        $acronym = "";
        foreach($room->addons as $addon) {
            $product = $this->getproduct($addon->productId);
            $title = $addon->count . " x " . $product->name;
            $acronym .= "<span title='$title'>(" . $addon->count . "x" . $this->getFirstWords($product->name) . ")</span> ";
        }
        return "<div style='padding-left: 10px;'>$acronym</div>";
    }

    public function getproduct($productId) {
        if(isset($this->products[$productId])) {
            return $this->products[$productId];
        }
        $prod = $this->getApi()->getProductManager()->getProduct($productId);
        $this->products[$productId] = $prod;
        return $prod;
    }

    public function toggleUseDefaultPrices() {
        $_SESSION['usedefaultpriceswhenaddingnewroomsinpms'] = $_POST['data']['checked'];
    }
    
    public function useDefaultPrices() {
        if(isset($_SESSION['usedefaultpriceswhenaddingnewroomsinpms'])) {
            return $_SESSION['usedefaultpriceswhenaddingnewroomsinpms'] == "true";
        }
        return true;
    }

    public function printRowPriceUpdate() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $pmsRoom = null;
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                $pmsRoom = $r;
            }
        }
        
        $result = array();
        $result['guestcount'] = $r->numberOfGuests;
        $result['price'] = $pmsRoom->price;
        $result['addons'] = $this->createAcronymAddonsRow($pmsRoom);
        $result['totalcost'] = $booking->totalPrice;
        ob_start();
        $this->printGuestInformation($pmsRoom->pmsBookingRoomId);
        $guestinfo = ob_get_contents();
        ob_end_clean();
        $result['guests'] = $guestinfo;
        echo json_encode($result);
    }

    public function clearUseDefaultPrices() {
        if(isset($_SESSION['usedefaultpriceswhenaddingnewroomsinpms'])) {
            unset($_SESSION['usedefaultpriceswhenaddingnewroomsinpms']);
        }
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function createGuestList($room) {
        $current = array();
        for($i = 0;$i < $room->numberOfGuests; $i++) {
            if(isset($room->guests[$i])) {
                $current[] = $room->guests[$i];
            }
        }
        $diff = $room->numberOfGuests - sizeof($room->guests);
        for($i = 0; $i < $diff;$i++) {
            $guest = new \core_pmsmanager_PmsGuests();
            $current[] = $guest;
        }
        return $current;
    }

    public function printAddedRooms($rows) {
        /*
         * array('type', 'Room type', 'type', null),
         * array('checkin', 'Check in', 'start', null),
         * array('checkout', 'Check out', 'end', null),
         * array('guests', 'Guest count', 'numberOfGuests', "formatNumberOfGuests"),
         * array('available', 'Available', 'available', null),
         * array('price', 'Room price', 'price', "formatRoomPrice"),
         * array('cost', 'Cost', 'cost', null),
         * array('sameAsBooker', 'Same as booker', 'cost', null)
         */
        foreach($rows as $row) {
            echo "<div class='roominformation datarow'>";
            echo "<span class='col col_5'>" . $row->available . "</span>";
            echo "<span class='col col_1'>" . $row->type . "</span>";
            echo "<span class='col col_2'>" . $row->start . "</span>";
            echo "<span class='col col_3'>" . $row->end . "</span>";
            echo "<span class='col col_4'>" . $this->formatNumberOfGuests($row) . " guests </span>";
            echo "<span class='col col_6'>" . $this->formatRoomPrice($row) . " per night</span>";
            echo "<span class='col col_7 col_cost'>" . $row->cost . "</span>";
            echo "</div>";
            echo "<div class='guestinformation' roomid='".$row->roomid."'>";
            $this->printGuestInformation($row->roomid);
            echo "</div>";
        }
    }

    public function updateSameAsBooker() {
    $pmsRoom = $this->getRoom($_POST['data']['roomid']);
    $this->selectedBooking->setGuestsSameAsBooker = $_POST['data']['sameasbooker'] == "true";
    $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $this->selectedBooking);
    $result = array();
    ob_start();
    $this->printGuestInformation($pmsRoom->pmsBookingRoomId);
    $guestinfo = ob_get_contents();
    ob_end_clean();
    $result['guests'] = $guestinfo;
    echo json_encode($result);
    }
    
    public function printGuestInformation($roomId) {
        $room = $this->getRoom($roomId);
        $setGuestsSameAsBooker = $this->selectedBooking->setGuestsSameAsBooker;
        $hideGuests = $setGuestsSameAsBooker ? "style='display:none;'" : "";
        $checkedBox = $setGuestsSameAsBooker ? "checked='checked'" : "";
        echo "<label class='sameasbooker'>";
        echo "<input type='checkbox' class='sameasbookerinfocheckbox' roomid='".$room->pmsBookingRoomId."' $checkedBox> Use same information for guest(s) as for booker.";
        echo "</label>";
        
        foreach($room->guests as $guest) {
            $name = @$guest->name ? $guest->name : "";
            $email = @$guest->email ? $guest->email : "";
            $prefix = @$guest->prefix ? $guest->prefix : "";
            $phone = @$guest->phone ? $guest->phone : "";
            echo "<div class='guestrow' guestId='".$guest->guestId."' $hideGuests>";
            echo "<input type='text' class='gsniceinput1 guestname' placeholder='Guest name' value='".$name."'> ";
            echo "<input type='text' class='gsniceinput1 guestemail' placeholder='Guest email' value='".$email."'> ";
            echo "<input type='text' class='gsniceinput1 guestprefix' placeholder='47'  value='".$prefix."'> ";
            echo "<input type='text' class='gsniceinput1 guestphone' placeholder='Guest phone number' value='".$phone."'>";
            echo "<div class='guestrowhint'></div>";
            echo "</div>";
        }
    }

    /**
     * 
     * @param type $roomId
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getRoom($roomId) {
        if(!$this->selectedBooking) {
            $this->selectedBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        }
        foreach($this->selectedBooking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomId) {
                return $room;
            }
        }
        return null;
    }
    
    public function toggleCreateOrderAndSendPaymentLink() {
        $_SESSION['toggleCreateOrderAndSendPaymentLink'] = $_POST['data']['checked'] == "true";
    }
    
    public function doCreateOrderAndSendPaymentLink() {
        if(!isset($_SESSION['toggleCreateOrderAndSendPaymentLink'])) {
            return false;
        }
        return $_SESSION['toggleCreateOrderAndSendPaymentLink'];
    }

    public function createAndSendPaymentLink($booking) {
        if(!$this->doCreateOrderAndSendPaymentLink()) {
            return;
        }
        $bookingId = $booking->id;
        $orderId = $this->getApi()->getPmsInvoiceManager()->createOrderOnUnsettledAmount($this->getSelectedMultilevelDomainName(), $bookingId);
        
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        $email = $user->emailAddress;
        $prefix = $user->prefix;
        $phone = $user->cellPhone;
        
        $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedMultilevelDomainName(), $orderId, $bookingId, $email, $prefix, $phone);
        $this->getApi()->getPmsManager()->doNotification($this->getSelectedMultilevelDomainName(), "booking_completed", $bookingId);
        unset($_SESSION['toggleCreateOrderAndSendPaymentLink']);
    }

    public function completeBookingBySpecificUser() {
        $this->completeByUser($_POST['data']['userid']);
        echo $this->msg;
    }
    
    public function printSuggestedUsers() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $suggestedUsers = (array)$booking->suggestedUserIds;
        if(!$suggestedUsers) {
            return;
        }
        
        echo "<h1>Suggested by above selection</h1>";
        foreach($suggestedUsers as $sugusr) {
            $user = $this->getApi()->getUserManager()->getUserById($sugusr);
            echo "<span class='usersuggestion selecteduser' userid='".$user->id."'>";
            echo "<table>";
            echo "<tr><td>Name</td><td>" . $user->fullName . "</td></tr>";
            echo "<tr><td>Email</td><td>" . $user->emailAddress . "</td></tr>";
            echo "<tr><td>Phone</td><td>" . $user->prefix . " " . $user->cellPhone . "</td></tr>";
            echo "<tr><td>Address</td><td>" . $user->address->address . "</td></tr>";
            echo "<tr><td></td><td>" . $user->address->postCode . " " . $user->address->city . "</td></tr>";
            echo "</table>";
            echo "</span>";
        }
    }

}
?>

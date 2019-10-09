<?php
namespace ns_bf644a39_c932_4e3b_a6c7_f6fd16baa34d;

class PmsNewBooking20 extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsNewBooking20";
    }

    public function searchCustomer() {
        $this->includefile("searchcustomerresult");
    }
    
    public function loadConferenceEvents() {
        $this->includefile("addguesttoconference");
    }
    
    public function printavailability() {
        $this->includefile("availability");
    }
    
     public function getNumberOfAvailableForType($id,$current,$start,$end) {
        $size = $this->getApi()->getPmsManager()->getNumberOfAvailable($this->getSelectedMultilevelDomainName(), $id, $start, $end, false);

        foreach($current->rooms as $room) {
            if($room->bookingItemTypeId == $id) {
                $size--;
            }
        }
        return $size;
    }
    
    public function registerRoomsFromAvailabilityCheck() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        foreach($types as $type) {
            $guestCount = 0;
            if(isset($_POST['data'][$type->id.'_guestcounter'])) {
                $guestCount = $_POST['data'][$type->id.'_guestcounter'];
            }
            $this->addToReadyList($type->id, $_POST['data'][$type->id], $_POST['data']['start'], $_POST['data']['end'], $guestCount);
        }
        $this->printRoomsAddedToReadyList();
        
        echo "<script>";
        echo "$('.roomcount').val(0);";
        echo "</script>";
    }
    public function printRoomsAddedToReadyList() {
        $this->includefile("roomsregisteredfromavailabilitycheck");
    }
    
    public function addconferenceroom() {
        $start = strtotime($_POST['data']['start'] . " " . $_POST['data']['starttime']);
        $end = strtotime($_POST['data']['end'] . " " . $_POST['data']['endtime']);
        
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
    
    public function loadCurrentBooking() {
        $this->includefile("roomsaddedarea");
    }
    
    public function setCouponCode() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->couponCode = $_POST['data']['code'];
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, false);
        $this->printCouponSummary($booking);
    }
    
    public function deleteRoom() {
        $roomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsBookingProcess()->removeRoom($this->getSelectedMultilevelDomainName(), $roomId);
        $this->printRoomsAddedToReadyList();
    }
    
    public function setSource() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->channel = $_POST['data']['source'];
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, false);
    }
    
    public function selectbooking() {
        $curbooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $this->getApi()->getPmsManager()->setCurrentBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);

        foreach($curbooking->rooms as $room) {
            $roomId = $this->getApi()->getPmsBookingProcess()->addBookingItemType($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid'], $room->bookingItemTypeId, $room->date->start, $room->date->end, null);
            $this->getApi()->getPmsBookingProcess()->quickChangeGuestCountForRoom($this->getSelectedMultilevelDomainName(), $roomId, $room->numberOfGuests);
        }
    }
    
    public function addTypesToBooking() {
        $type = $_POST['data']['typeid'];
        $start = $this->convertToJavaDate($_POST['data']['start']);
        $end = $this->convertToJavaDate($_POST['data']['end']);
        foreach($_POST['data']['items'] as $typeId => $count) {
            $this->addTypeToBooking($start, $end, $count, $typeId);
        }
    }
    
    public function loadRoomsAddedToBookingList() {
        $this->includefile("roomsaddedarea");
    }
    
    public function searchBooking() {
        $this->includefile("searchbookingresult");
    }
    
    public function render() {
        echo "<div style='max-width: 1500px; margin: auto;'>";
        $this->includefile("newbookingstep1");
        $this->includefile("newbookingstep2");
        echo "</div>";
        
        echo "<script>";
//        echo "app.PmsNewBooking20.setLastPage();";
        echo "</script>";
    }
    
    public function incraseDecreaseCount() {
        $roomId = $_POST['data']['roomid'];
        $guestCount = $_POST['data']['guestCount'];
        $this->getApi()->getPmsBookingProcess()->quickChangeGuestCountForRoom($this->getSelectedMultilevelDomainName(), $roomId, $guestCount);
        $this->printRoomsAddedToReadyList();
    }
    
    public function updateprice() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                foreach($r->priceMatrix as $day => $price) {
                    $r->priceMatrix->{$day} = $_POST['data']['price'];
                }
                $r->price = $_POST['data']['price'];
                $this->lockPriceOnRoom($r->pmsBookingRoomId);
            }
        }
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, true);
    }
    
    public function changeUser($user) {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->userId = $user->id;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
        $code = $this->getApi()->getPmsManager()->setBestCouponChoiceForCurrentBooking($this->getSelectedMultilevelDomainName());
        echo "<script>";
        echo "$('.couponsummary').html('');";
        echo "$('.addcouponcode').val('" . $code . "');";
        echo "</script>";
    }
    
    public function unlockroom() {
        $this->unlockPriceOnRoom($_POST['data']['roomid']);
    }
    
    public function completeBooking() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $this->getApi()->getPmsBookingProcess()->simpleCompleteCurrentBooking($this->getSelectedMultilevelDomainName());
        echo $booking->id;
    }
    
    public function saveGuestInformation() {
        /* @var $booking core_pmsmanager_PmsBooking */
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());;
        foreach($booking->rooms as $room) {
            foreach($room->guests as $guest) {
                $guest->email = $_POST['data'][$guest->guestId]['email'];
                $guest->prefix = $_POST['data'][$guest->guestId]['prefix'];
                $guest->name = $_POST['data'][$guest->guestId]['name'];
                $guest->phone = $_POST['data'][$guest->guestId]['phone'];
            }
        }
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function searchfreerom() {
        $this->includefile("freeroomresult");
    }
    
    public function removeRoomFromBooking() {
        $this->getApi()->getPmsManager()->removeFromCurrentBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['roomid']);
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $user = $this->getApi()->getUserManager()->createCompany($vat, $name);
        return $user;
    }
    
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user = $this->getApi()->getUserManager()->createUser($user);
        return $user;
    }
    
    public function findSuggestion() {
        $guest = new \core_pmsmanager_PmsGuests();
        switch($_POST['data']['type']) {
            case "name": 
                $guest->name = $_POST['data']['value'];
                break;
            case "phone": 
                $guest->phone = $_POST['data']['phone'];
                break;
            case "email": 
                $guest->email = $_POST['data']['email'];
                break;
        }
        $suggestions = $this->getApi()->getPmsManager()->findRelatedGuests($this->getSelectedMultilevelDomainName(), $guest);
        $this->printSuggestions($suggestions);
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function saveUser($user) {
        $this->getApi()->getUserManager()->saveUser($user);
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->getApi()->getPmsManager()->setBestCouponChoiceForCurrentBooking($this->getSelectedMultilevelDomainName());
    }

    public function translateDiscountType($type) {
        $types = array();
        $types[0] = "%";
        $types[1] = "fixed price";
        return $types[$type];
    }
    
    public function selectUser() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
        $this->getApi()->getPmsManager()->setBestCouponChoiceForCurrentBooking($this->getSelectedMultilevelDomainName());
    }

    public function removeGuestFromRoom() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                $guests = array();
                foreach($r->guests as $g) {
                    if($g->guestId == $_POST['data']['guestid']) {
                        continue;
                    }
                    $guests[] = $g;
                }
                $r->numberOfGuests = sizeof($guests);
                $r->guests = $guests;
            }
        }
        
        $updatePrice = $this->hasLockedPrice($_POST['data']['roomid']);
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, $updatePrice);
        $this->getApi()->getPmsManager()->setDefaultAddons($this->getSelectedMultilevelDomainName(), $booking->id);
    }
    
    public function addGuestToRoom() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $_POST['data']['roomid']) {
                for($i = 0; $i < $_POST['data']['gscount'];$i++) {
                    $r->numberOfGuests++;
                    $guest = new \core_pmsmanager_PmsGuests();
                    $guest->guestId = uniqid();
                    $r->guests[] = $guest;
                }
            }
        }
        
        $updatePrice = $this->hasLockedPrice($_POST['data']['roomid']);
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, $updatePrice);
        $this->getApi()->getPmsManager()->setDefaultAddons($this->getSelectedMultilevelDomainName(), $booking->id);
    }

    public function addAddonToRoom() {
        $productId = $_POST['data']['productid'];
        $pmsRoomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->addProductToRoom($this->getSelectedMultilevelDomainName(), $productId, $pmsRoomId, -1);
    }
    
    public function printSuggestions($suggestions) {
        $suggestioncount = 0;
        $suggestions =  (array)$suggestions;
        foreach($suggestions as $suggestion) {
            if(!$suggestion->guest->name) {
                continue;
            }
            ?>
            <div class='guestsuggestionrow'>
                <i class='fa fa-arrow-circle-up addsuggestionarrow'></i>
            <span class='guestsuggestionname'><?php echo $suggestion->guest->name; ?></span>
            <span class='guestsuggestionemail'><?php echo $suggestion->guest->email; ?></span>
            <span class='guestsuggestionphone'><?php echo "(<span class='guestsuggestionphoneprefix'>" . $suggestion->guest->prefix . "</span>)<span class='guestsuggestionphonenumber'>" . $suggestion->guest->phone; ?></span></span>
            </div>
            <?php
            $suggestioncount++;
            if($suggestioncount >= 20) {
                break;
            }
        }
    }

    public function lockPriceOnRoom($roomId) {
        if(!isset($_SESSION['lockpriceonroom'])) {
            $_SESSION['lockpriceonroom'] = array();
        }
        $_SESSION['lockpriceonroom'][$roomId] = true;
    }
    
    public function unlockPriceOnRoom($roomId) {
        unset($_SESSION['lockpriceonroom'][$roomId]);
    }
    
    public function hasLockedPrice($roomId) {
        if(isset($_SESSION['lockpriceonroom'][$roomId])) {
            return true;
        }
        return false;
    }

    /**
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function getAddonsCount($room) {
        $count = 0;
        foreach($room->addons as $addon) {
            $count += $addon->count;
        }
        return $count;
    }

    /**
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function getAddonsPrice($room) {
        $price = 0;
        foreach($room->addons as $addon) {
            $price += $addon->price * $addon->count;
        }
        return $price;
    }

    /**
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function printAddonsAdded($room) {
        echo "<div class='addedaddonspanel'>";
        foreach($room->addons as $addon) {
            echo "<div class='addedaddonsrow' addonid='".$addon->addonId."'>";
            $product = $this->getApi()->getProductManager()->getProduct($addon->productId);
            $name = $addon->name ? $addon->name : $product->name;
            $price = $addon->price;
            if($addon->percentagePrice > 0) {
                $price = $addon->percentagePrice . "%";
            }
            echo "<i class='fa fa-trash-o removeaddon' addonid='".$addon->addonId."' gsclick='removeAddon' synchron='true' roomid='".$room->pmsBookingRoomId."' gs_callback='app.PmsNewBooking20.addonRemove'></i> " . date("d.m.Y", strtotime($addon->date)) . " - " . $name . " - " . $addon->count . " - " . $price;
            echo "</div>";
        }
        echo "<div class='closeaddedaddonspanel'>Close</div>";
        echo "</div>";
        ?>
        <style>
            .addedaddonspanel { position:absolute; background-color: #fff; border: solid 1px #bbb; border-bottom: 0px; display:none; }
            .addedaddonspanel .addedaddonsrow { padding: 6px; border-bottom: solid 1px #bbb; }
            .addedaddonspanel .addedaddonsrow:hover { background-color:#efefef; }
            .addedaddonspanel .addedaddonsrow .removeaddon { cursor: pointer; }
            .addedaddonspanel .closeaddedaddonspanel { text-align: center; padding: 6px; border-bottom: solid 1px #bbb; cursor:pointer; }
        </style>
        <?php
    }
    
    public function removeAddon() {
        $id = $_POST['data']['addonid'];
        $roomid = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->removeAddonFromRoomById($this->getSelectedMultilevelDomainName(), $id, $roomid);
        $res = array();
        $res['id'] = $id;
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $r) {
            if($r->pmsBookingRoomId == $roomid) {
                $res['count'] = $this->getAddonsCount($r);
            }
        }
        echo json_encode($res);
    }

    public function quickReserveBookingList() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->userId = "quickreservation";
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, true);
        $this->completeBooking();
    }
    
    public function removefromreadyroomlist() {
        $typeid = $_POST['data']['typeid'];
        $time = $_POST['data']['time'];
        $list = $this->getReadyToAddList();
        unset($list[$time][$typeid]);
        if(!isset($list[$time]) || sizeof($list[$time]) == 0) {
            unset($list[$time]);
        }
        $_SESSION['pmsnewbooking20_readylist'] = $list;
        $res = array();
        $res['type'] = $typeid;
        $res['time'] = $time;
        $res['empty'] = !$list;
        echo json_encode($res);
        
    }
    
    public function addToReadyList($type, $count, $start, $end, $guestCount) {
        if($count == 0) {
            return;
        }
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $bookingId = $booking->id;
        $start = $this->convertToJavaDate((int)$start);
        $end = $this->convertToJavaDate((int)$end);
        for($i = 0; $i < $count; $i++) {
            $guestInfoFromRoom = array();
            $roomId = $this->getApi()->getPmsBookingProcess()->addBookingItemType($this->getSelectedMultilevelDomainName(), $bookingId, $type, $start, $end, null);
            $this->getApi()->getPmsBookingProcess()->quickChangeGuestCountForRoom($this->getSelectedMultilevelDomainName(), $roomId, $guestCount);
        }
    }
    
    public function resetBooking() {
        $this->clearRoomList();
    }
    
    public function clearRoomList() {
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedMultilevelDomainName());
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        if($config->defaultRegistrationLanguage) {
            $booking->language = $config->defaultRegistrationLanguage;
        }
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function searchForEvent() {
        $filter = new \core_pmsmanager_PmsConferenceEventFilter();
        $filter->keyword = $_POST['data']['keyword'];
        $events = (array)$this->getApi()->getPmsConferenceManager()->getConferenceEventsByFilter($filter);
        $this->printEvents($events);
    }
    
    public function getReadyToAddList() {
         if(!isset($_SESSION['pmsnewbooking20_readylist'])) {
            $_SESSION['pmsnewbooking20_readylist'] = array();
        }
        return $_SESSION['pmsnewbooking20_readylist'];
    }

    public function addReadyToAddListToBooking() {
        
    }

    public function addTypeToBooking($start, $end, $count, $typeId) {
        $bookingengine = $this->getSelectedMultilevelDomainName();
        $bookingId = $this->getApi()->getPmsManager()->getCurrentBooking($bookingengine)->id;
        for($i = 0; $i < $count; $i++) {
            $this->getApi()->getPmsBookingProcess()->addBookingItemType($bookingengine, $bookingId, $typeId, $start, $end, "");
        }
    }

    public function printEvents($events, $currentPmsEventIds = array()) {
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
            if(in_array($event->id, $currentPmsEventIds)) {
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
                echo "<td><span style='cursor:pointer;' class='attachguesttoevent' eventid='".$event->id."'>Remove</span></td>";
            }
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function attachGuestToEvent() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $guestInvolved = null;
        foreach($booking->rooms as $room) {
            foreach($room->guests as $guest) {
                if($guest->guestId == $_POST['data']['guestid']) {
                    $guestInvolved = $guest;
                    $array = (array)$guest->pmsConferenceEventIds;
                    if(!in_array($_POST['data']['eventid'], $array)) {
                        $array[] = $_POST['data']['eventid'];
                        $guest->pmsConferenceEventIds = $array;
                    } else {
                        $del_val = $_POST['data']['eventid'];
                        $newArray = array();
                        foreach($array as $val) {
                            if($val == $_POST['data']['eventid']) {
                                continue;
                            }
                            $newArray[] = $val;
                        }
                        $guest->pmsConferenceEventIds = $newArray;
                    }
                    
                }
            }
        }
        if(sizeof($guestInvolved->pmsConferenceEventIds) > 0) { echo sizeof($guestInvolved->pmsConferenceEventIds); }
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, true);
    }

    public function checkIfClosedPeriodes($start, $end) {
        $closedtext = "";
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $selectedStartMillis = strtotime($start);
        $selectedEndMillis = strtotime($end);

        foreach($config->closedOfPeriode as $row) {
            $startTime = strtotime($row->firstEvent->start);
            $endTime = strtotime($row->firstEvent->end);

            $found = false;
            if($selectedStartMillis <= $startTime && $startTime <= $selectedEndMillis) {
                //Start is in timerange
                $found = true;
            }
            if($selectedStartMillis <= $endTime && $endTime <= $selectedEndMillis) {
                //End is in timerange
                $found = true;
            }
            if($selectedStartMillis >= $startTime && $endTime >= $selectedEndMillis) {
                //End is in timerange
                $found = true;
            }

            if($found) {
                $closedtext .= "<div style='padding-left:5px;'>";
                $closedtext .= date("d.m.Y", strtotime($row->firstEvent->start)) . " - ";
                $closedtext .= date("d.m.Y", strtotime($row->firstEvent->end));
                $closedtext .= "</div>";
            }
        }
        return $closedtext;
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBooking $booking
     */
    public function printCouponCodes($booking) {
        $couponcodesfetched = $this->getApi()->getCartManager()->getCoupons();
        
        foreach($couponcodesfetched as $code) {
            $couponcodes[$code->code] = $code;
        }
        $discounts = $this->getApi()->getPmsInvoiceManager()->getDiscountsForUser($this->getSelectedMultilevelDomainName(), $booking->userId);
        
        
        $alreadyAdded = array();
        if($discounts->attachedDiscountCode || sizeof((array)$discounts->secondaryAttachedDiscountCodes) > 0) {
            echo "<optgroup label='Discount code connected to this user'>";
            if($discounts->attachedDiscountCode) {
                $selected = $booking->couponCode == $discounts->attachedDiscountCode ? "SELECTED" : "";
                $coupon = $couponcodes[$discounts->attachedDiscountCode];
                $description = $coupon->description;
                if($description) { $description = " (" . $description . ")"; }
                echo "<option value='".$coupon->code."' $selected>" .$coupon->code . $description . "</option>";
                $alreadyAdded[] = $discounts->attachedDiscountCode;
            }
            foreach($discounts->secondaryAttachedDiscountCodes as $code) {
                $selected = $booking->couponCode == $code ? "SELECTED" : "";
                $coupon = $couponcodes[$code];
                $description = $coupon->description;
                if($description) { $description = " (" . $description . ")"; }
                echo "<option value='".$code."' $selected>" .$code . $description. "</option>";
                $alreadyAdded[] = $code;
            }
            echo "</optgroup>";
        }
        
        echo "<optgroup label='Other discount codes'>";
        echo "<option value=''>Choose a discount code</option>";
        foreach($couponcodes as $code) {
            if(in_array($code->code, $alreadyAdded)) {
                continue;
            }
            $description = $code->description;
            if($description) { $description = " (" . $description . ")"; }
            $selected = $code->code == $booking->couponCode ? "SELECTED" : "";
            echo "<option value='".$code->code."' $selected>" .$code->code .$description. "</option>";
        }
        echo "</optgroup>";

    }

    public function printCouponSummary($booking) {
         $pmspricing = new \ns_1be25b17_c17e_4308_be55_ae2988fecc7c\PmsPricing();
        $coupon = null;
        if($booking->couponCode) {
            $coupon = $this->getApi()->getCartManager()->getCoupon($booking->couponCode);
        }
        if($coupon) { echo $coupon->code . " - " . $coupon->type . " - " . $pmspricing->getRepeatingSummary($coupon);
        if($coupon->description) {
            echo "<div>" . $coupon->description . "</div>";
        }
        
        }
    }

}
?>
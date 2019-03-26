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
    
    public function printavailability() {
        $this->includefile("availability");
    }
    
    public function registerRoomsFromAvailabilityCheck() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        foreach($types as $type) {
            $this->addToReadyList($type->id, $_POST['data'][$type->id], $_POST['data']['start'], $_POST['data']['end']);
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
    
    public function selectbooking() {
        $this->getApi()->getPmsManager()->setCurrentBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
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
        
    }
    
    public function unlockroom() {
        $this->unlockPriceOnRoom($_POST['data']['roomid']);
    }
    
    public function completeBooking() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $this->getApi()->getPmsManager()->simpleCompleteCurrentBooking($this->getSelectedMultilevelDomainName());
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
        
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedMultilevelDomainName());
        $booking->userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
    }

    public function translateDiscountType($type) {
        $types = array();
        $types[0] = "%";
        $types[1] = "fixed price";
        return $types[$type];
    }
    
    public function selectUser() {
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedMultilevelDomainName());
        $booking->userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
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
                $r->numberOfGuests++;
                $guest = new \core_pmsmanager_PmsGuests();
                $guest->guestId = uniqid();
                $r->guests[] = $guest;
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
            echo "<i class='fa fa-trash-o removeaddon' addonid='".$addon->addonId."' gsclick='removeAddon' synchron='true' roomid='".$room->pmsBookingRoomId."' gs_callback='app.PmsNewBooking20.addonRemove'></i> " . date("d.m.Y", strtotime($addon->date)) . " - " . $name . " - " . $addon->count . " - " . $addon->price;
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
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedMultilevelDomainName());
        $booking->userId = "quickreservation";
        $this->getApi()->getPmsManager()->setBookingByAdmin($this->getSelectedMultilevelDomainName(), $booking, true);
        $this->addReadyToAddListToBooking();
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
    
    public function addToReadyList($type, $count, $start, $end) {
        if($count == 0) {
            return;
        }
        if(!isset($_SESSION['pmsnewbooking20_readylist'])) {
            $_SESSION['pmsnewbooking20_readylist'] = array();
        }

        $time = $start . "_" . $end;
        
        $counter = 0;
        if(!isset($_SESSION['pmsnewbooking20_readylist'][$time])) {
            $_SESSION['pmsnewbooking20_readylist'][$time] = array();
        }
        if(isset($_SESSION['pmsnewbooking20_readylist'][$time][$type])) {
            $counter = $_SESSION['pmsnewbooking20_readylist'][$time][$type];
        }
        $counter += $count;
        $_SESSION['pmsnewbooking20_readylist'][$time][$type] = $counter;
    }
    
    public function clearRoomList() {
        unset($_SESSION['pmsnewbooking20_readylist']);
    }
    
    public function getReadyToAddList() {
         if(!isset($_SESSION['pmsnewbooking20_readylist'])) {
            $_SESSION['pmsnewbooking20_readylist'] = array();
        }
        return $_SESSION['pmsnewbooking20_readylist'];
    }

    public function addReadyToAddListToBooking() {
        $list = $this->getReadyToAddList();
        foreach($list as $time => $typesToAdd) {
            $timePart = explode("_", $time);
            $start = $this->convertToJavaDate($timePart[0]);
            $end = $this->convertToJavaDate($timePart[1]);
            foreach($typesToAdd as $typeId => $count) {
                $this->addTypeToBooking($start, $end, $count, $typeId);
            }
        }
        $this->clearRoomList();
    }

    public function addTypeToBooking($start, $end, $count, $typeId) {
        $bookingengine = $this->getSelectedMultilevelDomainName();
        $bookingId = $this->getApi()->getPmsManager()->getCurrentBooking($bookingengine)->id;
        for($i = 0; $i < $count; $i++) {
            $this->getApi()->getPmsManager()->addBookingItemType($bookingengine, $bookingId, $typeId, $start, $end, "");
        }
    }

}
?>

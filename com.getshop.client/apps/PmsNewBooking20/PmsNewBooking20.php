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
    
    public function loadCurrentBooking() {
        $this->includefile("roomsaddedarea");
    }
    
    public function addTypesToBooking() {
        $type = $_POST['data']['typeid'];
        $start = $this->convertToJavaDate($_POST['data']['start']);
        $end = $this->convertToJavaDate($_POST['data']['end']);
        $bookingengine = $this->getSelectedMultilevelDomainName();
        $bookingId = $this->getApi()->getPmsManager()->getCurrentBooking($bookingengine)->id;
        for($i = 0; $i < $_POST['data']['count']; $i++) {
            $this->getApi()->getPmsManager()->addBookingItemType($bookingengine, $bookingId, $type, $start, $end, "");
        }
    }
    
    public function loadRoomsAddedToBookingList() {
        $this->includefile("roomsaddedarea");
    }
    
    public function render() {
        echo "<div style='max-width: 1500px; margin: auto;'>";
        $this->includefile("newbookingstep1");
        $this->includefile("newbookingstep2");
        echo "</div>";
        
        echo "<script>";
        echo "app.PmsNewBooking20.setLastPage();";
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
        $booking = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedMultilevelDomainName());
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

    public function printSuggestions($suggestions) {
        $suggestioncount = 0;
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
    

}
?>

<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class BookCampingPage extends Page {
    /**
     * @var core_pmsbookingprocess_BookingConfig
     */
    public $config = null;
    
    public function getId() {
        
    }
    
    public function preRun() {
        $this->config = $this->getApi()->getPmsBookingProcess()->getConfiguration($this->getBookingEngineName());
        
        if (isset($_POST['action'])) {
            if ($_POST['action'] == "setCheckoutDate") {
                $this->setCheckoutDate();
            }
            if ($_POST['action'] == "unsetCheckoutDate") {
                $this->unsetCheckoutDate();
            }
            if ($_POST['action'] == "toggleItem") {
                $this->toggleItem();
            }
            if ($_POST['action'] == "completeBooking") {
                $this->completeBooking();
            }
        }
        
    }

    public function render() {
        if (isset($_SESSION['checkout_page']) && $_SESSION['checkout_page'] == "overview") {
            $this->includeFile("overview");
        } else if (!isset($_SESSION['checkoutdate'])) {
            $this->includeFile("selectcheckoutdate");
        } else {
            $this->includeFile("selectcampingspot");
        }
    }


    public function isInArray($item, $items) {
        foreach ($items as $iItem) {
            if ($iItem->id == $item->id) {
                return true;
            }
        }
        
        return false;
    }
    
    public function toggleItem() {
        if (!isset($_SESSION['items_to_book'])) {
            $_SESSION['items_to_book'] = array();
        }
        
        $key = array_search($_POST['itemid'], $_SESSION['items_to_book']);
        if ($key !== false) {
            unset($_SESSION['items_to_book'][$key]);
        } else {
            $_SESSION['items_to_book'][] = $_POST['itemid'];
        }
        
        $_SESSION['checkout_page'] = "overview";
    }
    
    public function clear() {
        unset($_SESSION['discountcode']);
        unset($_SESSION['checkout_page']);
        unset($_SESSION['items_to_book']);
        unset($_SESSION['checkoutdate']);
    }

    public function getCampingSpot() {
        $itemid = $_SESSION['items_to_book'][0];
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getBookingEngineName(), $itemid);
        return $item->bookingItemName;
    }

    public function completeBooking() {
        $start = new \core_pmsbookingprocess_StartBooking();
        $start->adults = 1;
        $start->children = 0;
        $start->discountCode = $_SESSION['discountcode'];
        $start->rooms = 1;
        $start->start = $this->getJavaStartDate();
        $start->end = $this->getJavaCheckoutDate();
        
        $guestInfo = new \core_pmsbookingprocess_GuestInfo();
        $guestInfo->email = $_POST['email'];
        $guestInfo->name = $_POST['name'];
        $guestInfo->prefix = $_POST['prefix'];
        $guestInfo->phone = $_POST['phone'];
        
        $this->getApi()->getPmsBookingProcess()->startBooking($this->getBookingEngineName(), $start);
        
        $itemid = $_SESSION['items_to_book'][0];
        $change = new \core_pmsbookingprocess_BookingTypeChange();
        $change->start = $this->getJavaStartDate();
        $change->end = $this->getJavaCheckoutDate();
        $change->numberOfRooms = 1;
        $change->guests = 1;
        $change->id = $this->getApi()->getBookingEngine()->getBookingItem($this->getBookingEngineName(), $itemid)->bookingItemTypeId;
        $guestSummary = $this->getApi()->getPmsBookingProcess()->changeNumberOnType($this->getBookingEngineName(), $change);        
        $guestSummary->rooms[0]->guestInfo = $guestInfo;
        $guestSummary->rooms[0]->guests = array();
        $guestSummary->rooms[0]->numberOfGuests = 1;
        $guestSummary->rooms[0]->guests[] = $guestInfo;
        
        $this->getApi()->getPmsBookingProcess()->saveGuestInformation($this->getBookingEngineName(), $guestSummary->rooms);
        $this->getApi()->getPmsBookingProcess()->setBookingItemToCurrentBooking($this->getBookingEngineName(), $guestSummary->rooms[0]->roomId, $itemid);
        
        $input = new \core_pmsbookingprocess_CompleteBookingInput();
        $input->terminalId = $this->getTerminalId();        
        $result = $this->getApi()->getPmsBookingProcess()->completeBookingForTerminal($this->getBookingEngineName(), $input);
        
        $orderId = $result->orderid;
        $amount = $result->amount;
        
        echo "<script>";
        echo "document.location = '/?page=paymentprocesspage&orderid=$orderId&amount=$amount'";
        echo "</script>";
        
        
        
//        $this->getApi()->getPmsBookingProcess()->saveGuestInformation($this->getBookingEngineName(), $arg);
    }

}
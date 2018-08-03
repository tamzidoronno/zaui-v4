<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of BookRoomPage
 *
 * @author ktonder
 */
class BookRoomPage extends Page {
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
            if ($_POST['action'] == "selectRoom") {
                $this->addType();
            }
            if ($_POST['action'] == "addmorerooms") {
                $this->goToAddMoreRooms();
            }
            if ($_POST['action'] == "deleteRoom") {
                $this->deleteRoom();
            }
            if ($_POST['action'] == "changeGuestCount") {
                $this->changeGuestCount();
            }
            if ($_POST['action'] == "gotooverview") {
                $this->goToOverview();
            }
            if ($_POST['action'] == "goToCustomerInfo") {
                $this->goToCustomerInfo();
            }
            if ($_POST['action'] == "paymentCancel") {
                $this->goToOverview(false);
            }
            if ($_POST['action'] == "startPayment") {
                $this->startPayment();
            }
        }
    }

    public function render() {
        if (isset($_SESSION['checkout_page']) && $_SESSION['checkout_page'] == "guestoverview") {
            $this->includeFile("guestoverview");
        } else  if (isset($_SESSION['checkout_page']) && $_SESSION['checkout_page'] == "overview") {
            $this->includeFile("overview");
        } else if (!isset($_SESSION['checkoutdate'])) {
            $this->clear();
            $this->includeFile("selectcheckoutdate");
        } else {
            $this->includeFile("selectroom");
        }
    }

    public function getDefaultImage($images) {
        foreach ($images as $image) {
            if ($image->isDefault) {
                return $image;
            }
        }

        return $images[0];
    }

    public function getRoomCategories() {
        return ['efbde7fe-8c38-43fc-879c-0c495a491129', '8c8d4dd5-c520-482e-8b7e-028695ee72d7', 'ac4b8c48-6f61-4ed3-a3e5-42169f390d1f', 'b991cf07-ddac-4798-9dd9-14336278e8b3', 'e6a3d4d9-0552-44c0-928c-154270dd2bcb', '9146a55f-0963-4853-acd3-8e0ada183807',
            '2ff62286-0994-4431-ac40-5bd907703b77'];
    }

    public function addType() {
        if (!isset($_SESSION['types_to_book'])) {
            $_SESSION['types_to_book'] = array();
        }

        if (isset($_SESSION['types_to_book'][$_POST['typeid']])) {
            $_SESSION['types_to_book'][$_POST['typeid']] += 1;
        } else {
            $_SESSION['types_to_book'][$_POST['typeid']] = 1;
        }
        
        $this->goToOverview();
    }

    /**
     * 
     * @return core_pmsbookingprocess_StartBookingResult
     */
    public function startBooking() {
        $startBooking = new \core_pmsbookingprocess_StartBooking();
        $startBooking->start = $this->getJavaStartDate();
        $startBooking->end = $this->getJavaCheckoutDate();
        $startBooking->rooms = 1;
        $startBooking->discountCode = $_SESSION['discountcode'];
        $startBooking->adults = 1;
        $startBooking->children = 0;
        return $this->getApi()->getPmsBookingProcess()->startBooking($this->getBookingEngineName(), $startBooking);
    }

    /**
     * 
     * @param type $rooms
     * @param type $typeId
     * @return \core_pmsbookingprocess_BookingProcessRooms
     */
    public function getRoom($rooms, $typeId) {
        foreach ($rooms as $room) {
            if ($room->id == $typeId) 
                return $room;
        }
        
        return null;
    }

    public function deleteRoom() {
        $addonsSummary = $this->getApi()->getPmsBookingProcess()->removeRoom($this->getBookingEngineName(), $_POST['roomid']);
        
        foreach ($_SESSION['types_to_book'] as $typeId => $count) {
            $_SESSION['types_to_book'][$typeId] = $this->getCount($addonsSummary, $typeId);
        }
        
        if (!$addonsSummary->rooms) {
            $this->goToAddMoreRooms();
        }
    }

    public function getCount($addonsSummary, $typeId) {
        $count = 0;
        foreach ($addonsSummary->rooms as $room) {
            if ($room->bookingItemTypeId === $typeId) {
                $count++;
            }
        }
        
        return $count;
    }

    public function goToAddMoreRooms() {
        unset($_SESSION['checkout_page']);
    }

    public function changeGuestCount() {
        $this->getApi()->getPmsBookingProcess()->changeGuestCountForRoom($this->getBookingEngineName(), $_POST['roomid'], $_POST['count']);
    }

    public function clear() {
        unset($_SESSION['checkout_page']);
        unset($_SESSION['checkoutdate']);
        unset($_SESSION['types_to_book']);
    }

    public function isRoomAvailableForSelect($availableRooms, $typeId) {
        if (!$availableRooms) {
            return false;
        }
        
        if (isset($_SESSION['types_to_book']) && isset($_SESSION['types_to_book'][$typeId])) {
            $alreadySelectedCount = $_SESSION['types_to_book'][$typeId];
            if ($alreadySelectedCount >= $availableRooms) {
                return false;
            }
        }

        return true;
        
    }

    public function anyRoomsInCart() {
        if (!isset($_SESSION['types_to_book'])) {
            return false;
        }
        
        foreach ($_SESSION['types_to_book'] as $typeId => $count) {
            if ($count) {
                return true;
            }
        }
        
        return false;
    }

    public function goToOverview($setupOvervew=true) {
        if ($setupOvervew) {
            $this->setUpOverview();
        }
        $_SESSION['checkout_page'] = "overview";
    }

    public function setUpOverview() {
        foreach ($_SESSION['types_to_book'] as $typeId => $count) {
            $change = new \core_pmsbookingprocess_BookingTypeChange();
            $change->numberOfRooms = $count;
            $change->guests = 1;
            $change->id = $typeId;
            $change->start = $this->getJavaStartDate();
            $change->end = $this->getJavaCheckoutDate();
            $this->getApi()->getPmsBookingProcess()->changeNumberOnType($this->getBookingEngineName(), $change);
        }
    }

    public function goToCustomerInfo() {
        $_SESSION['checkout_page'] = "guestoverview";
    }

    public function total() {
        $test = $this->getApi()->getPmsBookingProcess()->saveGuestInformation($this->getBookingEngineName(), array());
        $sum = 0;
        
        foreach ($test->rooms as $room ) {
            $sum += $room->totalCost;
        }
        
        return $sum;
    }

    public function startPayment() {
        $addons = $this->getApi()->getPmsBookingProcess()->saveGuestInformation($this->getBookingEngineName(), array());
        
        $guestInfo = new \core_pmsbookingprocess_GuestInfo();
        $guestInfo->email = $_POST['email'];
        $guestInfo->name = $_POST['name'];
        $guestInfo->prefix = $_POST['prefix'];
        $guestInfo->phone = $_POST['phone'];
        
        foreach ($addons->rooms as $room) {
            $room->guests = array();
            $room->numberOfGuests = $room->guestCount;
            
            for ($i=0;$i<$room->guestCount;$i++) {
                $room->guests[] = $guestInfo;
            }
        }
        
        $this->getApi()->getPmsBookingProcess()->saveGuestInformation($this->getBookingEngineName(), $addons->rooms);
        
        $input = new \core_pmsbookingprocess_CompleteBookingInput();
        $input->terminalId = $this->getTerminalId();        
        $result = $this->getApi()->getPmsBookingProcess()->completeBookingForTerminal($this->getBookingEngineName(), $input);
//        
        $orderId = $result->orderid;
        $amount = $result->amount;
        
        $this->clear();
        
        echo "<script>";
        echo "document.location = '/?page=paymentprocesspage&orderid=$orderId&amount=$amount'";
        echo "</script>";
    }

}
<?php

namespace ns_7e7ab1f5_2c73_4da6_8b23_4407fc4bfbd6;

class BookingSelfManagment extends \MarketingApplication implements \Application {

    public function getDescription() {
        
    }

    public function getName() {
        return "BookingSelfManagment";
    }

    public function getSelectedName() {
        return $this->getConfigurationSetting("bookingenginename");
    }

    public function render() {
        if (!$this->getSelectedName() && $this->isEditorMode()) {
            $this->includefile("selectbookingname");
            return;
        } 

        $bookingId = $this->getSecretId();
        $booking = $this->getApi()->getPmsSelfManagement()->getBookingById($this->getSelectedName(), $bookingId);

        if (!$booking || $booking->isDeleted) {
            $this->includefile("notfound");
            return;
        }
        

        if (isset($_SESSION['booking_secret_id_self_manage_roomid'])) {
            $this->includefile("managebookingroom");
            return;
        }

        $this->includefile("managebooking");
        
    }

    public function setName() {
        $this->setConfigurationSetting("bookingenginename", $_POST['data']['name']);
    }

    public function groupRooms($rooms) {
        $roomTypes = array();

        foreach ($rooms as $room) {
            if (!isset($roomTypes[$room->booking->bookingItemTypeId])) {
                $roomTypes[$room->booking->bookingItemTypeId] = array();
            }
            $roomTypes[$room->booking->bookingItemTypeId][] = $room;
        }

        return $roomTypes;
    }

    public function getAddonName($addon) {
        $type = $addon->addonType;
        
        $addonsText = array();
        $addonsText[1] = $this->__w("Breakfast");
        $addonsText[2] = $this->__w("Include parking for {price} per night");
        $addonsText[3] = $this->__w("Include late checkout for {price}");
        $addonsText[4] = $this->__w("Include early checkin for {price}");
        $addonsText[5] = $this->__w("Include extra bed for {price} per night");
        $addonsText[6] = $this->__w("Include the possibility to cancel your stay for {price} per night");

        if (isset($addonsText[$type])) {
            return $addonsText[$type];
        }

        if ($addon->descriptionWeb) {
            return $addon->descriptionWeb;
        }
        
        
        $product = $this->getApi()->getProductManager()->getProduct($addon->productId);
        
        if ($product) {
            return $product->name;
        }
        
        return "";
    }

    public function getAddonCount($addonsForRoom, $addon) {
        $retArray = array();
        $i = 0;
        $price = 0;
        foreach ($addonsForRoom as $addonForRoom) {
            if ($addonForRoom->productId == $addon->productId) {
                $i += $addonForRoom->count;
                $price += $addonForRoom->count * $addonForRoom->price;
            }
        }
        
        $retArray[0] = $i;
        $retArray[1] = $price;
        
        return $retArray;
    }

    public function getSecretId() {
        if (isset($_GET['id'])) {
            $_SESSION['booking_secret_id_self_manage'] = $_GET['id'];
        }
        
        return $_SESSION['booking_secret_id_self_manage'];
    }

    public function setRoom() {
        $_SESSION['booking_secret_id_self_manage_roomid'] = $_POST['data']['roomid'];
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getSelectedRoom() {
        if (!isset($_SESSION['booking_secret_id_self_manage_roomid'])) {
            return null;
        }
        
        $booking = $this->getApi()->getPmsSelfManagement()->getBookingById($this->getSelectedName(), $this->getSecretId());
        foreach ($booking->rooms as $room) {
            if ($room->pmsBookingRoomId == $_SESSION['booking_secret_id_self_manage_roomid']) {
                return $room;
            }
        }
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function getDaysForBooking($room, $excludeFirstDay=false, $excludeLastDay = false) {
        $startTime = new \DateTime($room->booking->startDate);
        $startTime->setTime(0,0,0);
        
        if ($excludeFirstDay) {
            $startTime->modify('+1 day');
        }
        
        
        
        $endTime = new \DateTime($room->booking->endDate);
        $endTime->setTime(23,59,59);
        
        if ($excludeLastDay) {
            $endTime->modify('-1 day');
        }
        
        $period = new \DatePeriod(
            new \DateTime($startTime->format('Y-m-d H:i:s')),
            new \DateInterval('P1D'),
            new \DateTime($endTime->format('Y-m-d H:i:s'))
        );
        
        
        if ($excludeFirstDay) {
            
        }
    
        return $period;
    }

    public function saveAndReturnFromRoom() {
        $this->saveAddons();
        
        unset($_SESSION['booking_secret_id_self_manage_roomid']);
    }
    
    public function toggleAddon() {
        
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBookingAddonItem $checkWithAddon
     * @param type $day
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @return boolean
     */
    public function isAddonOn($checkWithAddon, $day, $room) {
        foreach ($room->addons as $addon) {
            if ($addon->productId == $checkWithAddon->productId) {
                if ($this->isSameDay($addon->date, $day)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public function saveAddons() {
        $addonObjects = $this->convertAddonsObject();
        $this->getApi()->getPmsSelfManagement()->saveAddonSetup($this->getSelectedName(), $this->getSecretId(), $addonObjects);
    }

    public function convertAddonsObject() {
        $objects = array();
        foreach ($_POST['data']['addons'] as $addon) {
            $addonObject = new \core_pmsmanager_PmsSelfManageAddon();
            $addonObject->addonId = $addon['id'];
            $addonObject->days = array();
            $addonObject->roomId = $this->getSelectedRoom()->pmsBookingRoomId;
            $addonObject->productId = $addon['productId'];
            
            foreach($addon['days'] as $day) {
                $dayObject = new \core_pmsmanager_AddonDay();
                $dayObject->active = $day['state'] == "true";
                $dayObject->date = $this->convertToJavaDate(strtotime( str_replace('/', '-', $day['date'])));
                $addonObject->days[] = $dayObject;
            }
            
            $objects[] = $addonObject;
        }
        
        return $objects;
    }

    public function isSameDay($date, $day) {
        $firstDateTimeObj = new \DateTime($date);
        $secondDateTimeObj = $day;
        
        $firstDate = $firstDateTimeObj->format('Y-m-d');
        $secondDate = $secondDateTimeObj->format('Y-m-d');
        
        return $firstDate == $secondDate;
    }

    public function getOrdersFromBooking($booking, $bookingId) {
        $orders = array();
        
        foreach ($booking->orderIds as $orderId) {
            $order = $this->getApi()->getPmsSelfManagement()->getOrderById($this->getSelectedName(), $bookingId, $orderId);
            $orders[$order->incrementOrderId] = $order;
        }
        
        ksort($orders);
        $orders = array_reverse($orders);
        return $orders;
    }

}

?>

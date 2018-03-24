<?php
namespace ns_cbcf3e53_c035_43c2_a1ca_c267b4a8180f;

class PmsGroupBookingHeader extends \MarketingApplication implements \Application {
    private $currentBooking = null;
    public $notChangedError = array();
    
    public function getDescription() {
        
    }

    public function addAddons() {
        $booking = $this->getCurrentBooking();
        $productId = $_POST['data']['id'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $room) {
            foreach($config->addonConfiguration as $addonItem) {
                if($addonItem->productId == $productId) {
                    $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedMultilevelDomainName(), $addonItem->addonType, $room->pmsBookingRoomId, false);
                    break;
                }
            }
        }
        $this->currentBooking = null;
    }
    
    public function removeAddons() {
        $booking = $this->getCurrentBooking();
        $productId = $_POST['data']['id'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        foreach($booking->rooms as $room) {
            foreach($config->addonConfiguration as $addonItem) {
                if($addonItem->productId == $productId) {
                    $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedMultilevelDomainName(), $addonItem->addonType, $room->pmsBookingRoomId, true);
                    break;
                }
            }
        }
        $this->currentBooking = null;
    }
    
    public function getName() {
        return "PmsGroupBookingHeader";
    }
    
    public function addRoomToGroup() {
        $type = $_POST['data']['type'];
        $count = $_POST['data']['count'];
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $bookingId = $this->getCurrentBooking()->id;
        $guestInfoRoom = $_POST['data']['guestInfoOnRoom'];
        
        
        for($i = 0; $i < $count; $i++) {
            $this->getApi()->getPmsManager()->addBookingItemType($this->getSelectedMultilevelDomainName(), $bookingId, $type, $start, $end, $guestInfoRoom);
        }
        $this->currentBooking = null;
    }

    public function render() {
        $this->setBookingEngineId();
        $this->includefile("groupheader");
    }

    public function loadArea() {
        if(isset($_POST['data']['area'])) {
            $_SESSION['currentgroupbookedarea'] = $_POST['data']['area'];
        } else {
            echo "NOT FOUND";
        }
        switch($_POST['data']['area']) {
            case "owner":
                $this->includefile("groupbookerowner");
                break;
            case "guests":
                $this->includefile("guests");
                break;
            case "rooms":
                $this->includefile("rooms");
                break;
            case "payments":
                $this->includefile("payments");
                break;
            case "stay":
                $this->includefile("stay");
                break;
            case "addons":
                $this->includefile("addons");
                break;
            default:
                echo "Path not exisiting yet: " . $_POST['data']['area'];
        }
    }
    
    public function doRoomsBookedAction() {
        $action = $_POST['data']['type'];
        $bookingId = $this->getCurrentBooking()->id;
        $booking = $this->getCurrentBooking();
        if($action == "delete") {
            foreach($_POST['data']['rooms'] as $roomid) {
                $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedMultilevelDomainName(), $bookingId, $roomid);
            }
        } else if($action == "split") {
            $this->getApi()->getPmsManager()->splitBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['rooms']);
        } else if($action == "singlepayments" || $action == "singlepaymentsnosend") {
            $this->getApi()->getPmsInvoiceManager()->removeOrderLinesOnOrdersForBooking($this->getSelectedMultilevelDomainName(), $bookingId, $_POST['data']['rooms']);
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

                $newOrderId = $this->getManager()->createOrder($this->getSelectedMultilevelDomainName(), $bookingId, $filter);
                if($action != "singlepaymentsnosend") {
                    $email = $room->guests[0]->email;
                    $prefix = $room->guests[0]->prefix;
                    $phone = $room->guests[0]->phone;
                    $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedMultilevelDomainName(), $newOrderId, $bookingId, $email, $prefix, $phone);
                }
            }
        }
        $this->currentBooking = null;
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getCurrentBooking() {
        if (!$this->currentBooking) {
            $this->currentBooking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $_SESSION['PmsSearchBooking_bookingId']);
        }
        return $this->currentBooking;
    }

    public function setBookingEngineId() {
        if (isset($_GET['bookingId'])) {
            $this->currentBooking = null;
            $_SESSION['PmsSearchBooking_bookingId'] = $_GET['bookingId'];
        }
    }

    public function updateStayPeriode() {
        $booking = $this->getCurrentBooking();
        foreach($booking->rooms as $room) {
            $start = $_POST['data']['start_'.$room->pmsBookingRoomId] . " " . $_POST['data']['starttime_'.$room->pmsBookingRoomId];
            $end = $_POST['data']['end_'.$room->pmsBookingRoomId] . " " . $_POST['data']['endtime_'.$room->pmsBookingRoomId];
            
            $start = $this->convertToJavaDate(strtotime($start));
            $end = $this->convertToJavaDate(strtotime($end));
            
            $res = $this->getApi()->getPmsManager()->changeDates($this->getSelectedMultilevelDomainName(), $room->pmsBookingRoomId, $booking->id, $start, $end);
            if(!$res) {
                $this->notChangedError[$room->pmsBookingRoomId] = "Unable to change date on this room";
            }
        }
        $this->currentBooking = null;
    }
    
    public function massUpdateGuests() {
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
        foreach($booking->rooms as $room) {
            $room->numberOfGuests = sizeof($_POST['data']['guests'][$room->pmsBookingRoomId]);
            $room->guests = $_POST['data']['guests'][$room->pmsBookingRoomId];
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function saveGuestInformation() {
        $booking = $this->getCurrentBooking();
        foreach($booking->rooms as $room) {
            $room->guests = $_POST['data']['rooms'][$room->pmsBookingRoomId];
            $room->numberOfGuests = sizeof($room->guests);
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function printGuestRow($guest) {
        echo "<div style='margin-bottom: 2px;' class='guestrow'>";
        echo "<span class='shop_button removeguestrow' style='margin-right:5px;border-radius:5px;'><i class='fa fa-trash-o'></i></span>";
        echo "<input type='text' class='gsniceinput1' gsname='name' value='".$guest->name."' style='margin-right: 10px;'>";
        echo "<input type='text' class='gsniceinput1' gsname='email' value='".$guest->email."' style='margin-right: 10px;'>";
        echo "<input type='text' class='gsniceinput1' gsname='prefix' value='".$guest->prefix."' style='width: 30px;margin-right: 10px;'>";
        echo "<input type='text' class='gsniceinput1' gsname='phone' value='".$guest->phone."' style='margin-right: 10px;'>";
        echo "<input type='hidden' class='gsniceinput1' gsname='guestId' value='".$guest->guestId."'>";
        echo "</div>";
    }

    public function includeSelectedArea() {
        $area = "owner";
        if(isset($_SESSION['currentgroupbookedarea'])) {
            $area = $_SESSION['currentgroupbookedarea'];
        }
        
        $_POST['data']['area'] = $area;
        $this->loadArea();
    }

}
?>

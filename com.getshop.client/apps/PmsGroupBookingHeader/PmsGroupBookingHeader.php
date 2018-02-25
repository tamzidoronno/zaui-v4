<?php
namespace ns_cbcf3e53_c035_43c2_a1ca_c267b4a8180f;

class PmsGroupBookingHeader extends \MarketingApplication implements \Application {
    private $currentBooking = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsGroupBookingHeader";
    }

    public function render() {
        $this->setBookingEngineId();
        $this->includefile("groupheader");
    }

    public function loadArea() {
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
            default:
                echo "Path not exisiting yet: " . $_POST['data']['area'];
        }
        $_SESSION['currentgroupbookedarea'] = $_POST['data']['area'];
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
            $this->currentBooking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_SESSION['PmsSearchBooking_groupbookingengineid']);
        }
        return $this->currentBooking;
    }

    public function setBookingEngineId() {
        if (isset($_GET['bookingEngineId'])) {
            $_SESSION['PmsSearchBooking_groupbookingengineid'] = $_GET['bookingEngineId'];
        }
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
        echo "<span class='shop_button'><i class='fa fa-trash-o removeguestrow'></i></span>";
        echo "<input type='text' class='gsniceinput1' gsname='name' value='".$guest->name."'>";
        echo "<input type='text' class='gsniceinput1' gsname='email' value='".$guest->email."'>";
        echo "<input type='text' class='gsniceinput1' gsname='prefix' value='".$guest->prefix."' style='width: 30px;'>";
        echo "<input type='text' class='gsniceinput1' gsname='phone' value='".$guest->phone."'>";
        echo "<input type='hidden' class='gsniceinput1' gsname='guestId' value='".$guest->guestId."'>";
        echo "</div>";
    }

}
?>

<?php
namespace ns_46b52a59_de5d_4878_aef6_13b71af2fc75;

class PmsBookingSummary extends \WebshopApplication implements \Application {
    var $curBooking;
    
    public function getDescription() {
        return "Displays a view with a summary for the current booking that is processed";
    }

    public function getName() {
        return "PmsBookingSummary";
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function updateDateOnRow() {
        $itemId = $_POST['data']['itemid'];
        $typeId = $_POST['data']['typeid'];
        $roomid = $_POST['data']['roomid'];
        $start = strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']);
        if(!isset($_POST['data']['enddate'])) {
            $end = strtotime($_POST['data']['startdate'] . " " . $_POST['data']['endtime']);
        } else {
            $end = strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']);
        }
        $canAdd = $this->getApi()->getPmsManager()->getNumberOfAvailable($this->getSelectedName(), $typeId, $this->convertToJavaDate($start), $this->convertToJavaDate($end));
        if($canAdd && ($start < $end)) {
            $booking = $this->getCurrentBooking();
            foreach($booking->rooms as $room) {
                if($room->pmsBookingRoomId == $roomid) {
                    $room->date->start = $this->convertToJavaDate($start);
                    $room->date->end = $this->convertToJavaDate($end);
                }
            }
            $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
            echo "<i class='fa fa-check'></i>";
        } else {
            echo "<i class='fa fa-warning' color='red' title='".$this->__w("Sorry, but the selected time periode is not available, or incorrect")."'></i>";
        }
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        $this->includefile("summary");
    }

    public function toggleAddon() {
        $this->getApi()->getPmsManager()->toggleAddon($this->getSelectedName(), $_POST['data']['item']);
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    public function addAddon() {
        $itemType = $_POST['data']['itemtypeid'];
        $this->getApi()->getPmsManager()->addAddonToCurrentBooking($this->getSelectedName(), $itemType);
    }
    
    public function removeAddon() {
        $itemType = $_POST['data']['itemtypeid'];
        $this->getApi()->getPmsManager()->removeFromCurrentBooking($this->getSelectedName(), $itemType);
    }
    
    public function addRepeatingDates() {
        $data = $this->createRepeatingDateObject();
        $this->getApi()->getPmsManager()->addRepeatingData($this->getSelectedName(), $data);
    }
    
    public function getRepeatingSummary() {
        $booking = $this->getCurrentBooking();
        if(!$booking->lastRepeatingData) {
            return "";
        }
        
        $text = "";
        if($booking->lastRepeatingData->repeattype == "repeat") {
            if($booking->lastRepeatingData->data->repeatPeride == "0") {
                $text = $this->__w("Repeats daily");
            }
            if($booking->lastRepeatingData->data->repeatPeride == "1") {
                $text = $this->__w("Repeats every {periode} week") . " (";
                $text = str_replace("{periode}", $booking->lastRepeatingData->data->repeatEachTime, $text);
                if($booking->lastRepeatingData->data->repeatMonday) {
                    $text .= strtolower($this->__w("Mon")) . ", ";
                }
                if($booking->lastRepeatingData->data->repeatTuesday) {
                    $text .= strtolower($this->__w("Tue")) . ", ";
                }
                if($booking->lastRepeatingData->data->repeatWednesday) {
                    $text .= strtolower($this->__w("Wed")) . ", ";
                }
                if($booking->lastRepeatingData->data->repeatThursday) {
                    $text .= strtolower($this->__w("Thu")) . ", ";
                }
                if($booking->lastRepeatingData->data->repeatFriday) {
                    $text .= strtolower($this->__w("Fri")) . ", ";
                }
                if($booking->lastRepeatingData->data->repeatSaturday) {
                    $text .= strtolower($this->__w("Sat")) . ", ";
                }
                if($booking->lastRepeatingData->data->repeatSunday) {
                    $text .= strtolower($this->__w("Sun")) . ", ";
                }
                $text = substr($text, 0, -2) . ")";
            }
            if($booking->lastRepeatingData->data->repeatPeride == "2") {
                if($booking->lastRepeatingData->data->repeatAtDayOfWeek) {
                    $text = $this->__w("Repeats montly same day in week");
                } else {
                    $text = $this->__w("Repeats montly same date in month");
                }
            }
            
            $text .= " " . $this->__w("until") . " " . date("d.m.Y", strtotime($booking->lastRepeatingData->data->endingAt));
        }
        return $text;
    }
    
    /**
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getFirstBookedRoom() {
        $booking = $this->getCurrentBooking();
        if(isset($booking->rooms[0])) {
            return $booking->rooms[0];
        } else {
            return new \core_pmsmanager_PmsBookingRooms();
        }
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBooking $booking
     */
    public function hasRoomsThatCanNoBeAdded($booking) {
        foreach($booking->rooms as $room) {
            if(!$room->canBeAdded) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return \core_pmsmanager_PmsBooking
     */
    public function getCurrentBooking() {
        if(!$this->curBooking) {
            $this->curBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        }
        
        return $this->curBooking;
                
    }

    public function createRepeatingDateObject() {
        $data = new \core_pmsmanager_PmsRepeatingData();
        $data->repeattype = $_POST['data']['repeattype'];
        if(isset($_POST['data']['itemid'])) {
            $data->bookingItemId = $_POST['data']['itemid'];
        }
        if(isset($_POST['data']['typeid'])) {
            $data->bookingTypeId = $_POST['data']['typeid'];
        }
        
        $data->data = new \core_pmsmanager_TimeRepeaterData();
        $data->data->repeatMonday = $_POST['data']['repeatMonday'] == "true";
        $data->data->repeatTuesday = $_POST['data']['repeatTuesday'] == "true";
        $data->data->repeatWednesday = $_POST['data']['repeatWednesday'] == "true";
        $data->data->repeatThursday = $_POST['data']['repeatThursday'] == "true";
        $data->data->repeatFriday = $_POST['data']['repeatFriday'] == "true";
        $data->data->repeatSaturday = $_POST['data']['repeatSaturday'] == "true";
        $data->data->repeatSunday = $_POST['data']['repeatSunday'] == "true";
        $data->data->endingAt = $this->convertToJavaDate(strtotime($_POST['data']['endingAt']));
        $data->data->repeatEachTime = $_POST['data']['repeateachtime'];
        if(isset($_POST['data']['repeatmonthtype'])) {
            $data->data->repeatAtDayOfWeek = $_POST['data']['repeatmonthtype'] == "dayofweek";
        }
        $data->data->repeatPeride = $_POST['data']['repeat_periode'];
        
        $data->data->firstEvent = new \core_pmsmanager_TimeRepeaterDateRange();
        $data->data->firstEvent->start = $this->convertToJavaDate(strtotime($_POST['data']['eventStartsAt'] . " " . $_POST['data']['starttime']));
        if(isset($_POST['data']['eventEndsAt'])) {
            $data->data->firstEvent->end = $this->convertToJavaDate(strtotime($_POST['data']['eventEndsAt'] . " " . $_POST['data']['endtime']));
        } else {
            $data->data->firstEvent->end = $this->convertToJavaDate(strtotime($_POST['data']['eventStartsAt'] . " " . $_POST['data']['endtime']));
        }
        
        if($data->data->repeatPeride == "0") {
            $data->data->repeatEachTime = 1;
        }
        return $data;
    }

    public function includeCouponSystem() {
        $coupon = $this->getApi()->getStoreApplicationPool()->getApplication("90cd1330-2815-11e3-8224-0800200c9a66");
        if($coupon) {
            if($this->getCurrentBooking()->couponCode) {
                echo "<i class='fa fa-trash-o'  gstype='clicksubmit' method='removeCouponCode' gsname='id' gsvalue='somevalue'></i> ";
                echo "Coupon code added: " . $this->getCurrentBooking()->couponCode;
            } else {
                if(isset($_POST['data']['code'])) {
                    echo "Invalid code";
                }
                ?>
                <div gstype="form" method="addCouponCode">
                    <input type='text' gsname='code' gstype='submitenter'><span class='addcouponbutton' gstype='submit'>Add coupon</span>
                </div>
                <?php
            }
        }
    }
    
    public function addCouponCode() {
        $code = $_POST['data']['code'];
        $curBooking = $this->getCurrentBooking();
        $curBooking->couponCode = $code;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $curBooking);
        $this->curBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
    }
    
    public function removeCouponCode() {
        $curBooking = $this->getCurrentBooking();
        $curBooking->couponCode = "";
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $curBooking);
        $this->curBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
    }
}
?>
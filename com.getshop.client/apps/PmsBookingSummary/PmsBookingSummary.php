<?php
namespace ns_46b52a59_de5d_4878_aef6_13b71af2fc75;

class PmsBookingSummary extends \WebshopApplication implements \Application {
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
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        $this->includefile("summary");
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
        $data->data->firstEvent->end = $this->convertToJavaDate(strtotime($_POST['data']['eventEndsAt'] . " " . $_POST['data']['endtime']));
        
        $this->getApi()->getPmsManager()->addRepeatingData($this->getSelectedName(), $data);
    }

    
    /**
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getFirstBookedRoom() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        return $booking->rooms[0];
    }

}
?>

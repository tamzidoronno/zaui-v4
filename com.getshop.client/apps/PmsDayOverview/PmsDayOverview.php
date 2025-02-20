<?php
namespace ns_139d9335_2353_48fe_aae5_abc4268fc14e;

class PmsDayOverview extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsDayOverview";
    }

    public function render() {
        $this->includefile("checkinoutoverview");
    }

    public function getSelectedName() {
        if($this->getApi()->getStoreManager()->getMyStore()->id == "7b21932d-26ad-40a5-b3b6-c182f5ee4b2f") {
            return "hybelhotell";
        }
        return "default";
    }
    
    public function changeDate() {
        $date = $_POST['data']['date'];
        $_SESSION['pmsdayoverview_date'] = strtotime($date);
    }
    
    public function getSelectedDate() {
        if(isset($_SESSION['pmsdayoverview_date'])) {
            return $_SESSION['pmsdayoverview_date'];
        }
        return time();
    }

    public function getSelectedFilter($checkin, $daysAhead = 0) {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->state = 0;
        $start = strtotime(date("d.m.Y 00:00", $this->getSelectedDate()));
        $start += 86400 *$daysAhead;
        $filter->startDate = $this->formatTimeToJavaDate($start);
        $filter->endDate = $this->formatTimeToJavaDate($start+82400);
        if($checkin) {
            $filter->filterType = "checkin";
        } else {
            $filter->filterType = "checkout";
        }
        return $filter;
    }
    
    public function keyIsReturned() {
        $_SESSION['returnedkey_'.$_POST['data']['roomid'] . "_" . date("d.m.Y")] = true;
        $this->getApi()->getPmsManager()->returnedKey($this->getSelectedName(), $_POST['data']['roomid']);
    }
    
    public function updatecode() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $config->extraField = $_POST['data']['code'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }

    public function markedAsReturned($roomid, $room) {
        if($room != null) {
            return $room->keyIsReturned;
        }
        return "notinuse";
    }

}
?>

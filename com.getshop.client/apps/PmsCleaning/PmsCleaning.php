<?php
namespace ns_ae42328b_3726_4875_8546_212cd8cc3cde;

class PmsCleaning extends \WebshopApplication implements \Application {
    var $items;
    var $additionalInfo;
    var $counter = 0;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsCleaning";
    }


    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        $this->includefile("cleaningpanel");
    }
    
    function print_guests_table($time, $arriving) {
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        if($arriving) {
            $filter->filterType = "checkin";
        } else {
            $filter->filterType = "checkout";
        }
        $filter->startDate = $this->convertToJavaDate($time);
        $filter->endDate = $this->convertToJavaDate($time+86400);

        $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
        if(!$bookings) {
            $bookings = array();
        }
        echo "<table width='100%' cellspacing='0' cellpadding='0'>";
        $this->printRowHeader();
        
        foreach($bookings as $booking) {
            foreach($booking->rooms as $room) {
                $this->printRoomRow($room);
            }
        }
        echo "</table>";
        echo $this->counter . " rows found";
    }

    public function markCleaned() {
        $id = $_POST['data']['id'];
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedName(), $id);
        
    }
    
    public function getCleaningDate() {
        if(isset($_SESSION['cleaningdateselected'])) {
            return date("d.m.Y", $_SESSION['cleaningdateselected']);
        }
        return date("d.m.Y", time());
    }

    public function changeDate() {
        $_SESSION['cleaningdateselected'] = strtotime($_POST['data']['date']);
    }

    public function getItems() {
        if(isset($this->items)) {
            return $this->items;
        }
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        $items2 = array();
        foreach($items as $item) {
            $items2[$item->id] = $item;
        }
        $this->items = $items2;
        return $items2;
    }

    public function printCleaningTable($time) {
        $items = $this->getItems();
        echo "<table width='100%' cellspacing='0' cellpadding='0'>";
        echo "<tr>";
        for($i = 0; $i < 7; $i++) {
            $res = $time+(86400*$i);
            echo "<th>".date("d.m.Y", $res). "</th>";
        }
        echo "</tr>";

        $total = 0;
        for($i = 0; $i < 7; $i++) {
            echo "<td valign='top'>";
            $filter = new \core_pmsmanager_PmsBookingFilter();
            $filter->filterType = "checkin";
            $filter->startDate = $this->convertToJavaDate($time+(86400*$i));
            $filter->endDate = $this->convertToJavaDate($time+(86400*($i+1)));
            $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
            $intervalResult = $this->getApi()->getPmsManager()->getRoomsNeedingIntervalCleaning($this->getSelectedName(), $this->convertToJavaDate($time+(86400*$i)));
            if(!$intervalResult) {
                $intervalResult = array();
            }
            if($bookings) {
                foreach($bookings as $booking) {
                    foreach($booking->rooms as $room) {
                        echo $room->guests[0]->name . "<br>";
                        $total++;
                    }
                }
            }
            
            foreach($intervalResult as $room) {
                $guestName = $room->guests[0]->name;
                echo $room->numberOfGuests . " - " . $items[$room->bookingItemId]->bookingItemName . " - " . $guestName. "<br>";
            }
            
            
            echo "</td>";
        }
        
        echo "</table>";
        
        echo "<h1>Total cleanings for the next 7 days : $total</h1>";
    }

    public function printRoomOverview() {
        $additional = $this->getAdditionalInfo();
        $items = $this->getItems();
        echo "<span class='notclean' style='color:#fff !important; width:130px; display:inline-block; padding: 5px;'>Room is not clean</span><br>";
        echo "<span class='clean' style='color:#fff !important; width:130px; display:inline-block; padding: 5px;'>Room is clean</span><br>";
        echo "<span class='inUse' style='color:#fff !important; width:130px; display:inline-block; padding: 5px;'>Room is in use</span><br><br>";
        foreach($additional as $add) {
            $isClean = "notclean";
            if($add->isClean) {
                $isClean = "clean";
            }
            if($add->inUse) {
                $isClean = "inUse";
            }
            echo "<span class='roombox cleaningbox $isClean'>" . $items[$add->itemId]->bookingItemName . "</span>";
        }
    }

    public function getAdditionalInfo() {
        if(isset($this->additionalInfo)) {
            return $this->additionalInfo;
        }
        $add = $this->getApi()->getPmsManager()->getAllAdditionalInformationOnRooms($this->getSelectedName());
        $add2 = array();
        foreach($add as $ad) {
            $add2[$ad->itemId] = $ad;
        }
        $this->additionalInfo = $add2;
        return $add2;
    }

    public function print_interval_cleaning($time) {
        $items = $this->getItems();
        $result = $this->getApi()->getPmsManager()->getRoomsNeedingIntervalCleaning($this->getSelectedName(), $this->convertToJavaDate($time));
        if(!$result) {
            $result = array();
        }
        echo "<table width='100%' cellspacing='0' cellpadding='0'>";
        $this->printRowHeader();
        foreach($result as $res) {
            $this->printRoomRow($res);
        }
        echo "</table>";
        echo $this->counter . " rows found";
    }

    public function printRoomRow($room) {
        $this->counter++;
        $items = $this->getItems();
        $additional = $this->getAdditionalInfo();
        
        echo "<tr>";
        echo "<td>" . $room->numberOfGuests . "</td>";
        echo "<td>";
        if($room->bookingItemId) {
            echo $items[$room->bookingItemId]->bookingItemName;
        } else {
            echo "Not assigned yet";
        }
        echo "</td>";
        echo "<td>";
        if($room->bookingItemId) {
            if($this->getApi()->getPmsManager()->isClean($this->getSelectedName(), $room->bookingItemId)) {
                echo "<span class='roomIsReady'>Room is marked as ready</span>";
            } else {
                echo "<span gstype=\"clicksubmit\" class='roomNotReady' method=\"markCleaned\" gsname=\"id\" gsvalue=\"".$room->bookingItemId."\">Mark room as ready</span>";
            }
        }
        echo "</td>";
        echo "<td>";
        foreach($room->guests as $guest) {
            echo $guest->name . " - " . $guest->phone . " - " . $guest->email . "<br>";
        }
        echo "</td>";
        echo "<td>";
        if($room->bookingItemId) {
            if($additional[$room->bookingItemId]->lastCleaned) {
                echo date("d.m.Y H:i", strtotime($additional[$room->bookingItemId]->lastCleaned));
            }
        }
        echo "</td>";
        echo "<td align='right'>From " .date("d.m.Y H:i", strtotime($room->date->start)). "<br>To ". date("d.m.Y H:i", strtotime($room->date->end)) . "</td>";
        echo "</tr>";
    }

    public function printRowHeader() {
        $this->counter = 0;
        echo "<tr>";
        echo "<th align='left'></th>";
        echo "<th align='left'>Room</th>";
        echo "<th align='left'>Action</th>";
        echo "<th align='left'>Guest information</th>";
        echo "<th align='left'>Last cleaned</th>";
        echo "<th align='right'>Duration</th>";
        echo "</tr>";
    }

}
?>

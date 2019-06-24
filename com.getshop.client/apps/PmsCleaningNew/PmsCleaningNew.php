<?php
namespace ns_c9a0671d_2eef_4a8e_8e69_523bcfc263e1;

class PmsCleaningNew extends \WebshopApplication implements \Application {

    var $items;
    var $additionalInfo;
    var $counter = 0;
    var $config;
    
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsCleaningNew";
    }


    public function printCleaningTableStatistics($time) {
        $startDate = $this->convertToJavaDate($time);
        $endDate = $this->convertToJavaDate($time+(86400*30));
        $overview = $this->getApi()->getPmsManager()->getSimpleCleaningOverview($this->getSelectedName(), $startDate, $endDate);
        
        
        ?>
    <table cellspacing="1" cellpadding="1">
    <tr>
        <th>Date</th>
        <th>Stayovers</th>
        <th>Checkout</th>
        <th>Interval</th>
        <th>Total</th>
    </tr>
    <?php
    foreach($overview as $view) {
        echo "<tr>";
        echo "<td width='100%'>".date("d.m.Y", strtotime($view->date))."</td>";
        echo "<td>".$view->stayOvers."</td>";
        echo "<td>".$view->intervalCleaningCount."</td>";
        echo "<td>".$view->intervalCleaningCount."</td>";
        echo "<td>".($view->intervalCleaningCount + $view->checkoutCleaningCount)."</td>";
        echo "</tr>";
    }
    ?>
    </table>
        <?php
    }

    public function getSelectedName() {
        return $this->getSelectedMultilevelDomainName();
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
        echo "<div style='max-width:1500px;margin:auto;'>";
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        if($config->cleaningInterval == 0) {
            $this->includefile("notsetupyet");
        } else {
            $this->includefile("cleaningpanel");
        }
        echo "</div>";
    }
    
    function print_guests_table($time, $arriving) {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->filterType = "checkin";
        $filter->startDate = $this->convertToJavaDate($time);
        $filter->endDate = $this->convertToJavaDate($time+85499);
        $filter->removeHiddenFromCleaning = true;
        $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
        if(!$bookings) {
            $bookings = array();
        }

        echo "<table width='100%' cellspacing='0' cellpadding='0'>";
        $this->printRowHeader();

        $rooms = array();
        foreach($bookings as $booking) {
            foreach($booking->rooms as $room) {
                if(!$this->isSameDay(strtotime($room->date->start), $time)) {
                    continue;
                }
                if($room->deleted) {
                    continue;
                }
                
                $add = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedMultilevelDomainName(), $room->bookingItemId);
                if($add->hideFromCleaningProgram) {
                    continue;
                }
                
                $items = $this->getItems();
                $room->booking = $booking;
                @$rooms[$items[$room->bookingItemId]->bookingItemName] = $room;
                
            }
        }
        
        ksort($rooms);
        foreach($rooms as $room) {
            $this->printRoomRow($room);
        }
        
        echo "</table>";
        echo "<span style='color:#aaa;padding-top:10px; padding-top: 10px;'>" . $this->counter . " rows found</span>";
    }

    public function markCleaned() {
        $id = $_POST['data']['id'];
        $this->getApi()->getPmsManager()->markRoomAsCleaned($this->getSelectedName(), $id);
        
    }
    
    public function getCleaningDate() {
        if(isset($_SESSION['cleaningdateselected'])) {
            return date("d.m.Y 01:00", $_SESSION['cleaningdateselected']);
        }
        return date("d.m.Y 01:00", time());
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
        for($i = 0; $i < 8; $i++) {
            $res = $time+(86400*$i);
            echo "<th>".date("d.m.Y", $res). "</th>";
        }
        echo "</tr>";

        $total = 0;
        for($i = 0; $i < 8; $i++) {
            echo "<td valign='top'>";
            $filter = new \core_pmsmanager_PmsBookingFilter();
            $filter->filterType = "checkoutcleaning";
            $filter->startDate = $this->convertToJavaDate($time+(86400*$i));
            $filter->endDate = $this->convertToJavaDate($time+(86400*($i+1)));
            $checkoutCleaningRooms = $this->getApi()->getPmsManager()->getRoomsNeedingCheckoutCleaning($this->getSelectedName(), $this->convertToJavaDate($time+(86400*$i)));
            $intervalResult = $this->getApi()->getPmsManager()->getRoomsNeedingIntervalCleaning($this->getSelectedName(), $this->convertToJavaDate($time+(86400*$i)));
            $all = $this->mergeIntervalAndCheckout($checkoutCleaningRooms, $intervalResult);
            foreach($all as $room) {
                $guestName = $room->guests[0]->name;
                $icon = "<i class='fa fa-refresh'></i>";
                if($room->isCheckout) {
                    $icon = "<i class='fa fa-sign-out'></i> ";
                }
                $total++;
                echo $icon . $room->numberOfGuests . " - " . @$items[$room->bookingItemId]->bookingItemName . " - <span class='guestname' title='$guestName'>" . $guestName. "</span><br>";
            }
            
            
            echo "</td>";
        }
        
        echo "</table>";
        
        echo "<h1>Total cleanings for the next 7 days : $total</h1>";
    }

    
    public function printRoomOverview() {
        $res = array();
        $res['isclean'] = 0;
        $res['inuse'] = 0;
        $res['dirty'] = 0;
        
        $additional = $this->getAdditionalInfo();
        $roomsNeedCleaning = $this->getApi()->getPmsManager()->getAllRoomsNeedCleaningToday($this->getSelectedMultilevelDomainName());
        foreach($roomsNeedCleaning as $tmpr) {
            $roomsNeedCleaning[$tmpr->roomId] = $tmpr;
        }
        $items = $this->getItems();
        echo "<span class='roomisclosed' style='color:#fff !important; width:230px; display:inline-block; padding: 5px;'>Room is closed</span><br>";
        echo "<span class='notclean' style='color:#fff !important; width:230px; display:inline-block; padding: 5px;'>Room is not clean</span><br>";
        echo "<span class='notcleancheckedout' style='color:#fff !important; width:230px; display:inline-block; padding: 5px;'>Room is not clean checked out</span><br>";
        echo "<span class='clean' style='color:#fff !important; width:230px; display:inline-block; padding: 5px;'>Room is clean</span><br>";
        echo "<span class='inUse' style='color:#fff !important; width:230px; display:inline-block; padding: 5px;'>Room is in use</span><br><br>";
        foreach($additional as $add) {
            $isClean = "notclean roomNotReady";
            $state = $roomsNeedCleaning[$add->itemId]->cleaningState;
            if($state == 2) {
                $isClean = "inUse roomNotReady";
                $res['inuse']++;
            } else if($state == 5) {
                $isClean = "notcleancheckedout";
                $res['dirty']++;
            } else if($state == 6) {
                $isClean = "roomisclosed";
                $res['dirty']++;
            } else if($state == 1) {
                $isClean = "clean";
                $res['isclean']++;
            } else {
                $res['dirty']++;
            }
            echo "<span class='roombox cleaningbox $isClean' itemid='".$add->itemId."'>" . $items[$add->itemId]->bookingItemName . "</span>";
        }
        return $res;
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
        $interval = $this->getApi()->getPmsManager()->getRoomsNeedingIntervalCleaning($this->getSelectedName(), $this->convertToJavaDate($time));
        $checkout = $this->getApi()->getPmsManager()->getRoomsNeedingCheckoutCleaning($this->getSelectedName(), $this->convertToJavaDate($time));
        $all = $this->mergeIntervalAndCheckout($checkout, $interval);
        
        echo "<table width='100%' cellspacing='0' cellpadding='0'>";
        $this->printRowHeader();
        foreach($all as $res) {
            $this->printRoomRow($res);
        }
        echo "</table>";
        echo "<span style='color:#aaa;padding-top:10px; padding-top: 10px;'>" . $this->counter . " rows found</span>";
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function printRoomRow($room) {
        if($room->deleted) {
            return;
        }
        $config = $this->getPmsConfig();
        $this->counter++;
        $items = $this->getItems();
        $additional = $this->getAdditionalInfo();
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $room->pmsBookingRoomId);
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        $hasExtraBed = false;
        $hasChildBed = false;
        foreach($room->addons as $addon) {
            if($addon->addonType == 5) {
                $hasExtraBed = true;
            }
            if($addon->addonType == 7) {
                $hasChildBed = true;
            }
        }
        echo "<tr roomid='".$room->pmsBookingRoomId."' itemid='".$room->bookingItemId."'>";
        echo "<td>";
        echo $room->numberOfGuests . "<br>";
        $startHour = date("H", strtotime($room->date->start));
        $endHour = date("H", strtotime($room->date->end));
        $defaultStartHour = $this->getHour(true);
        $defaultEndHour = $this->getHour(false);
        if($startHour < $defaultStartHour) { echo '<i class="fa fa-clock-o" style="color:red"></i>'; }
        if($endHour > $defaultEndHour) { echo '<i class="fa fa-clock-o" style="color:green"></i>'; }
        if($hasExtraBed) { echo '<i class="fa fa-user-plus"></i>'; }
        if($hasChildBed) { echo '<i class="fa fa-bed"></i>'; }
        echo "</td>";
        echo "<td>";
        
        $isInterval = false;
        if(!$this->isCheckout($room, true) && !$this->isCheckout($room,false)) {
            echo "<i class='fa fa-refresh'></i> ";
            $isInterval = true;
        }

        if($room->bookingItemId) {
            echo $items[$room->bookingItemId]->bookingItemName;
        } else {
            echo "Not assigned yet";
        }
        echo "</td>";
        echo "<td>";
        if($room->bookingItemId) {
            $isClean = $this->getApi()->getPmsManager()->isClean($this->getSelectedName(), $room->bookingItemId);
            $isUsedToday = $this->getApi()->getPmsManager()->isUsedToday($this->getSelectedName(), $room->bookingItemId);
            if($isClean || $isUsedToday) {
                echo "<span class='roomIsReady'>Room is marked as ready</span>";
            } else {
                echo "<span class='roomNotReady' method=\"markCleaned\" itemid=\"".$room->bookingItemId."\">Mark room as ready</span>";
            }
        }
        echo "</td>";
        echo "<td>";
        if(time() > strtotime($room->date->end)) { echo '<i class="fa fa-sign-out" title="Guest has checked out"></i> '; }
        foreach($room->guests as $guest) {
            echo $guest->name . " - " . $guest->phone . " - " . $guest->email . "<br>";
            break;
        }
        echo "<span style='color:#bbb'>" . $user->fullName . "</span>";
        
        if($room->cleaningComment) {
            echo "<div style='color:red; font-weight: bold;'><i class='fa fa-warning'></i> " . $room->cleaningComment . "</div>";
        }

        
        echo "</td>";
        echo "<td>";
        if($room->bookingItemId) {
            $lastCleaned = $additional[$room->bookingItemId]->lastCleaned;
            if($lastCleaned) {
                echo date("d.m.Y H:i", strtotime($lastCleaned));
            }
            $time = "";
            $lastCleanedUser = "";
            foreach($additional[$room->bookingItemId]->cleanedByUser as $timer => $userId) {
                if(!$time || $timer>$time) {
                    $time = $timer;
                    $lastCleanedUser = $userId;
                }
            }
            
            if($lastCleanedUser) {
                echo "<div style='color:#aaa'>by " . $this->getApi()->getUserManager()->getUserById($lastCleanedUser)->fullName . "</div>";
            }
//            $userId = $additional[$room->bookingItemId]->cleanedByUser->{$time};
//            if($userId) { echo $userId; }
        }
        if($isInterval) {
            echo "<div style='color:red; font-weight:bold; cursor:pointer;' class='posponeuntiltomorrow'>Pospone</div>";
        }
        echo "</td>";
        echo "<td>";
        if($room->bookingItemId) {
            if($additional[$room->bookingItemId]->lastCleaned) {
                echo date("d.m.Y H:i", strtotime($additional[$room->bookingItemId]->lastUsed));
            }
        }
        echo "</td>";
        echo "<td align='right'>From " .date("d.m.Y H:i", strtotime($room->date->start)). "<br>To ". date("d.m.Y H:i", strtotime($room->date->end)) . "</td>";
        echo "</tr>";
    }

    public function pospone() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedName(), $_POST['data']['roomid']);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['roomid']) {
                $cdate = strtotime($this->getCleaningDate())+86400;
                $room->date->cleaningDate = $this->convertToJavaDate($cdate);
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedName(), $booking);
    }
    
    public function printRowHeader() {
        $this->counter = 0;
        echo "<tr>";
        echo "<th align='left'></th>";
        echo "<th align='left' width='50'>Room</th>";
        echo "<th align='left' width='150'>Action</th>";
        echo "<th align='left'>Guest information</th>";
        echo "<th align='left' width='130'>Last cleaned</th>";
        echo "<th align='left' width='130'>Last used</th>";
        echo "<th align='right' width='150'>Duration</th>";
        echo "</tr>";
    }

    public function isSameDay($time1, $time2) {
        return date("d.m.Y", $time1) == date("d.m.Y", $time2);
    }

    public function mergeIntervalAndCheckout($checkoutCleaningRooms, $intervalResult) {
        $items = $this->getItems();
        if(!$intervalResult) {
            $intervalResult = array();
        }
        if(!$checkoutCleaningRooms) {
            $checkoutCleaningRooms = array();
        }
        foreach($checkoutCleaningRooms as $check) { $check->isCheckout = true; }
        foreach($intervalResult as $check) { $check->isCheckout = false; }

        $all = array_merge($checkoutCleaningRooms, $intervalResult);
        
        $newArray = array();
        $i = 0;
        $unsorted = array();
        foreach($all as $a) {
            if($a->bookingItemId) {
                @$newArray[$items[$a->bookingItemId]->bookingItemName] = $a;
            } else {
                $unsorted[] = $a;
            }
        }
        
        $newArray = array_merge($newArray, $unsorted);
        
        ksort($newArray);
        
        return $newArray;
    }

    public function isCheckout($room, $checkout) {
        if($checkout) {
            return date("dmy", time()) == date("dmy", strtotime($room->date->end));
        }
        return date("dmy", time()) == date("dmy", strtotime($room->date->start));
    }

    /**
     * @return \core_pmsmanager_PmsConfiguration
     */
    public function getPmsConfig() {
        if(!$this->config) {
            $this->config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        }
        return $this->config;
    }

    public function getHour($startHour) {
        $time = $this->getPmsConfig()->defaultEnd;
        if($startHour) {
            $time = $this->getPmsConfig()->defaultStart;
        }
        
        $time = explode(":", $time);
        return $time[0];
    }

    public function printUncleanedRooms() {
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        $items = $this->indexList($items);
        $rooms = $this->getApi()->getPmsManager()->getAllAdditionalInformationOnRooms($this->getSelectedName());
        $checkoutCleaningRooms = $this->getApi()->getPmsManager()->getRoomsNeedingCheckoutCleaning($this->getSelectedName(), $this->convertToJavaDate(time()));

        echo "<table cellspacing='0' cellpadding='0'>";
        echo "<tr>";
        echo "<th>Room</th>";
        echo "<th>Action</th>";
        echo "</tr>";
        
        foreach($rooms as $room) {
            $found = false;
            foreach($checkoutCleaningRooms as $checkout) {
                if($room->itemId == $checkout->bookingItemId) {
                    $found = true;
                }
            }
            if($found) {
                continue;
            }
            if(!$room->isClean && (!$room->inUse || $room->inUseByCleaning || $room->closed)) {
                echo "<tr>";
                echo "<td>".$items[$room->itemId]->bookingItemName."</td>";
                echo "<td><span class='roomNotReady' method=\"markCleaned\" itemid=\"".$room->itemId."\">Mark room as ready</span></td>";
                echo "</tr>";
            }
        }
        echo "</table>";
    }
}
    
?>

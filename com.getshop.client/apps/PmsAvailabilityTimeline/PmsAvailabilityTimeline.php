<?php
namespace ns_176ea989_c7bb_4cef_a4bd_0c8421567e0b;

class PmsAvailabilityTimeline extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAvailabilityTimeline";
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function loadBookingList() {
        $type = $_POST['data']['type'];
        $item = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $type);
        $day = strtotime(date("d.m.Y 14:00", strtotime($_POST['data']['day'])));
        $start = $this->convertToJavaDate($day);
        $end = $this->convertToJavaDate($day+60000);
        $timeline = $this->getApi()->getBookingEngine()->getTimelines($this->getSelectedName(), $type, $start, $end);
        echo "<h1>" . $start . " - " . $end . "</h1><br>";
        echo "<h2>" . $item->name . "</h2>";
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->filterType = "active";
        $filter->typeFilter = array();
        $filter->typeFilter[] = $type;
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        
        $filter->startDate = $start;
        $filter->endDate = $end;
        $rooms = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedName(), $filter);
        echo "<table width='100%'>";
        echo "<tr>";
        echo "<th>Room</th>";
        echo "<th>Owner</th>";
        echo "<th>Check in</th>";
        echo "<th>Check out</th>";
        echo "<th>Guest</th>";
        echo "<th></th>";
        echo "</tr>";
        foreach($rooms as $room) {
            $started = "";
            if($room->start/1000 < time()) {
                $started = "style='color:red; font-weight:bold;'";
            }
            echo "<tr $started>";
            echo "<td>" . $room->room . "</td>";
            echo "<td>".$room->owner."<br>".$room->guest[0]->name."</td>";
            echo "<td>".date("d.m.Y H:i", $room->start/1000)."</td>";
            echo "<td>".date("d.m.Y H:i", $room->end/1000)."</td>";
            echo "<td>";
            if(!$started) {
                echo '<div gstype="form" method="changeItemForBooking">';
                echo "<input type='hidden' gsname='roomid' value='". $room->pmsRoomId . "'>";
                echo "<input type='hidden' gsname='day' value='". $_POST['data']['day'] . "'>";
                echo "<input type='hidden' gsname='type' value='". $_POST['data']['type'] . "'>";
                echo "<select gsname='newtype'>";
                foreach($types as $type) {
                    $items = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedName(), 
                            $type->id, 
                            $this->convertToJavaDate($room->start/1000), 
                            $this->convertToJavaDate($room->end/1000));
                    
                    if($type->id == $type) {
                        continue;
                    }
                    echo "<option value='".$type->id."'>" . $type->name . " (" . $items .")". "</option>";
                }
                echo "</select>";
                echo "<input type='button' value='change' gstype='submitToInfoBox'>";
                echo "</div>";
            }
            echo "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function changeItemForBooking() {
        $newType = $_POST['data']['newtype'];
        $roomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->setNewRoomType($this->getSelectedName(), $roomId, null, $newType);
        $this->loadBookingList();
        echo "<script>";
        echo "thundashop.framework.reprintPage();";
        echo "</script>";
    }

    public function saveSettings() {
        foreach ($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function render() {
        if (!$this->getSelectedName()) {
            echo "You need to specify a booking engine first.";
            return;
        }
        if(!isset($_POST['event'])) {
//            unset($_SESSION['pmsavailabilitytimelinedata']);
        }
        $this->includefile("timelineview");
    }
    
    public function getData() {
        if(isset($_SESSION['pmsavailabilitytimelinedata'])) {
            return unserialize($_SESSION['pmsavailabilitytimelinedata']);
        }
        return array();
    }
    
    public function loadHover() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedName(), $_POST['data']['bookingid']);
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        echo "<b>" . $user->fullName . "</b><br>";
        foreach($booking->rooms as $room) {
            if($room->bookingId == $_POST['data']['bookingid']) {
                echo date("d.m.Y", strtotime($room->date->start)) . " - " . date("d.m.Y", strtotime($room->date->end)) . "<br>";
                echo "Room : " . $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $room->bookingItemId)->bookingItemName . "<bR>";
                if($room->guests[0]->name)
                    echo "Guest : " . $room->guests[0]->name;
            }
        }
    }
    
    public function getStart() {
        $data = $this->getData();
        if(isset($data['start'])) {
            $time = $data['start'];
        } else {
            $time = time();
        }
        return strtotime(date("d.m.Y 16:00", $time));
    }

    public function getEnd() {
        $data = $this->getData();
        $time = time()+(86400*7);
        if(isset($data['end'])) {
            $time = $data['end'];
        }
        return strtotime(date("d.m.Y 06:00", $time));
    }
    
    public function getInterval() {
        $data = $this->getData();
        if(isset($data['interval'])) {
            return $data['interval'];
        }
        return 60*60*24;
    }

    public function showTimeLine() {
        $_POST['data']['start'] = strtotime($_POST['data']['start']);
        $_POST['data']['end'] = strtotime($_POST['data']['end']);
        $_POST['data']['compactmode'] = $_POST['data']['compactmode'] == "true";
        $_SESSION['pmsavailabilitytimelinedata'] = serialize($_POST['data']);
    }

    public function printTypes() {
        $data = $this->getData();
        if(isset($data['viewtype'])) {
            return ($data['viewtype'] == "types");
        }
        return true;
    }

    public function makeInitials($name) {
        $inits = "";
        $names = explode(" ", $name);
        foreach($names as $name) {
            $inits .= substr($name, 0, 1);
        }
        return $inits;
    }

    public function getCompactView() {
        $data = $this->getData();
        if(!isset($data['compactmode'])) {
            return false;
        }
        return $data['compactmode'];
    }

}
?>

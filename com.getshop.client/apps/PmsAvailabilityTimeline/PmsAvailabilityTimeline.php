<?php
namespace ns_176ea989_c7bb_4cef_a4bd_0c8421567e0b;

class PmsAvailabilityTimeline extends \WebshopApplication implements \Application {
    public $roomWhereNotClosed = false;
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAvailabilityTimeline";
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function closeAllRoomsForBooking() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $config->closedUntil = $this->convertToJavaDate(strtotime($_POST['data']['closeRoomDate'] . " " . "12:00"));
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
        $this->loadCloseRoomBox();
    }
    
    public function openAllRoomsForBooking() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $config->closedUntil = null;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
        $this->loadCloseRoomBox();
    }
    
    public function closeRooms() {
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        $comment = "closed by: " . $user->fullName . ", ";
        if($_POST['event'] == "closeRooms") {
            $startTime = $_POST['data']['start'];
            $endTime = $_POST['data']['end'];
            if(!$endTime || !$startTime) {
                echo "Please select a start and end date";
            } else {
                $start = $this->convertToJavaDate(strtotime($startTime . " 14:00"));
                $end = $this->convertToJavaDate(strtotime($endTime . " 11:00"));
                $comment .= $_POST['data']['closeroomcomment'];
                foreach($_POST['data'] as $key => $val) {
                    if(strstr($key, "item_") && $val == "true") {
                        $itemId = str_replace("item_", "", $key);
                        $closed = $this->getApi()->getPmsManager()->closeItem($this->getSelectedName(), $itemId, $start, $end, $comment);
                        $bookingItem = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $itemId);
                        if($closed) {
                            echo "<div><i class='fa fa-check'></i> " . $bookingItem->bookingItemName . " is now closed</div>";
                        } else {
                            echo "<div style='color:red;'><i class='fa fa-warning'></i> " . $bookingItem->bookingItemName . " Unable to close room, this room is not available for closing.";
                            echo "</div>";
                        }
                    }
                }
            }
        }
        echo "<script>";
        echo "thundashop.framework.reprintPage();";
        echo "</script>";
        
        $this->loadCloseRoomBox();
    }
    
    public function closeOtas() {
        if(isset($_POST['data']['clicksubmit'])) {
            $this->getApi()->getWubookManager()->deleteRestriction($this->getSelectedName(), $_POST['data']['id']);
            $this->loadCloseRoomBox();
            return;
        }
        
        $startTime = $_POST['data']['start'];
        $endTime = $_POST['data']['end'];
        if(!$endTime || !$startTime) {
            echo "Please select a start and end date";
        } else {
            $restriction = new \core_wubook_WubookAvailabilityRestrictions();
            $restriction->start = $this->convertToJavaDate(strtotime($startTime  . " 14:00"));
            $restriction->end = $this->convertToJavaDate(strtotime($endTime . "  11:00"));
            
            foreach($_POST['data'] as $key => $event) {
                if(stristr($key, "type_") && $event == "true") {
                    $typeId = str_replace("type_", "", $key);
                    $restriction->types[] = $typeId;
                }
            }
            $this->getApi()->getWubookManager()->addRestriction($this->getSelectedName(), $restriction);
        }
        
        echo "<script>";
        echo "thundashop.framework.reprintPage();";
        echo "</script>";
            
        $this->loadCloseRoomBox();
    }
    
    public function loadCloseRoomBox() {
        $this->includefile("closeRoomOptions");
    }
    
    public function findTypesToMove() {
        echo "<div style='font-size: 12px;'>";
        echo "Hey there, there are no rooms available, do you wish to swap room with someone?<br><br>";
        $movetotype = $_POST['data']['movetotype'];
        $moveRoom = $_POST['data']['moveRoom'];
        $rooms = $this->getApi()->getPmsManager()->getRoomsToSwap($this->getSelectedName(), $moveRoom, $movetotype);
        
        foreach($rooms as $room) {
            /* @var $room \core_pmsmanager_PmsRoomSimple */
            $guest = $room->guest[0]->name;
            if($guest) {
                $guest = "($guest)";
            }
            echo "<div style=' width: 350px;white-space: nowrap; overflow: hidden;text-overflow: ellipsis;'>";
            echo "<input type='checkbox' gsname='swapwithroom_".$room->pmsRoomId."'></input> ";
            echo date("d.m.Y", $room->start/1000) . " - " .  date("d.m.Y", $room->end/1000) . " " . $room->owner;
            echo "</div>";
        }
        echo "</div>";
    }
    
    public function loadBookingList() {
        $type = $_POST['data']['type'];
        $selectedType = $type;
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
            if($room->bookingTypeId != $selectedType) {
                continue;
            }
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
                echo "<div class='warnmovepeople'></div>";
                echo "<input type='hidden' gsname='roomid' value='". $room->pmsRoomId . "'>";
                echo "<input type='hidden' gsname='day' value='". $_POST['data']['day'] . "'>";
                echo "<input type='hidden' gsname='type' value='". $_POST['data']['type'] . "'>";
                echo "<select gsname='newtype' class='changetypeonbookingselector'>";
                echo "<option>Select a type to move to</option>";
                foreach($types as $type) {
                    $items = $this->getApi()->getBookingEngine()->getNumberOfAvailable($this->getSelectedName(), 
                            $type->id, 
                            $this->convertToJavaDate($room->start/1000), 
                            $this->convertToJavaDate($room->end/1000));
                    
                    if($type->id == $type) {
                        continue;
                    }
                    echo "<option value='".$type->id."' count='".$items."'>" . $type->name . " (" . $items .")". "</option>";
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
        
        $swapRoomList = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "swapwithroom_") && $val == "true") {
                $swapRoomList[] = str_replace("swapwithroom_", "", $key);
            }
        }
        
        if(sizeof($swapRoomList) > 0) {
            $error = $this->getApi()->getPmsManager()->swapRoom($this->getSelectedName(), $roomId, $swapRoomList);
        } else {
            $error = $this->getApi()->getPmsManager()->setNewRoomType($this->getSelectedName(), $roomId, null, $newType);
        }
        if($error) {
            echo "<hr>";
            echo $error;
            echo "<hr>";
        }
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
        if($this->getConfigurationSetting("viewtype") == "new") {
            $this->includefile("timelineviewnew");
        } else {
            $this->includefile("timelineview");
        }
    }
    
    public function isVirtuallyAssigned($itemId, $value) {
        foreach ($value->virtuallyAssigned as $key => $assignedItemId) {
            if ($assignedItemId == $itemId) {
                return true;
            }
        }
        
        return false;
    }
    
    public function getData() {
        if(isset($_SESSION['pmsavailabilitytimelinedata'])) {
            return unserialize($_SESSION['pmsavailabilitytimelinedata']);
        }
        return array();
    }
    
    public function loadHover() {
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedName(), $_POST['data']['bookingid']);
        if(!$booking) {
            $bookingfromengine = $this->getApi()->getBookingEngine()->getBooking($this->getSelectedName(), $_POST['data']['bookingid']);
            $source  = $bookingfromengine->source;
            if($source == "cleaning") {
                echo "This room is marked as unavailable because it has not been cleaned.";
            } else if($source == "closed") {
                echo "This has been closed off.";
            } else {
                echo "This booking is a placeholder booking for source: " . $bookingfromengine->source;
            }
            return;
        }
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        echo "<b>" . $user->fullName . "</b><br>";
        foreach($booking->rooms as $room) {
            if($room->bookingId == $_POST['data']['bookingid']) {
                echo date("d.m.Y", strtotime($room->date->start)) . " - " . date("d.m.Y", strtotime($room->date->end)) . "<br>";
                if($room->bookingItemId) {
                    echo "Room : " . $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $room->bookingItemId)->bookingItemName . "<bR>";
                } else {
                    echo "Room : Forecasted this room<br>";
                }
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

    public function deletebooking() {
        $this->getApi()->getBookingEngine()->deleteBooking($this->getSelectedName(), $_POST['data']['id']);
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

    public function getSelectedDefinedFilter() {
        $data = $this->getData();
        if(isset($data['filtertouse'])) {
            return $data['filtertouse'];
        }
        return "";
    }
    
    public function showTimeLine() {
        $_POST['data']['start'] = strtotime($_POST['data']['start']);
        $_POST['data']['end'] = strtotime($_POST['data']['end']);
        if(isset($_POST['data']['compactmode'])) {
            $_POST['data']['compactmode'] = $_POST['data']['compactmode'] == "true";
        }
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

    
    public function closeRoom() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " 15:00"));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " 09:00"));
        $item = $_POST['data']['itemid'];
        $closed = $this->getApi()->getPmsManager()->closeItem($this->getSelectedName(), $item, $start, $end, "closed");
        $this->roomWhereNotClosed = !$closed;
    }

    public function createDeleteBookingIcon($id) {
        return "<i class='fa fa-trash-o' gstype='clicksubmit' method='deletebooking' gsvalue='".$id."' gsname='id' style='cursor:pointer;float:left; padding-left: 5px; padding-top: 3px;'></i>";
    }
    public function uploadFile() {
        $content = strstr($_POST['data']['fileBase64'], "base64,");
        $rawContent = $_POST['data']['fileBase64'];
        $contentType = substr($rawContent, 0, strpos($rawContent, ";base64,"));
        if($contentType) {
            $contentType = str_replace("data:", "", $contentType);
            $contentType = trim($contentType);
        }

        $content = str_replace("base64,", "", $content);
        $content = base64_decode($content);
        $imgId = \FileUpload::storeFile($content);
        
        $entry = new \core_usermanager_data_UploadedFiles();
        $entry->fileName = $_POST['data']['fileName'];
        $entry->fileId = $imgId;
        $entry->createdDate = $this->convertToJavaDate(time());
        $entry->contentType = $contentType;
        
        $bid = $_POST['data']['bookingid'];
        $name = $this->getSelectedName();
        $booking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($name, $bid);
        $user = $this->getApi()->getUserManager()->getUserById($booking->userId);
        $user->files[] = $entry;
        $this->getApi()->getUserManager()->saveUser($user);
    }
}
?>

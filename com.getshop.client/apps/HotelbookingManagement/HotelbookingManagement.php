<?php
namespace ns_15e67fa1_c862_4bc3_9b17_dfd818f30712;

class HotelbookingManagement extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "HotelbookingManagement";
    }
    
    public function getAvailablePositions() {
        return "left";
    }
    
    public function saveArxData() {
        $this->setConfigurationSetting("arx_server", $_POST['data']['arx_server']);
        $this->setConfigurationSetting("arx_username", $_POST['data']['arx_username']);
        $this->setConfigurationSetting("arx_password", $_POST['data']['arx_password']);
        $this->getApi()->getHotelBookingManager()->setArxConfiguration($_POST['data']['arx_server'],  $_POST['data']['arx_username'], $_POST['data']['arx_password']);
    }
    
    public function updateAdminFee() {
        $fee = $_POST['data']['fee'];
        $reservation = $_POST['data']['ref'];
        echo $fee;
        $reservation = $this->getApi()->getHotelBookingManager()->getReservationByReferenceId($reservation);
        $reservation->bookingFee = $fee;
        $this->getApi()->getHotelBookingManager()->updateReservation($reservation);
    }
    
    public function saveContractData() {
        $this->setConfigurationSetting("utleier_navn", $_POST['data']['utleier_navn']);
        $this->setConfigurationSetting("utleier_adresse", $_POST['data']['utleier_adresse']);
        $this->setConfigurationSetting("utleier_postnr", $_POST['data']['utleier_postnr']);
        $this->setConfigurationSetting("utleier_sted", $_POST['data']['utleier_sted']);
    }
    
    public function addType() {
        $type = new \core_hotelbookingmanager_RoomType();
        $type->id = $_POST['data']['id'];
        $type->name = $_POST['data']['name'];
        $type->description_en = $_POST['data']['no_desc'];
        $type->description_no = $_POST['data']['no_desc'];
        $this->getApi()->getHotelBookingManager()->saveRoomType($type);
    }
    
    public function removeBookingReference() {
        $id = $_POST['data']['id'];
        $this->getApi()->getHotelBookingManager()->deleteReference($id);
    }
    
    
    public function deleteType() {
        $id = $_POST['data']['typeId'];
        $this->getApi()->getHotelBookingManager()->removeRoomType($id);
    }
    function startsWith($haystack, $needle) {
        $length = strlen($needle);
        return (substr($haystack, 0, $length) === $needle);
    }
    function addRoom() {
         $room = new \core_hotelbookingmanager_Room();
         if(isset($_POST['data']['newroom'])) {
             $room->roomName = $_POST['data']['roomname'];
             $this->getApi()->getHotelBookingManager()->saveRoom($room);
         } else {
            foreach($_POST['data'] as $id => $val) {
                if($this->startsWith($id, "id_")) {
                   $id = str_replace("id_", "", $id);
                   $room = $this->getApi()->getHotelBookingManager()->getRoom($id);

                   $room->roomType = $_POST['data']['roomtype_'.$id];
                   $room->roomName = $_POST['data']['roomname_'.$id];
                   $room->lockId = $_POST['data']['lockid_'.$id];
                   $room->currentCode = $_POST['data']['lockcode_'.$id];

                   if($_POST['data']['available_'.$id] == "false") {
                       $room->isActive = "false";
                   } else {
                       $room->isActive = "true";
                   }
                   $this->getApi()->getHotelBookingManager()->saveRoom($room);
                }
            }
         }
    }
    
    public function getName() {
        return "HotelbookingManagement";
    }

    public function loadArxLog() {
        $this->includefile("arxlog");
    }
    
    public function postProcess() {
        
    }

    
    public function reprint() {
        
    }
    
    
    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("HotelbookingManagement");
    }

    public function deleteRoom() {
        $id = $_POST['data']['roomid'];
        $this->getApi()->getHotelBookingManager()->removeRoom($id);
    }
    
    public function loadMoveRoom() {
        $this->includefile("moveroom");
    }
    
    public function moveRoom() {
        $refid = $_POST['data']['refid'];
        $rooms = $_POST['data']['rooms'];
        
        $reference = $this->getApi()->getHotelBookingManager()->getReservationByReferenceId($refid);
        $reference->startDate = date("M d, Y h:m:s A", strtotime($_POST['data']['startdate']));
        $reference->endDate = date("M d, Y h:m:s A", strtotime($_POST['data']['enddate']));
        $this->getApi()->getHotelBookingManager()->updateReservation($reference);

        foreach($rooms as $id => $room) {
            $this->getApi()->getHotelBookingManager()->moveRoomOnReference($refid, $id, $room);
        }
    }
    
    /**
     * 
     * @param \core_hotelbookingmanager_Room $room
     * @param \core_hotelbookingmanager_RoomType[] $types
     */
    public function printRoomRow($room, $types) {
        
        $falseselected = "";
        $unavclass = "";
        if(!$room->isActive) {
            $falseselected = "SELECTED";
            $unavclass = "unavailable";
        }
        
        
        echo "<tr class='existingroomrow ".$unavclass."'>";
        echo "<td width='10'>";
        echo "<input type='hidden' value='". $room->id."' gsname='id_".$room->id."'>";
        echo "<i class='fa fa-trash-o' roomid='".$room->id."'></i></td>";
        echo "<td width='10'>";
        echo "<select gsname='roomtype_".$room->id."'>";
        echo "<option value=''>Set a room type</option>";
        foreach($types as $type) {
            $selected = "";
            if($type->id == $room->roomType) {
                $selected = "SELECTED";
            }
            echo "<option value='".$type->id."' $selected>".$type->name."</option>";
        }
        echo "</select>";
        echo "</td>";
        echo "<td>";
        
        echo "<select gsname='available_".$room->id."'>";
        echo "<option value='true'>". $this->__f("Available") . "</option>";
        echo "<option value='false' $falseselected>". $this->__f("Not available") . "</option>";
        echo "</select>";
        echo "</td>";
        echo "<td><input gsname='roomname_".$room->id."' value='" . $room->roomName . "'></td>";
        echo "<td><input gsname='lockid_".$room->id."' value='" . $room->lockId . "'></td>";
        echo "</tr>";
    }
}
?>

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
    
    public function addType() {
        $type = new \core_hotelbookingmanager_RoomType();
        $type->id = $_POST['data']['id'];
        $type->name = $_POST['data']['name'];
        $type->description_en = $_POST['data']['no_desc'];
        $type->description_no = $_POST['data']['no_desc'];
        $this->getApi()->getHotelBookingManager()->saveRoomType($type);
    }
    
    public function deleteType() {
        $id = $_POST['data']['typeId'];
        $this->getApi()->getHotelBookingManager()->removeRoomType($id);
    }
    
    function addRoom() {
         $room = new \core_hotelbookingmanager_Room();
         
         if(isset($_POST['data']['id']))
             $room->id = $_POST['data']['id'];
         if(isset($_POST['data']['roomtype']))
             $room->roomType = $_POST['data']['roomtype'];
         if(isset($_POST['data']['roomname']))
            $room->roomName = $_POST['data']['roomname'];
         if(isset($_POST['data']['lockid']))
            $room->lockId = $_POST['data']['lockid'];
         if(isset($_POST['data']['lockcode']))
            $room->currentCode = $_POST['data']['lockcode'];
         
         $this->getApi()->getHotelBookingManager()->saveRoom($room);
    }
    
    public function getName() {
        return "HotelbookingManagement";
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
    
    /**
     * 
     * @param \core_hotelbookingmanager_Room $room
     * @param \core_hotelbookingmanager_RoomType[] $types
     */
    public function printRoomRow($room, $types) {
        echo "<tr class='existingroomrow' gstype='form' method='addRoom'>";
        echo "<td width='10'>";
        echo "<input type='hidden' value='". $room->id."' gsname='id'>";
        echo "<i class='fa fa-trash-o' roomid='".$room->id."'></i></td>";
        echo "<td width='10'>";
        echo "<select gsname='roomtype'>";
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
        echo "<td><input gsname='roomname' value='" . $room->roomName . "'></td>";
        echo "<td><input gsname='lockid' value='" . $room->lockId . "'></td>";
        echo "<td><input gsname='lockcode' value='" . $room->currentCode . "'></td>";
        echo "<td width='10'><i class='fa fa-floppy-o' style='cursor:pointer; color:green;' gstype='submit'></td>";
        echo "</tr>";
    }
}
?>

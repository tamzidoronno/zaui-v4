<?php
namespace ns_8f7198af_bd49_415a_8c39_9d6762ef1440;

class ComfortDashBoard extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ComfortDashBoard";
    }

    public function render() {
        echo "<div style='max-width:1600px; margin: auto;margin-top: 30px;'>";
        $this->includefile("dashboardintroduction");
        $this->includefile("dashboardoverview");
        echo "</div>";
    }

    public function translateEventFromText($event) {
        $res = $this->getEventsArray();

        if(isset($res[$event])) {
            return $res[$event];
        } 
        return "";
    }

    public function getEventsArray() {
        $res = array();
        
        $res['room_starting_0_hours'] = "Room is started.";
        $res['room_starting_4_hours'] = "Room starting in 4 hours";
        $res['room_starting_12_hours'] = "Room starting in 12 hours";
        $res['room_starting_24_hours'] = "Room starting in 1 day";
        $res['room_starting_48_hours'] = "Room starting in 2 days";
        $res['room_started_4_hours'] = "Room started 4 hours ago";
        $res['room_started_12_hours'] = "Room started 12 hours ago";
        $res['room_started_24_hours'] = "Room started 24 hours ago";
        $res['room_started_48_hours'] = "Room started 48 hours ago";
        $res['room_changed'] = "Room has been changed while stay is started";
        $res['room_resendcode'] = "When resending a code to the guest";
        $res['room_dooropenedfirsttime'] = "When guest opens the door";
        $res['room_ended_0_hours'] = "Room has ended.";
        $res['room_ended_24_hours'] = "Room ended 1 day ago";
        $res['room_ended_48_hours'] = "Room ended two days ago";
        $res['room_added_to_arx'] = "Code sent to guest";
        $res['room_removed_from_arx'] = "Code inactive for guest";
        return $res;
    }

}
?>

<?php
namespace ns_2b4a865c_6aed_416e_bf52_ab6e2428bd1f;

class PmsWubookConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsWubookConfiguration";
    }

    public function render() {
        $this->includefile("wubookadminpanel");
        $this->includefile("actiondone");
    }
    
    public function addReservation() {
        $id = $_POST['data']['id'];
        $this->getApi()->getWubookManager()->addBooking($this->getSelectedMultilevelDomainName(), $id);
    }

    
    public function doAction() {
        
    }
    
    public function saveCredentials() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->wubookusername = $_POST['data']['username'];
        $config->wubookpassword = $_POST['data']['password'];
        $config->wubooklcode = $_POST['data']['lcode'];
        $config->ignoreNoShow = $_POST['data']['ignoreNoShow'] == "true";
        $config->increaseByPercentage = $_POST['data']['increaseByPercentage'];
        $config->numberOfRoomsToRemoveFromBookingCom = $_POST['data']['numberOfRoomsToRemoveFromBookingCom'];
        $config->daysAllowedInTheFuture = $_POST['data']['daysAllowedInTheFuture'];
        if($config->daysAllowedInTheFuture > 500) {
            $config->daysAllowedInTheFuture = 500;
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }

    public function updateRoomData() {
        $curData = $this->getApi()->getWubookManager()->getWubookRoomData($this->getSelectedMultilevelDomainName());
        foreach($_POST['data'] as $id => $val) {
            if(stristr($val, ";")) {
                $splitted = explode(";", $val);
                $curData->{$id}->wubookroomid = $splitted[0];
                $curData->{$id}->virtualWubookRoomIds = $val;
            } else {
                $curData->{$id}->virtualWubookRoomIds = "";
                $curData->{$id}->wubookroomid = $val;
            }
        }
        $this->getApi()->getWubookManager()->saveWubookRoomData($this->getSelectedMultilevelDomainName(), $curData);
    }
    
    public function printBookingTable($res) {
        if(!$res) {
            echo "None found";
            return;
        }
        echo "<table width='100%'>";
        echo "<tr>";
        echo "<th align='left'>Reservation code</th>";
        echo "<th align='left'>Name</th>";
        echo "<th align='left'>Arrival</th>";
        echo "<th align='left'>Departure</th>";
        echo "<th>Modified</th>";
        echo "<th>Deleted</th>";
        echo "<th>Added</th>";
        echo "<th>Number of rooms</th>";
        echo "<th>Action</th>";
        echo "</tr>";
        foreach($res as $r) {
            echo "<tR>";
            echo "<td>" . $r->reservationCode . "</td>";
            echo "<td>" . $r->name . "</td>";
            echo "<td>" . $r->arrivalDate . "</td>";
            echo "<td align='center'>" . $r->depDate . "</td>";
            echo "<td align='center'>" . $r->wasModified . "</td>";
            echo "<td>";
            echo $r->delete ? "yes" : "no"; 
            echo "</td>";
            echo "<td>";
            echo $r->isAddedToPms ? "yes" : "no"; 
            echo "</td>";
            echo "<td align='center'>" . sizeof($r->rooms) . "</td>";
            echo "<td align='center'><span gstype='clicksubmit' method='addReservation' gsname='id' gsvalue='".$r->reservationCode."'>add</span> / delete</td>";
            echo "</tr>";
        }
        echo "</table>";
    }

    
}
?>

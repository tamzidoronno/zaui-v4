<?php
namespace ns_2d93c325_b7eb_4876_8b08_ae771c73f95a;

class WubookAdmin extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "WubookAdmin";
    }
    
    public function setEngine() {
        $engine = $_POST['data']['engine'];
        $this->setConfigurationSetting("engine_name", $engine);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function addReservation() {
        $id = $_POST['data']['id'];
        $this->getApi()->getWubookManager()->addBooking($this->getSelectedName(), $id);
    }

    public function render() {
        if (!$this->getSelectedName()) {
            echo "You need to specify a booking engine first<br>";
            $engines = $this->getApi()->getStoreManager()->getMultiLevelNames();
            foreach($engines as $engine) {
                echo "<span gstype='clicksubmit' style='font-size: 20px; cursor:pointer; display:inline-block; margin-bottom: 20px;' method='setEngine' gsname='engine' gsvalue='$engine'>$engine</span><br>"; 
            }
            return;
        }
        $this->includefile("wubookadminpanel");
        $this->includefile("actiondone");
    }
    
    public function doAction() {
        
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
        echo "<th>Number of rooms</th>";
        echo "<th>Action</th>";
        echo "</tr>";
        foreach($res as $r) {
            echo "<tR>";
            echo "<td>" . $r->reservationCode . "</td>";
            echo "<td>" . $r->name . "</td>";
            echo "<td>" . $r->arrivalDate . "</td>";
            echo "<td>" . $r->depDate . "</td>";
            echo "<td align='center'>" . sizeof($r->rooms) . "</td>";
            echo "<td align='center'><span gstype='clicksubmit' method='addReservation' gsname='id' gsvalue='".$r->reservationCode."'>add</span> / delete</td>";
            echo "</tr>";
        }
        echo "</table>";
    }

}
?>

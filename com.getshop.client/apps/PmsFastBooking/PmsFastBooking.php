<?php
namespace ns_8e6f79e0_754f_443f_b699_5a493964d8e9;

class PmsFastBooking extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsFastBooking";
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }
    
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function startBooking() {
        $minutes = $this->getConfigurationSetting("minutes");
        $type = $this->getConfigurationSetting("type");
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());
        $room = new \core_pmsmanager_PmsBookingRooms();
        $room->bookingItemTypeId = $type;
        $room->date = new \core_pmsmanager_PmsBookingDateRange();
        $room->date->start = $this->convertToJavaDate(time());
        if($minutes) {
            $room->date->end = $this->convertToJavaDate(time()+($minutes*60));
        }
        $booking->rooms = array();
        $booking->rooms[] = $room;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    public function render() {
        if(isset($_POST['event']) && $_POST['event'] == "startBooking") {
            ?>
            <script>
                thundashop.common.goToPage("summary_<?php echo $this->getSelectedName(); ?>"); 
            </script>
            <?php
        }
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first. <br>";
            $engns = $this->getApi()->getStoreManager()->getMultiLevelNames();

            foreach($engns as $engine) {
                echo '<div gstype="clicksubmit" style="font-size: 16px; cursor:pointer; margin-top: 10px;" method="selectEngine" gsname="name" gsvalue="'.$engine.'">' . $engine . "</div>";
            }
        }
        $text = $this->getConfigurationSetting("buttontext");
        if(!$text) {
            $text = "Unnamed button";
        }
        echo "<span class='shop_button radius green' gstype='clicksubmit' method='startBooking'>$text</span>";
    }
    
}
?>

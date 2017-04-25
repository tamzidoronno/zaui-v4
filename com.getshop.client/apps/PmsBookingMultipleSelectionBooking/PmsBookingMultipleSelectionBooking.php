<?php
namespace ns_0f6913fb_b28d_49b1_9c6b_7aa023bd21bb;

class PmsBookingMultipleSelectionBooking extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingMultipleSelectionBooking";
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first. <br>";
            $engns = $this->getApi()->getStoreManager()->getMultiLevelNames();

            foreach($engns as $engine) {
                echo '<div gstype="clicksubmit" style="font-size: 16px; cursor:pointer; margin-top: 10px;" method="selectEngine" gsname="name" gsvalue="'.$engine.'">' . $engine . "</div>";
            }
            return;
        }
        $this->includefile('pmsbookingmultipleselectionbooking');
    }
    
    public function initBooking() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());
        $booking->sessionStartDate = $this->convertToJavaDate(strtotime(date("d.m.Y", strtotime($_POST['data']['start'])) . " " . $config->defaultStart));
        $booking->sessionEndDate = $this->convertToJavaDate(strtotime(date("d.m.Y", strtotime($_POST['data']['end'])) . " " . $config->defaultEnd));
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    public function getText($key) {
        return $this->getConfigurationSetting($key);
    }
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }
}
?>

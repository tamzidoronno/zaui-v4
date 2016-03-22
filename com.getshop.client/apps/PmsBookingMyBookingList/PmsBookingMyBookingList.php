<?php
namespace ns_b675ce83_d771_4332_ba09_a54ed8537282;

class PmsBookingMyBookingList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingMyBookingList";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first. <br>";
            $engns = $this->getApi()->getStoreManager()->getMultiLevelNames();

            foreach($engns as $engine) {
                echo '<div gstype="clicksubmit" style="font-size: 16px; cursor:pointer; margin-top: 10px;" method="selectEngine" gsname="name" gsvalue="'.$engine.'">' . $engine . "</div>";
            }
        }
        $this->includefile("bookinglist");
    }
    
    public function deleteRoom() {
        $bookingId = $_POST['data']['bookingid'];
        $roomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedName(), $bookingId, $roomId);
    }
    
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }

    /**
     * @return \core_bookingengine_data_BookingItem[]
     */
    public function getBookingItems() {
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        return $this->indexList($items);
    }

    /**
     * @return \core_bookingengine_data_BookingItemType[]
     */
    public function getBookingItemTypes() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        return $this->indexList($types);
    }

}
?>

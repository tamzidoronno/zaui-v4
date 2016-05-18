<?php
namespace ns_7ca01dc3_49e7_4bad_a0f9_4f1eae1199d6;

class PmsAddonSelection extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAddonSelection";
    }

    public function toggleAddon() {
        $type = $_POST['data']['type'];
        $remove = false;
        if($this->hasAddonType($type)) {
            $remove = true;
        }
        $current = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        $bookingId = $current->id;
        foreach($current->rooms as $room) {
            $roomId = $room->pmsBookingRoomId;
            $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedName(), $type, $bookingId, $roomId, $remove);
        }
    }

    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
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
        
        $this->includefile("addonselections");
    }

    public function hasAddonType($type) {
        $current = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        foreach($current->rooms as $room) {
            foreach($room->addons as $addon) {
                if($addon->addonType == $type) {
                    return true;
                }
            }
        }
        return false;
    }

}
?>

<?php
namespace ns_b72ec093_caa2_4bd8_9f32_e826e335894e;

class PmsAddAddonsList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAddAddonsList";
    }

    public function render() {
        echo "FDASF";
        $this->includefile("addaddonslist");
    }
    
    public function addProductToBooking() {
        $app = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $app->setData();
        $productId = $_POST['data']['productId'];
        $room = $app->getSelectedRoom();
        
        $addons = $this->getApi()->getPmsManager()->createAddonsThatCanBeAddedToRoom($this->getSelectedMultilevelDomainName(), $productId, $room->pmsBookingRoomId);
        
        $initList = array();
        foreach($room->addons as $addon) {
            if(isset($addon->notAddedYet) && $addon->productId == $productId && !$addon->isSingle) {
                continue;
            }
            $initList[] = $addon;
        }
        
        foreach($addons as $addon) {
            $addon->notAddedYet = true;
            $initList[] = $addon;
        }
        $room->addons = $initList;
        $app->updateRoom($room);
    }
    
}
?>

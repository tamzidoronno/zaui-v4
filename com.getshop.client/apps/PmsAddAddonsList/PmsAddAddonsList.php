<?php
namespace ns_b72ec093_caa2_4bd8_9f32_e826e335894e;

class PmsAddAddonsList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAddAddonsList";
    }

    public function render() {
        $this->includefile("addaddonslist");
    }
    
    public function addProductToBooking() {
        $app = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $app->setData();
        
        $addon = new \core_pmsmanager_PmsBookingAddonItem();
        $addon->count = 1;
        $addon->name = $this->getApi()->getProductManager()->getProduct($_POST['data']['productId'])->name;
        $addon->productId = $_POST['data']['productId'];
        $addon->price = 100;
        
        $room = $app->getSelectedRoom();
        $room->addons[] = $addon;
        $app->updateRoom($room);
    }
    
}
?>

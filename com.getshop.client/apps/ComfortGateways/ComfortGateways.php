<?php
namespace ns_d40f9875_95e5_4ee2_a041_989efa0f4f5c;

class ComfortGateways extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ComfortGateways";
    }

    public function render() {
        echo "<div style='max-width:1600px; margin: auto;margin-top: 30px;'>";
        $this->includefile("gatewayslist");
        echo "</div>";
    }
    
    public function createGateway() {
        $device = new \core_gsd_GetShopDevice();
        $device->name = $_POST['data']['name'];
        $device->token = $_POST['data']['token'];
        $this->getApi()->getGdsManager()->saveDevice($device);
    }
}
?>

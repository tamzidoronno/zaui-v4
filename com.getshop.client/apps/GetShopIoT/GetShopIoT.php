<?php
namespace ns_ca4162a4_b26b_4920_8d51_80b809546167;

class GetShopIoT extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopIoT";
    }
    
    public function updateIotDevice() {
        $address = $_POST['data']['address'];
        $id = $_POST['data']['identification'];
        if(!stristr($address, "http")) {
            return;
        }
        
        $information = new \core_gsd_IotDeviceInformation();
        $information->identification = $id;
        
        $device = $this->getApi()->getGdsManager()->getIotDeviceInformation($information);
        $device->address = $address;
        $this->getApi()->getGdsManager()->updateIotDevice($device);
    }

    public function render() {
        $this->includefile("iotdevices");
    }
}
?>

<?php
namespace ns_26958edf_e2a1_4e76_aca0_38e7edfe8c80;

class GetShopDeviceList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointAccessPoint";
    }
    
    public function GdsManager_getDevices() {
        $this->includefile("deviceinner");
    }

    public function render() {
        $this->includefile("accesspointlist");
    }
    
    public function createNewDevice() {
        $device = new \core_gsd_GetShopDevice();
        $device->name = $_POST['data']['name'];
        $device->token = $_POST['data']['token'];
        $device->type = $_POST['data']['type'];
        $this->getApi()->getGdsManager()->saveDevice($device);
    }
    
    public function formatMessageCount() {
        return "";
    }
    
    public function deleteDevice() {
        $this->getApi()->getGdsManager()->deleteDevice($_POST['data']['deviceid']);
    }
}
?>

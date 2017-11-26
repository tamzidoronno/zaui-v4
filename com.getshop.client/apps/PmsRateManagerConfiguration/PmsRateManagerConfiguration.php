<?php
namespace ns_e80c6ab9_fd20_44f5_8dd8_0b7bef4d3d8d;

class PmsRateManagerConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsRateManagerConfiguration";
    }

    public function render() {
        $this->includefile("config");
    }

    public function updateBComInventory() {
        $this->getApi()->getBookingComRateManagerManager()->pushInventoryList($this->getSelectedMultilevelDomainName());
    }
    
    public function pushAllRoomsBookingComRateManager() {
        $this->getApi()->getBookingComRateManagerManager()->pushAllBookings($this->getSelectedMultilevelDomainName());
    }
    
    
    public function updateRateManagerConfig() {
        $hotelid = $_POST['data']['hotelid'];
        $typeMap = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "typeid_")) {
                $id = str_replace("typeid_", "", $key);
                $typeMap[$id] = $val;
            }
        }
        
        $config = $this->getApi()->getBookingComRateManagerManager()->getRateManagerConfig($this->getSelectedMultilevelDomainName());
        $config->roomTypeIdMap = $typeMap;
        $config->hotelId = $hotelid;
        
        $this->getApi()->getBookingComRateManagerManager()->saveRateManagerConfig($this->getSelectedMultilevelDomainName(), $config);
    }
}
?>

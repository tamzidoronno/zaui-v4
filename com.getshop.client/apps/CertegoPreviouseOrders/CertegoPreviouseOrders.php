<?php
namespace ns_f85a81c8_f91a_45b3_a107_31ca3961affd;

class CertegoPreviouseOrders extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CertegoPreviouseOrders";
    }

    public function render() {
        $this->includefile("orderoverview");
    }

    public function getFirstSystemNumber($data) {
        
        if ($data->page2->keys == "true") {
            foreach ($data->page2->keys_setup as $keySetup) {
                if ($keySetup->systemNumber) {
                    return $keySetup->systemNumber;
                }
            }   
        }
        
        if ($data->page2->cylinders == "true") {
            if ($cylindersetup->systemNumber) {
                return $cylindersetup->systemNumber;
            }
        }
        
        return "";
    }

}
?>

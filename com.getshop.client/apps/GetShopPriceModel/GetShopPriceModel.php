<?php
namespace ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e;

class GetShopPriceModel extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopPriceModel";
    }

    public function render() {
        $this->includefile("calculator");
    }
}
?>

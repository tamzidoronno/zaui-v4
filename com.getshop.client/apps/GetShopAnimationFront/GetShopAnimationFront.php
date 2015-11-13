<?php
namespace ns_a77292ec_8e4b_4e68_9729_27b45cdc6720;

class GetShopAnimationFront extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopAnimationFront";
    }

    public function render() {
        $this->includefile("animation");
    }
}
?>

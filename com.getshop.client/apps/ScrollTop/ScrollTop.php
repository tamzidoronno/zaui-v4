<?php
namespace ns_bbb0eafc_73d8_47c1_be53_e4090a2f92d8;

class ScrollTop extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ScrollTop";
    }

    public function render() {
        $this->includefile("scrolltop");
    }
}
?>

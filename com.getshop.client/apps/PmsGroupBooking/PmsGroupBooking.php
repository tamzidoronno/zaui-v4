<?php
namespace ns_086f9a01_7d03_4c30_bbcd_7bc2a473954e;

class PmsGroupBooking extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsGroupBooking";
    }

    public function render() {
        $this->includefile("bookingview");
    }
}
?>
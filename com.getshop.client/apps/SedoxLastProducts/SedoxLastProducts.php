<?php
namespace ns_d0d8b0dc_b2c3_4cf0_92b9_e850abf6e778;

class SedoxLastProducts extends \ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b\SedoxCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxLastProducts";
    }

    public function render() {
        $this->includefile("latestproducts");
    }

    

}
?>

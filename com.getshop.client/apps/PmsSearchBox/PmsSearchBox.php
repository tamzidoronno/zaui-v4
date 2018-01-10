<?php
namespace ns_1ba01a11_1b79_4d80_8fdd_c7c2e286f94c;

class PmsSearchBox extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsSearchBox";
    }

    public function render() {
        $this->includefile("searchbox");
    }
    
    public function search() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->searchBooking();
    }
    
    public function clearFilter() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->clearFilter();
    }
    
    public function showCheckins() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->showCheckins();
    }
    
    public function showCheckOuts() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->showCheckouts();
    }
}
?>

<?php
namespace ns_b794bba8_d25c_49a7_96c9_0aea81a408ee;

class ProMeisterMekonomenDatabase extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterMekonomenDatabase";
    }

    public function render() {
        $this->includefile("search");
    }
    
    public function searchForUsers() {
        // helperfunction for serach, can not be removed.
    }
}
?>

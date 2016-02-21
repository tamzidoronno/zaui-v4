<?php
namespace ns_6daa9962_9ba6_40ae_b1a7_5be23cb89128;

class ProMeisterLogin extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterLogin";
    }

    public function render() {
        $this->includefile("login");
    }
}
?>
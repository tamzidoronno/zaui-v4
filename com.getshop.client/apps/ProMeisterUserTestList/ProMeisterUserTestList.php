<?php
namespace ns_a97cd73e_11f6_4742_ad1e_07ffd9e51e9f;

class ProMeisterUserTestList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterUserTestList";
    }

    public function render() {
        $this->includefile("testlist");
    }
}
?>

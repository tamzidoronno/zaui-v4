<?php
namespace ns_f1f2c4f4_fc7d_4bec_89ec_973ff192ff6d;

class C3RegisterHours extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "C3RegisterHours";
    }

    public function render() {
//        $this->includefile("overview");
        $this->includefile("summary");
    }
}
?>

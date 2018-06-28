<?php
namespace ns_50c1d218_2ba4_4ea6_b96b_f2061434cd41;

class EventPersonalIds extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventPersonalIds";
    }

    public function render() {
        $this->includefile("showids");
    }
}
?>

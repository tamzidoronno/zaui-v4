<?php
namespace ns_b732f369_039b_404d_89f6_bf1a176b29cb;

class SimpleEventBookingButton extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SimpleEventBookingButton";
    }

    public function render() {
        $event = $this->getApi()->getSimpleEventManager()->getEventByPageId($this->getPage()->getId());
        if (!$event) {
            echo "Button here";
            return;
        }
        if ($event && $event->requireSignup) {
            $this->includefile("button");
        } 
    }
}
?>

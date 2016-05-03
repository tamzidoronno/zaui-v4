<?php
namespace ns_2ec5f857_b7bd_42f7_b7f4_db09b273d097;

class EventPrice extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventPrice";
    }

    public function render() {
        $item = $this->getApi()->getEventBookingManager()->getBookingItemTypeByPageId($this->getBookingEngineName(), $this->getPage()->getId());
        if (!$item) {
            echo "No booking item found, price can not be shown";
            return;
        }
        
        $price = $this->getApi()->getEventBookingManager()->getPriceForEventType($this->getBookingEngineName(), $item->id);
        if ($price > 0) {
            echo $this->__f("Price").": ".$price." kr";
        } else {
            echo $this->__f("Price: n/a");
        }
    }
}
?>

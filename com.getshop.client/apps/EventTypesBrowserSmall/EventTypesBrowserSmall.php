<?php
namespace ns_7e6c4a3f_d0d6_4096_84cc_a5829c93581d;

class EventTypesBrowserSmall extends \ns_83df5ae3_ee55_47cf_b289_f88ca201be6e\EngineCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventBrowserSmall";
    }

    public function printLine($type) {
        echo "<a href='/?page=$type->pageId'><div class='eventtype'>$type->name</div></a>";
    }
    
    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("eventbrowser");
        }
    }
    
    /**
     * 
     * @param \core_bookingengine_data_BookingItemType[]
     */
    public function groupThem($types) {
        $grouped = array();
        foreach ($types as $type) {
            if (!isset($grouped[$type->eventItemGroup])) {
                $grouped[$type->eventItemGroup] = array();
            }
            $grouped[$type->eventItemGroup][] = $type;
        }
        
        return $grouped;
    }
}
?>

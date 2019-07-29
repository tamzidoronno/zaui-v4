<?php
namespace ns_4b10210b_5da3_4b01_9bd9_1e6f0a2c7cfc;

class GetShopPmsLockSetup extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopPmsLockSetup";
    }

    public function render() {
        $this->includefile("showrooms");
    }
    
    public function saveGroupConnection() {
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedMultilevelDomainName());
        foreach ($items as $item) {
            if (isset($_POST['data'][$item->id])) {
                $item->lockGroupId = $_POST['data'][$item->id];
                $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedMultilevelDomainName(), $item);
            }
        }
    }
}
?>

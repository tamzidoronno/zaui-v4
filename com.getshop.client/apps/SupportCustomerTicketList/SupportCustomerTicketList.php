<?php
namespace ns_13c0bc5f_ce62_45c5_be76_90237d16de91;

class SupportCustomerTicketList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SupportCustomerTicketList";
    }

    public function render() {
        $this->includefile("list");
    }
}
?>

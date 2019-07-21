<?php
namespace ns_f5e525cc_f11e_4611_93bb_1afacd9aade5;

class TicketViewCustomer extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TicketViewCustomer";
    }

    public function render() {
        $this->includefile("ticketview");
    }
}
?>

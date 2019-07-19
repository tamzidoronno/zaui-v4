<?php
namespace ns_a5175115_187a_4721_90e5_4752fa52ca7a;

class SupportNew extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SupportNew";
    }

    public function render() {
        $this->includefile("createnew");
    }
    
    public function createTicket() {
        $ticketLight = $this->getApi()->getTicketManager()->createLightTicket($_POST['data']['title']);
        $realTicket = $this->getSystemGetShopApi()->getCustomerTicketManager()->createTicket($ticketLight);
        $ticketLight->incrementalTicketId = $realTicket->incrementalId;
        $this->getApi()->getTicketManager()->updateTicket($realTicket->ticketToken, $ticketLight);
        
        $content = new \core_ticket_TicketContent();
        $content->content = $_POST['data']['content'];
        $this->getSystemGetShopApi()->getCustomerTicketManager()->addContent($this->getFactory()->getStore()->id, $ticketLight->ticketToken, $content);
        
        echo $realTicket->incrementalId;
    }
    
}
?>
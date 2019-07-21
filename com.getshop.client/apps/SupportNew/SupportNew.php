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
        $ticketLight->urgency = $_POST['data']['urgency'];
        
        $ticketLight->replyToEmail = $_POST['data']['emailtonotify'];
        $ticketLight->replyToPrefix = $_POST['data']['phoneprefix'];
        $ticketLight->replyToPhone = $_POST['data']['phonenumber'];
        
        $realTicket = $this->getSystemGetShopApi()->getCustomerTicketManager()->createTicket($ticketLight);
        $ticketLight->incrementalTicketId = $realTicket->incrementalId;
        $this->getApi()->getTicketManager()->updateTicket($realTicket->ticketToken, $ticketLight);
        
        $content = new \core_ticket_TicketContent();
        $content->content = nl2br($_POST['data']['content']);
        $this->getSystemGetShopApi()->getCustomerTicketManager()->addContent($this->getFactory()->getStore()->id, $ticketLight->ticketToken, $content);
        
        foreach ($_POST['data']['attachemnts'] as $attachmentId) {
            $this->getSystemGetShopApi()->getCustomerTicketManager()->addAttachmentToTicket(
                    $this->getFactory()->getStore()->id, 
                    $ticketLight->ticketToken,
                    $attachmentId);
        }
        
        echo $realTicket->incrementalId;
    }
 
}
?>
<?php
namespace ns_13c0bc5f_ce62_45c5_be76_90237d16de91;

class SupportCustomerTicketList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SupportCustomerTicketList";
    }

    public function render() {
        if(isset($_GET['subsection']) && $_GET['subsection'] == "predefined") {
            $this->includefile("predefinedtickets");
        } else {
            $this->includefile("list");
        }
    }
    
    public function selectPredefinedTicket() {
        $storeId = $this->getApi()->getStoreManager()->getStoreId();
        $ticket = $this->getSystemGetShopApi()->getCustomerTicketManager()->cloneSetupTicket($_POST['data']['ticketid'], $storeId);
        $this->getApi()->getTicketManager()->createLightTicketOfClonedSetupTicket($ticket);
    }
    
}
?>

<?php
namespace ns_79c775aa_f941_4bac_85d5_ddbecd69f790;

class TicketTransferToAccounting extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TicketTransferToAccounting";
    }

    public function render() {
        echo "<div class='shop_button' gsclick='createAddons'>Create addons</div>";
    }
    
    public function createAddons() {
        $this->getApi()->getPmsManager()->transferTicketsAsAddons($this->getSelectedMultilevelDomainName());
    }
}
?>

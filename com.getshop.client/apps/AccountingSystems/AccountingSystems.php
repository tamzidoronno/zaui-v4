<?php
namespace ns_60a0cbcf_6f12_4798_8d65_f136472fe2a3;

class AccountingSystems extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountingSystems";
    }

    public function render() {
        $this->includefile("systems");
    }
    
    public function setSystem() {
        $this->getApi()->getGetShopAccountingManager()->setSystemTypeInvoice($_POST['data']['systemtypeinvoices']);
        $this->getApi()->getGetShopAccountingManager()->setSystemTypeOther($_POST['data']['systemtypeother']);
    }
}
?>

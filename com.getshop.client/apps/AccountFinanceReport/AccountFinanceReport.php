<?php
namespace ns_e6570c0a_8240_4971_be34_2e67f0253fd3;

class AccountFinanceReport extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountFinanceReport";
    }

    public function render() {
        $this->includefile("freport");
    }
}
?>

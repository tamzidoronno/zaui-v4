<?php
namespace ns_1af1d50d_13b3_4664_8b1c_edfbfaab0269;

class SedoxInvoicingList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxInvoicingList";
    }

    public function render() {
        $this->includefile("sedoxlist");
    }
    
    public function setNorwegian() {
        $_SESSION['SedoxInvoiceList_type'] = "norwegian";
    }
    
    public function setAll() {
        $_SESSION['SedoxInvoiceList_type'] = "all";
    }
    
    public function isNorwegian() {
        if (!isset($_SESSION['SedoxInvoiceList_type']) || $_SESSION['SedoxInvoiceList_type'] == "norwegian") {
            return true;
        }
        
        return false;
    }
}
?>

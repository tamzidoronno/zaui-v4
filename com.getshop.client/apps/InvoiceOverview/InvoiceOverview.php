<?php
namespace ns_3746f382_0450_414c_aed5_51262ae85307;

class InvoiceOverview extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "InvoiceOverview";
    }

    public function render() {
        $this->includefile("filterheader");
        $this->includefile("filterresult");
    }

    public function getFilteredResult() {
        
    }
    
    public function updatefilter() {
        
    }

}
?>

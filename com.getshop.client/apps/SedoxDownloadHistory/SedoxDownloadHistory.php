<?php
namespace ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b;

class SedoxDownloadHistory extends SedoxCommon implements \Application {
    private $orders = null;

    public function getDescription() {   
    }

    public function getName() {
        return "SedoxDownloadHistory";
    }

    public function render() {
        $this->includefile("downloadhistory");
    }

    public function loadOrders() {
        if ($this->orders) {
            return;
        }
        
        $this->orders = $this->getApi()
                ->getSedoxProductManager()
                ->getOrders($this->createFilterData());
        
    }
    
    public function getCurrentOrders() {
        $this->loadOrders();
        return $this->orders;
    }
    
    public function getTotalPages() {
        return $this->getApi()
                ->getSedoxProductManager()
                ->getOrdersPageCount($this->createFilterData());
    }
    
 
}
?>

<?php
namespace ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b;

class SedoxDownloadHistory extends \MarketingApplication implements \Application {
    private $orders = null;
    private $pageSize = 15;
    
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
                ->getOrders($this->getFilterText(), $this->pageSize, $this->getCurrentPage());
        
    }
    
    public function getCurrentOrders() {
        $this->loadOrders();
        return $this->orders;
    }
    
    public function getFilterText() {
        if (isset($_SESSION[$this->getAppInstanceId()."_filterText"])) {
            return $_SESSION[$this->getAppInstanceId()."_filterText"];
        }
        
        return "";
    }
    
    public function setFilter() {
        $_SESSION[$this->getAppInstanceId()."_filterText"] = $_POST['data']['filterText'];
    }
    
    public static function createProductName($product) {
        return $product->brand." ".$product->model." ".$product->engineSize." ".$product->power." ".$product->year;
    }
    
    public function getCurrentPage() {
        if (isset($_SESSION[$this->getAppInstanceId()."_currentPage"])) {
            return $_SESSION[$this->getAppInstanceId()."_currentPage"];
        }
        
        return 1;
    }
    
    public function getTotalPages() {
        return $this->getApi()
                ->getSedoxProductManager()
                ->getOrdersPageCount($this->getFilterText(), $this->pageSize);
    }
}
?>

<?php
namespace ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b;

class SedoxDownloadHistory extends \MarketingApplication implements \Application {
    private $orders = null;
    private $pageSize = 15;
    private $currentProduct = null;
    
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
    
    public function getFilterText() {
        if (isset($_SESSION[$this->getAppInstanceId()."_filterText"])) {
            return $_SESSION[$this->getAppInstanceId()."_filterText"];
        }
        
        return "";
    }
    
    public function setFilter() {
        $_SESSION[$this->getAppInstanceId()."_filterText"] = $_POST['data']['filterText'];
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
                ->getOrdersPageCount($this->createFilterData());
    }
    
    public function setSortering() {
        $_SESSION[$this->getAppInstanceId()."_sortby"] = $_POST['data']['sortBy'];
        $_SESSION[$this->getAppInstanceId()."_ascending"] = !$this->isAscending();
    }
    
    private function isAscending() {
        if (!isset($_SESSION[$this->getAppInstanceId()."_ascending"])) {
            return true;
        }
        
        return $_SESSION[$this->getAppInstanceId()."_ascending"];
    }

    public function getSorting() {
        if (isset($_SESSION[$this->getAppInstanceId()."_sortby"])) {
            return $_SESSION[$this->getAppInstanceId()."_sortby"];
        }
        
        return "sedoxorder_date";
    }
    
    public function createFilterData() {
        $filterdata = new \core_sedox_FilterData();
        $filterdata->ascending = $this->isAscending();
        $filterdata->filterText = $this->getFilterText();
        $filterdata->pageNumber = $this->getCurrentPage();
        $filterdata->pageSize = $this->pageSize;
        $filterdata->sortBy = $this->getSorting();
        return $filterdata;
    }

    public function setCurrentProduct($product) {
        $this->currentProduct = $product;
    }
    
    public function getCurrentProduct() {
        return $this->currentProduct;
    }

}
?>

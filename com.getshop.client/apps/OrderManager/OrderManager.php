<?php

namespace ns_27716a58_0749_4601_a1bc_051a43a16d14;

class OrderManager extends \WebshopApplication implements \Application {

    public $totalPageCount = false;

    public function getDescription() {
        
    }

    public function getName() {
        
    }

    public function postProcess() {
        
    }

    
    public function renderConfig() {
        $this->includefile("orderoverview");
    }
    
    public function renderDashBoardWidget() {
        $this->includefile("dashboardwidget");
    }
    
    public function getDashboardChart() {
        return ['fa-credit-card', 'app.OrderManager.drawChart', $this->__f("Orders")];
    }
    
    public function render() {
  
    }

    public function getOrders() {
        if (isset($_SESSION['gss_orders_filter']) && $_SESSION['gss_orders_filter'] != "") {
            $orders = $this->getApi()->getOrderManager()->searchForOrders($_SESSION['gss_orders_filter'], $this->getPageNumber(), $this->getPageSize());
        } else {
            $orders = $this->getApi()->getOrderManager()->getOrders([], $this->getPageNumber(), $this->getPageSize());
        }
        
        
        return $orders;
    }
    
    public function getPageNumber() {
        if (!isset($_SESSION['gss_orders_currentPageNumber'])) {
            return 1;
        }
        
        return $_SESSION['gss_orders_currentPageNumber'];   
    }
    
    public function nextOrderPage() {
        $pageNumber = $this->getPageNumber();
        $pageNumber++;
        $_SESSION['gss_orders_currentPageNumber'] = $pageNumber;
    }
    
    public function prevOrderPage() {
        $pageNumber = $this->getPageNumber();
        $pageNumber--;
        if ($pageNumber < 1) {
            $pageNumber = 1;
        }
        $_SESSION['gss_orders_currentPageNumber'] = $pageNumber;
    }
    
    public function hasPrevPage() {
        return $this->getPageNumber() > 1;
    }
    
    public function hasNextPage() {
        if ($this->getPageNumber() < $this->getTotalPageCount()) {
            return true;
        }
        return false;
    }

    public function getPageSize() {
        return 30;
    }

    public function getTotalPageCount() {
        if (!$this->totalPageCount) {
            $searchWord = isset($_SESSION['gss_orders_filter']) ? $_SESSION['gss_orders_filter'] : null;
            $this->totalPageCount = $this->totalPageCount = $this->getApi()->getOrderManager()->getPageCount($this->getPageSize(), $searchWord);
        }
        return $this->totalPageCount;
    }

    public function filterOrders() {
        $_SESSION['gss_orders_currentPageNumber'] = 1;
        $_SESSION['gss_orders_filter'] = $_POST['order_filter'];
    }
    
    public function showOrder() {
        
    }
}

?>

<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of GSTableCommon
 *
 * @author ktonder
 */

namespace ns_27716a58_0749_4601_a1bc_051a43a16d14;

class GSTableCommon extends \WebshopApplication {
    /**
     *
     * @var \core_common_FilteredData
     */
    public $filteredData = null;
    
    public $totalPageCount = false;
    
    /** 
     * Override this to load the correct data
     */
    public function loadData() {}
    
    public function getFilteredData() {
        $this->loadData();
        return $this->filteredData->datas;
    }
    
    private function getSessionName($name) {
        return $this->getApplicationSettings()->id."_".$name;
    }
    
    public function getPageSize() {
        $varName = $this->getSessionName('current_pagesize');
        if (isset($_SESSION[$varName]));
            return @$_SESSION[$varName];
        
        return 30;
    }

    public function getTotalPageCount() {
        $this->loadData();
        return $this->filteredData->totalPages;
    }

    public function filterOrders() {
        $_SESSION[$this->getSessionName('gss_orders_currentPageNumber')] = 1;
        $_SESSION[$this->getSessionName('gss_orders_filter')] = $_POST['order_filter'];
        $_SESSION[$this->getSessionName('gss_orders_startdate')] = $_POST['start_date'];
        $_SESSION[$this->getSessionName('gss_orders_enddate')] = $_POST['end_date'];
        $_SESSION[$this->getSessionName('gss_orders_status')] = $_POST['status'];
    }
    
    public function setPageSize() {
        $_SESSION[$this->getSessionName('current_pagesize')] = $_POST['value'];
    }

    public function setCurrentOrder($order) {
        $this->order = $order;
    }
    
    public function createFilter() {
        $filter = new \core_common_FilterOptions();
        $filter->searchWord = isset($_SESSION[$this->getSessionName('gss_orders_filter')]) ? $_SESSION[$this->getSessionName('gss_orders_filter')] : "";
        $filter->pageSize = $this->getPageSize();
        $filter->pageNumber = $this->getPageNumber();
        
        if (isset($_SESSION[$this->getSessionName('gss_orders_startdate')]) && $_SESSION[$this->getSessionName('gss_orders_startdate')]) {
            $filter->startDate = $this->convertToJavaDate(strtotime($_SESSION[$this->getSessionName('gss_orders_startdate')]));
        }
        
        if (isset($_SESSION[$this->getSessionName('gss_orders_enddate')]) && $_SESSION[$this->getSessionName('gss_orders_enddate')]) {
            $filter->endDate = $this->convertToJavaDate(strtotime($_SESSION[$this->getSessionName('gss_orders_enddate')]));
        }
        
        if (isset($_SESSION[$this->getSessionName('gss_orders_status')]) && $_SESSION[$this->getSessionName('gss_orders_status')]) {
            $filter->extra["orderstatus"] = $_SESSION[$this->getSessionName('gss_orders_status')];
        }
        
        
        
        return $filter;
    }
    
    public function getPageNumber() {
        if (!isset($_SESSION[$this->getSessionName('gss_orders_currentPageNumber')])) {
            return 1;
        }
        
        return $_SESSION[$this->getSessionName('gss_orders_currentPageNumber')];   
    }
    
    public function nextOrderPage() {
        $pageNumber = $this->getPageNumber();
        $pageNumber++;
        $_SESSION[$this->getSessionName('gss_orders_currentPageNumber')] = $pageNumber;
    }
    
    public function prevOrderPage() {
        $pageNumber = $this->getPageNumber();
        $pageNumber--;
        if ($pageNumber < 1) {
            $pageNumber = 1;
        }
        $_SESSION[$this->getSessionName('gss_orders_currentPageNumber')] = $pageNumber;
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
    
    public function getSearchText() {
        return isset($_SESSION[$this->getSessionName('gss_orders_filter')]) ? $_SESSION[$this->getSessionName('gss_orders_filter')] : "";
    }

}

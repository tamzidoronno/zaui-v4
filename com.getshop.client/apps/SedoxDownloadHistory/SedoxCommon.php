<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b;

class SedoxCommon extends \MarketingApplication {
    private $currentProduct = null;
    private $pageSize = 15;
    
    
    public function setSortering() {
        $_SESSION[$this->getAppInstanceId()."_sortby"] = $_POST['data']['sortBy'];
        $_SESSION[$this->getAppInstanceId()."_ascending"] = !$this->isAscending();
    }
    
    public function isAscending() {
        if (!isset($_SESSION[$this->getAppInstanceId()."_ascending"])) {
            return true;
        }
        
        return $_SESSION[$this->getAppInstanceId()."_ascending"];
    }

    public function getSorting() {
        if (isset($_SESSION[$this->getAppInstanceId()."_sortby"])) {
            return $_SESSION[$this->getAppInstanceId()."_sortby"];
        }
        
        return $this->defaultSorting();
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
    
    public function setCurrentProduct($product) {
        $this->currentProduct = $product;
    }
    
    public function getCurrentProduct() {
        return $this->currentProduct;
    }

    public function getTotalPages() {
        return 1;
    }
    
    public function createFilterData() {
        $filterdata = new \core_sedox_FilterData();
        $filterdata->ascending = $this->isAscending();
        $filterdata->filterText = $this->getFilterText();
        $filterdata->pageNumber = $this->getCurrentPage();
        $filterdata->pageSize = $this->pageSize;
        $filterdata->sortBy = $this->getSorting();
        $filterdata->slaveId = $this->getSlaveId();
        return $filterdata;
    }

    public function defaultSorting() {
        return "sedoxorder_date";
    }

    public function preProcess() {
        $this->setSlaveId();
    }
    
    public function setSlaveId() {
        if (isset($_GET['slaveid'])) {
            $_SESSION[$this->getAppInstanceId()."_slaveid"] = $_GET['slaveid'];
        }
    }
    
    public function getSlaveId() {
        if (isset($_SESSION[$this->getAppInstanceId()."_slaveid"])) {
            return $_SESSION[$this->getAppInstanceId()."_slaveid"];
        }
        
        return "";
    }
    
    private function sessionNameFileSelected($productId) {
        return $this->getAppInstanceId()."_fileselected_".$productId;
    }
    
    public function fileSelected() {
        $productId = $_POST['data']['productId'];
        $_SESSION[$this->sessionNameFileSelected($productId)] = $_POST['data']['fileId'];
    }
    
    public function isSelected($productId, $fileId) {
        if (!isset($_SESSION[$this->sessionNameFileSelected($productId)])) {
            return false;
        }
        
        return $_SESSION[$this->sessionNameFileSelected($productId)] == $fileId;
    }
}
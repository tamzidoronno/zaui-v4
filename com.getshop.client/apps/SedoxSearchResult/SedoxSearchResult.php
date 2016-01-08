<?php
namespace ns_366dbe67_0829_47c8_b6ed_47bc3a136338;

class SedoxSearchResult extends \ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b\SedoxCommon implements \Application {
    private $searchResult = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxSearchResult";
    }

    public function preProcess() {
        $this->setSearchSession();
        $this->loadResult();
    }
    
    public function render() {
        $this->includefile("searchresult");
    }

    public function setSearchSession() {
        if (isset($_GET['searchword'])) {
            $_SESSION['sedox_search_value'] = $_GET['searchword'];
        }
    }

    public function getSearchString() {
        if(isset($_SESSION['sedox_search_value'])) {
            return $_SESSION['sedox_search_value'];
        }
        
        return "";
    }
    
    public function loadResult() {
        $searchInput = new \core_sedox_SedoxSearch();
        $searchInput->searchCriteria = $this->getSearchString();
        $searchInput->page = $this->getCurrentPage();
        $this->searchResult = $this->getApi()->getSedoxProductManager()->search($searchInput);
    }
    
    public function getTotalPages() {
        if (!$this->searchResult) {
            return 1;
        }
        
        return $this->searchResult->totalPages;
    }
    
    public function getProducts() {
        return $this->searchResult->products;
    }

}
?>

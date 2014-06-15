<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of SedoxProductSearcher
 *
 * @author ktonder
 */
namespace ns_2ef9eeb0_da05_11e3_9c1a_0800200c9a66;

class SedoxProductSearcher extends \WebshopApplication implements \Application {
    private $test = "";
    
    public function getDescription() {
        return $this->__("Nothing to see");
    }

    public function getName() {
        return $this->__f("Sedox Search Products");
    }

    public function render() {
        $this->includefile("searchtemplate");
//        unset($_SESSION['searchKey']);
    }
    
    public function searchProduct() {
        $_SESSION['searchKey'] = $_POST['data']['searchKey'];
        
    }
    
    public function getSearchResult($key) {
        if (!$key) {
            return array();
        }
        
        $page = 1;
                
        if (isset($_GET['searchppage'])) {
            $page = $_GET['searchppage'];
        }
        $search = new \core_sedox_SedoxSearch();
        $search->searchCriteria = $_SESSION['searchKey'];
        $search->page = $page;
        
        $hrm = $this->getApi()->getSedoxProductManager()->search($search);
        return $hrm;
    }
}

?>

<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of SedoxUserPanel
 *
 * @author ktonder
 */

namespace ns_32b5f680_dd8d_11e3_8b68_0800200c9a66;

class SedoxUserPanel extends \ApplicationBase implements \Application {
    private $creditAccount = null;
    
    public function getDescription() {
        return "An application for Sedox Performance that can be used for displaying userspecific information to the user";
    }

    public function getName() {
        return "Sedox User Panel";
    }

    public function render() {
        $this->includefile("userpanel");
    }
    
    

    /**
     * Gets the latest four credit history entries.
     * 
     * @return null / latest for
     */
    public function getLatestCreditHistory() {
        $creditAccount = $this->getSedoxUserAccount()->creditAccount;
        $history = $creditAccount->history;
        return $history;
    }
    
    public function getOrders() {
        $orders = $this->getSedoxUserAccount()->orders;
        if (count($orders) > 0) {
            $ordersReversed = array_reverse($orders);
            return $ordersReversed;
        } else {
            return null;
        }
    }

    public function getSedoxUserAccount() {
        if (!isset($this->sedoxUser)) {
            $this->sedoxUser = $this->getApi()->getSedoxProductManager()->getSedoxUserAccount();
        }
        return $this->sedoxUser;
    }

    public function getLatestUploadedProducts() {
        $productsConnected = $this->getApi()->getSedoxProductManager()->getProductsFirstUploadedByCurrentUser();
        if (count($productsConnected) == 0) {
            return null;
        }
        return $productsConnected;
    }

    public function getCreditAccountBalance() {
        return $this->getSedoxUserAccount()->creditAccount->balance;
    }

    public function getFileCount() {
        return count($this->getApi()->getSedoxProductManager()->getProductsFirstUploadedByCurrentUser());
    }

    
    public function printPaging($list, $currentPage) {
        echo "<div class='paging'>";
        
            if ($list == null) {
                echo $pages = 1;
            } else {
                $pages = count($list)/10;
                $pages = ceil($pages);
            }

            $prev = $currentPage > 1 ? "<i class='fa fa-arrow-left prev'></i>&nbsp;&nbsp;" : "";
            $next = $currentPage < $pages ? "&nbsp;&nbsp;<i class='fa fa-arrow-right next'></i> " : "";
            echo " $prev $currentPage / $pages $next";
        
        echo '</div>';
    }
    
    private function getSessionVariable($name) {
        if (!isset($_SESSION[$name])) {
            $_SESSION[$name] = 1;
        }
        
        return $_SESSION[$name];
    }
    public function getCurrentFileHistoryPage() {
        return $this->getSessionVariable('sedox_user_panel_current_session');
    }
    
    public function nextFileHistory() {
        $currentPage = $this->getCurrentFileHistoryPage();
        $currentPage++;
        $_SESSION['sedox_user_panel_current_session'] = $currentPage;
    }
    
    public function prevFileHistory() {
        $currentPage = $this->getCurrentFileHistoryPage();
        $currentPage--;
        if ($currentPage < 1) {
            $currentPage = 1;
        }
        $_SESSION['sedox_user_panel_current_session'] = $currentPage;
    }
    
   
    public function nextTransactionHistory() {
        $currentPage = $this->getCurrentTransactionHistoryPage();
        $currentPage++;
        $_SESSION['sedox_user_panel_filehistory_session'] = $currentPage;
    }
    
    public function prevTransactionHistory() {
        $currentPage = $this->getCurrentTransactionHistoryPage();
        $currentPage--;
        if ($currentPage < 1) {
            $currentPage = 1;
        }
        $_SESSION['sedox_user_panel_filehistory_session'] = $currentPage;
    }

    public function getCurrentTransactionHistoryPage() {
        return $this->getSessionVariable("sedox_user_panel_filehistory_session");
    }
    
    public function getCurrentPageForDownloadedFiles() {
        return $this->getSessionVariable("sedox_user_panel_page_filedownloaded");
    }
    
     public function nextDownloadHistory() {
        $currentPage = $this->getCurrentPageForDownloadedFiles();
        $currentPage++;
        $_SESSION['sedox_user_panel_page_filedownloaded'] = $currentPage;
    }
    
    public function prevDownloadHistory() {
        $currentPage = $this->getCurrentPageForDownloadedFiles();
        $currentPage--;
        if ($currentPage < 1) {
            $currentPage = 1;
        }
        $_SESSION['sedox_user_panel_page_filedownloaded'] = $currentPage;
    }
    
    public function getSlaves() {
        $masterId = $this->getSedoxUserAccount()->id;
        return $this->getApi()->getSedoxProductManager()->getSlaves($masterId);
    }
    
    public function getSlave() {
        $slaveId = $_POST['data']['userId'];
        $slaves = $this->getSlaves();
        foreach ($slaves as $slave) {
            if ($slave->id == $slaveId) {
                return $slave;
            }
        }
    }
    
    public function showSlaveHistory() {
        $this->includefile("slaveCreditHistory");
    }
    
    public function showFullCreditHistory() {
	$this->includefile("fullcredithistory");
    }
    
    public function showFullFileHistory() {
	$this->includefile("fullfilehistory");
    }
    
    public function showFullDownloadHistory() {
	$this->includefile("fulldownloadhistory");
    }
}

?>

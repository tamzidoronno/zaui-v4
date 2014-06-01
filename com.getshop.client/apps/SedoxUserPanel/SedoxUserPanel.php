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
        if (count($history) > 0) {
            $reverstedArray = array_reverse($history);
            $fourLatestCreditHistory = array_slice($reverstedArray, 0, 10);
            return $fourLatestCreditHistory;
        } else {
            return null;
        }
    }
    
    public function getLatestOrder() {
        $orders = $this->getSedoxUserAccount()->orders;
        if (count($orders) > 0) {
            $ordersReversed = array_reverse($orders);
            $fourLatestCreditHistory = array_slice($ordersReversed, 0, 10);
            return $fourLatestCreditHistory;
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
        return array_slice(array_reverse($productsConnected), 0, 10);
    }

    public function getCreditAccountBalance() {
        return $this->getSedoxUserAccount()->creditAccount->balance;
    }

    public function getFileCount() {
        return count($this->getApi()->getSedoxProductManager()->getProductsFirstUploadedByCurrentUser());
    }

    public function getLatestDownloadedFiles() {
        $orders = $this->getLatestOrder();
        $retProductes = [];
        foreach ($orders as $order) {
            $retProductes[] = $this->getApi()->getSedoxProductManager()->getProductById($order->productId);
            
        }
//        echo "<pre>";
//        print_r($orders);
//        echo "</pre>";
        return $retProductes;
    }
}

?>

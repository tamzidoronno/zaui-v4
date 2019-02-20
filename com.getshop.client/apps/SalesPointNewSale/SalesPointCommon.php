<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_57db782b_5fe7_478f_956a_ab9eb3575855;

class SalesPointCommon extends \MarketingApplication {
    protected function preRender() {
        $cashPoints = $this->getApi()->getPosManager()->getCashPoints();
        
        if ($cashPoints && count($cashPoints) == 1) {
            $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id'] = $cashPoints[0]->id;
            $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_name'] = $cashPoints[0]->cashPointName;
        }
        
        if ($cashPoints && count($cashPoints) && !$this->getSelectedCashPointId()) {
            $this->includefile("selectCashPoint", "ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale");
            return true;
        }
        
        $this->setViewForUser($cashPoints);
        
        return false; 
    }
    
    public static function sortOrdersByDate($l1, $l2) {
       return strtotime($l2->paymentDate) > strtotime($l1->paymentDate);
    }
   
    public function getSelectedCashPointId() {
        
        if(isset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id'])) {
            return $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id'];
        }
        
        return "";
    }
    
    public function selectCashPoint() {
        $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id'] = $_POST['data']['cashpointid'];
        $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_name'] = $this->getApi()->getPosManager()->getCashPoint($_POST['data']['cashpointid'])->cashPointName;
    }
    
    public function getSelectedCashPointName() {
        if (isset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_name'])) {
            return $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_name'];
        }
        
        return "";
    }
    
    public function disconnectedCashPoint() {
        unset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id']);
    }
    
    public function getSelectedViewId() {
        if(isset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_view_id'])) {
            return $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_view_id'];
        }
        
        return "";
    }
    
    public function selectView() {
        $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_view_id'] = $_POST['data']['viewId'];
        
        $selectedCashPoint = $this->getSelectedCashPointId();
        if ($selectedCashPoint) {
            $this->getApi()->getPosManager()->setView($selectedCashPoint, $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_view_id']);
        }
    }
    
    public function disconnectedView() {
        unset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_view_id']);
        
        $selectedCashPoint = $this->getSelectedCashPointId();
        if ($selectedCashPoint) {
            $this->getApi()->getPosManager()->setView($selectedCashPoint, "");
        }
    }
    
    public function getSelectedKitchenPrinter() {
        $cashPointId = $this->getSelectedCashPointId();
        if ($cashPointId) {
            return $this->getApi()->getPosManager()->getCashPoint($cashPointId)->kitchenPrinterGdsDeviceId;
        }
        
        return "";
    }
    
    public function getSelectedReceiptPrinter() {
        $cashPointId = $this->getSelectedCashPointId();
        
        if ($cashPointId) {
            return $this->getApi()->getPosManager()->getCashPoint($cashPointId)->receiptPrinterGdsDeviceId;
        }
        
        return "";
    }

    public function setViewForUser($cashPoints) {
        if ($cashPoints == null || !is_array($cashPoints)) {
            return;
        }
        
        
        $selectedCashPointId = $this->getSelectedCashPointId();
        $currentSelectedViewId = $this->getSelectedViewId();
        
        $userId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        
        foreach ($cashPoints as $cashPoint) {
            if ($selectedCashPointId == $cashPoint->id) {
                $userPointViewId = $cashPoint->selectedUserView->{$userId};
                if ($currentSelectedViewId != $userPointViewId) {
                    $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_view_id'] = $userPointViewId;
                }
            }
        }
    }

}

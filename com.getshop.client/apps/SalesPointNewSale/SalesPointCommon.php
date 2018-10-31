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
        }
        
        if ($cashPoints && count($cashPoints) && !$this->getSelectedCashPointId()) {
            $this->includefile("selectCashPoint", "ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale");
            return true;
        }
        
        return false;
    }
    
    public function getSelectedCashPointId() {
        
        if(isset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id'])) {
            return $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id'];
        }
        
        return "";
    }
    
    public function selectCashPoint() {
        $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id'] = $_POST['data']['cashpointid'];
    }
    
    public function disconnectedCashPoint() {
        unset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_cashpoint_id']);
    }
}

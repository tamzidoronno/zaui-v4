<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PaymentApplication
 *
 * @author ktonder
 */
class PaymentApplication extends ApplicationBase {
    /* @var $order core_ordermanager_data_Order */
    public $order;
    
    /**
     * @return core_ordermanager_data_Order
     **/
    function getOrder() {
        return $this->order;
    }
    
    public function initPaymentMethod() {
        $this->order->status = 2;
        $this->order->paymentType = get_class($this);
        $this->getApi()->getOrderManager()->saveOrder($this->order);
    }
    
    public function isAvailable() {
        return true;
    }
    
    /**
     * Callback system for payments.
     */
    public function paymentCallback() {
        
    }
}

?>
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
     * @var PaymentMethod[]
     */
    private $paymentMethods = array();
    
    /**
     * @return core_ordermanager_data_Order
     **/
    function getOrder() {
        return $this->order;
    }
    
    public function initPaymentMethod() {
        $this->order->status = 2;
        $this->order->payment->paymentType = get_class($this);
        $this->order->payment->paymentFee = $this->getPaymentFee();
        $this->getApi()->getOrderManager()->saveOrder($this->order);
    }
    
    public function getPaymentFee() {
        return 0;
    }
    
    public function isAvailable() {
        return true;
    }
    
    /**
     * Callback system for payments.
     */
    public function paymentCallback() {
        
    }
    
    public function hasSubProducts() {
        return false;
    }
    
    public function isReadyToPay() {
        return true;
    }
    
    public function doSubProductProccessing() {
        
    }
    
    protected function addPaymentMethod($name, $logo, $id="", $paymentDetails="") {
        $paymentMethod = new PaymentMethod();
        $paymentMethod->setName($name);
        $paymentMethod->setLogo($logo);
        $paymentMethod->setId($id);
        $paymentMethod->setPaymentApplication($this);
        $paymentMethod->setPaymentDetails($paymentDetails);
        $this->paymentMethods[$name] = $paymentMethod;
    }
    
    public function getPaymentMethods($tryRun=true) {
        if (method_exists($this, "addPaymentMethods"))
            $this->addPaymentMethods($tryRun);
        return $this->paymentMethods; 
    }
    
    public function getExtendedPaymentForm(\PaymentMethod $paymentMethod) {
        return "";
    }
}

?>
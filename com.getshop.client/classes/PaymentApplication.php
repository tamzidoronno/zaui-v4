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
    
    private $paymentOptions = null;
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
    
    
    public function isPublicPaymentApp() {
        return true;
    }
    
    public function getSavedCards($userId) {
    }
    
    public function initPaymentMethod() {
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
    
    /**
     * Creates order and process payment
     */
    public function processPayment($address, $managerReference) {
        $order = $this->getApi()->getOrderManager()->createOrder($address);
        $order->createByManager = $managerReference;
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function canChangePaymentMethod($order) {
        if (isset($order->closed) && $order->closed) {
            return false;
        }
        
        return true;
    }
    
    public function canDeleteOrder($order) {
        if (isset($order->closed) && $order->closed) {
            return false;
        }
        
        return true;
    }
    
    public function isAllowedToManuallyMarkAsPaid($order) {
        if (isset($order->closed) && $order->closed) {
            return false;
        }
        
        return true;
    }
    
    public function renderPaymentOption() {
    }
    
    public function canCreateOrderAfterStay() {
        return true;
    }
    
    public function renderPayment($options=false) {
        $this->paymentOptions = $options;
        $classname = get_class($this);
        $arr = explode("\\", $classname);
        $class = $arr[1];
        $paymentid = $this->applicationSettings->id;
        $postMethod = isset($this->paymentOptions->postMethod) ? $this->paymentOptions->postMethod : "doPayment";
        $callback = isset($this->paymentOptions->gscallback) ? "gs_callback='".$this->paymentOptions->gscallback."' synchron='true'" : "";
        
        echo "<div class='$class' gstype='form' method='$postMethod' $callback>";
        
        if (isset($this->paymentOptions->extraArgs)) {
            foreach ($this->paymentOptions->extraArgs as $key => $val) {
                echo "<input type='hidden' value='$val' gsname='$key'/>";
            }
        }
        
        echo "<input type='hidden' value='$paymentid' gsname='paymentid'/>";
        $this->renderPaymentOption();
        echo "</div>";
    }
    
    function getPaymentOptions() {
        return $this->paymentOptions;
    }
    
    function renderPaymentButtons() {
        echo '<div class="shop_button" gstype="submit">'.$this->__f("Create order").'</div>';
        
        if ($this->paymentOptions->canCreateOrderAfterStay && $this->canCreateOrderAfterStay()) {
            echo ' <div class="shop_button">'.$this->__f("Create order after stay").'</div>';
        }
    }
    
    public function doPayment($cart=false) {
        if ($cart) {
            $this->getApi()->getCartManager()->setCart($cart);
        }
        
        $this->order = $this->getApi()->getOrderManager()->createOrder(null);
        $this->initPaymentMethod();
        $this->order->payment->paymentId = $this->getApplicationSettings()->id;
        $this->getApi()->getOrderManager()->saveOrder($this->order);
        return $this->order;
    }

    public function getExtraInformation($order) {
        return "";
    }
}

?>
<?php
namespace ns_f22e70e7_7c31_4c83_a8c1_20ae882853a7;

class OrderSimplePrinter extends \MarketingApplication implements \Application {
    private $orderId;
    
    private $compactView = false;
    public $printHeader = false;
    
    /**
     * @var \core_ordermanager_data_Order
     */
    private $order;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "OrderSimplePrinter";
    }

    public function render() {
        if ($this->compactView) {
            $this->includefile("compactview");
        } else {
            $this->includefile("order");
        }
    }
    
    public function setOrderId($orderId) {
        $this->orderId = $orderId;
    }
    
    /**
     * 
     * @return \core_ordermanager_data_Order
     */
    public function getOrder() {
        if (!$this->order) {
            $this->order = $this->getApi()->getOrderManager()->getOrder($this->orderId);
        }
        
        return $this->order;
    }
    
    public function deleteOrder() {
        $this->setOrderId($_POST['data']['orderid']);
        $order = $this->getOrder();
        $paymentApp = $this->getPaymentApplication();
       
        if ($paymentApp->canDeleteOrder($order)) {
            $order->cart->items = array();
            $this->getApi()->getOrderManager()->saveOrder($order);
        }
    }

    public function getPaymentApplication() {
        $order = $this->getOrder();
        $paymentType = $this->getApi()->getStoreApplicationPool()->getApplication($order->payment->paymentId);
        $paymentInstance = $this->getFactory()->getApplicationPool()->createInstace($paymentType);
        return $paymentInstance;
    }

    public function setCompactView() {
        $this->compactView = true;
    }
    
    public function setPrintHeader() {
        $this->printHeader = true;
    }

    public function formatPaymentType($paymentType) {
        $payment = explode("\\", $paymentType);
        return $payment[1];
    }

}
?>

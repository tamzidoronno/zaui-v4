<?php
namespace ns_486009b1_3748_4ab6_aa1b_95a4d5e2d228;

class DefaultPaymentHandlingAction extends \PaymentApplication implements \Application {
    public $overrideDefault = true;
    public $header = false;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "DefaultPaymentHandlingAction";
    }

    public function render() {
        $this->includefile("common");
        
        if ($this->order->status == 7) {
            $this->includefile("closedoptions");
        } else {
            $this->includefile("openoptions");
        }
    }
    
    public function creditOrder() {
        $this->getApi()->getOrderManager()->creditOrder($_POST['data']['orderid']);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function sendReceipt() {
        $this->getApi()->getInvoiceManager()->sendReceiptToCashRegisterPoint($_POST['data']['deviceid'], $_POST['data']['orderid']);
    }
    
    public function markOrderAsPaid() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $amount = $this->getApi()->getOrderManager()->getTotalAmount($order);
        $date = $this->convertToJavaDate(strtotime($_POST['data']['paymentdate']));
        
        $this->getApi()->getOrderManager()->markAsPaid($order->id, $date, $amount);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function cancelCurrentOrder() {
        $this->getApi()->getOrderManager()->deleteOrder($_POST['data']['orderid']);
    }
}
?>

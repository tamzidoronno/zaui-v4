<?php
namespace ns_8f5d04ca_11d1_4b9d_9642_85aebf774fee;

class Epay extends \PaymentApplication implements \Application {
    public $singleton = true;
    public function getDescription() {
        
    }

    public function getName() {
        return "Epay";
    }
    
    public function paymentCallback() {
        $orderId = $_GET['orderId'];
        $nextPage = $_GET['nextpage'];
        
        $paymentsuccess = $this->getConfigurationSetting("paymentsuccess");
        $paymentfailed = $this->getConfigurationSetting("paymentfailed");
        
        if (isset($_GET['orderId'])) {
            if($_GET['nextpage'] == "payment_success") {
                $order = $this->getApi()->getOrderManager()->getOrder($_GET['orderId']);
                $order->status = 9;
                $order->payment->transactionLog->{time()*1000} = "Payment completed, capturing needed.";
                $this->getApi()->getOrderManager()->saveOrder($order);
                header('Location: ' . "/?page=payment_success");
            } else {
                $order = $this->getApi()->getOrderManager()->getOrder($_GET['orderId']);
                $order->status = 5;
                $order->payment->transactionLog->{time()*1000} = "Payment failed /cancelled by user.";
                $this->getApi()->getOrderManager()->saveOrder($order);
                header('Location: ' . "/?page=payment_failed");
            }
        }
    }

    
    
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("epayconfig");
    }
    
    public function preProcess() {
        $this->includefile("epaywindow");
    }
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "");
    }
    
    public function simplePayment() {
        $this->preProcess();
    }

    public function saveSettings() {
        $this->setConfigurationSetting("merchantid", $_POST['merchantid']);
        $this->setConfigurationSetting("paymentsuccess", $_POST['paymentsuccess']);
        $this->setConfigurationSetting("paymentfailed", $_POST['paymentfailed']);
        $this->setConfigurationSetting("paymentcancelled", $_POST['paymentcancelled']);
        $this->setConfigurationSetting("hmac", $_POST['hmac']);
        $this->setConfigurationSetting("savecard", $_POST['savecard']);
        $this->setConfigurationSetting("testmode", $_POST['testmode']);
    }
    
    
    public function render() {
        
    }
}
?>

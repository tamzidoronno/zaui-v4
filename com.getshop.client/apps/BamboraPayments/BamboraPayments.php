<?php
namespace ns_a92d56c0_04c7_4b8e_a02d_ed79f020bcca;

class BamboraPayments extends \PaymentApplication implements \Application {
    
    public function getDescription() {
        return $this->__("bambora payment services allows you to start charging with visa / mastercard / and other card vendors. its extremly easy to use. prices for using this application is provided by dibs, and you will be contacted by dibs when adding this application.");
    }

    public function getName() {
        return $this->__w("Cards / Bambora");
    }
    public function paymentCallback() {
        header('Location: ' . "/?page=".$_GET['nextpage']);
    }
    
    public function testCheckCapture() {
        $this->getApi()->getBamboraManager()->checkForOrdersToCapture();
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "");
    }
    
    
    public function isPublicPaymentApp() {
        return false;
    }
    
    public function simplePayment() {
        $orderId = $this->getOrder()->id;
        $url = $this->getApi()->getBamboraManager()->getCheckoutUrl($orderId);
        echo $this->__w("You are being transferred to the payment window, please wait.");
        $order = $this->order;
        if($url) {
            echo "<script>";
            echo "window.location.href='$url';";
            echo "</script>";
            $order->payment->transactionLog->{time()*1000} = "Transferred to payment window : $url";
        } else {
            $order->payment->transactionLog->{time()*1000} = "Failed transferring to payment window";
        }
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("configuration");
        
    }

    public function saveSettings() {
        $this->setConfigurationSetting("merchant_id", $_POST['merchant_id']);
        $this->setConfigurationSetting("access_token", $_POST['access_token']);
        $this->setConfigurationSetting("secret_token", $_POST['secret_token']);
        $this->setConfigurationSetting("callback_id", $_POST['callback_id']);
        $this->setConfigurationSetting("md5", $_POST['md5']);
    }
    
    public function render() {
        
    }
}
?>

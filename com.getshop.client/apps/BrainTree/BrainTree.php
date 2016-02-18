<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_542e6a1e_9927_495c_9b6d_bb52af4ea9be;

class BrainTree extends \PaymentApplication implements \Application {
    
    public function getDescription() {
        return $this->__w("BrainTree is a international payment method offering VISA / MasterCard / PayPal etc. Very simple to use and gets you started with visa payment immediatly");
    }

    public function getName() {
        return "BrainTree";
    }
    
    public function simplePayment() {
        $this->includefile("paymentformdirect");
    }
    
    public function render() {
        if (isset($_GET['gsPaymentOrderId'])) {
            $_SESSION['gsPaymentOrderId'] = $_GET['gsPaymentOrderId'];
        };
        
        if (isset($_POST['payment_method_nonce'])) {
            if (!isset($_SESSION['gsPaymentOrderId'])) {
                echo $this->__f("You have used to long time to complete the checkout, please go back and try again");
                return;
            } else {
                $result = $this->doPayment($_SESSION['gsPaymentOrderId']);
                if (!$result) {
                    echo "<div class='payment_failed'>".$this->__w("Was not able to process your payment, check your card details and try again.")."</div>";
                } else {
                    unset($_SESSION['gsPaymentOrderId']);
                    echo "<script>document.location = '/index.php?page=paymentsuccess'</script>";
                    die();
                }
            }
        }
        
        $this->includefile("paymentoverview");
        $this->includefile("paymentformdirect");
    }
    
    public function addPaymentMethods($fromCheckout=false) {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("BrainTree", "/showApplicationImages.php?appNamespace=$namespace&image=BrainTree.png", "braintree", "");
    }
    
    private function doPayment($orderId = false) {
        $useOrderId = $orderId ? $orderId : $this->getOrder()->id;
        return $this->getApi()->getBrainTreeManager()->pay($_POST['payment_method_nonce'], $useOrderId);
    }
    
    public function preProcessPayment() {        
        if (isset($_POST['payment_method_nonce'])) {
            return $this->doPayment();
        }
        return true;
    }
    
    public function includeExtraJavascript() {
        echo '<script src="https://js.braintreegateway.com/v2/braintree.js"></script>';
    }    
    
    public function getDetails() {
        ob_start();
        $this->includefile("paymentform");
        $content = ob_get_contents();
        ob_end_clean();
        return $content;
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
    
    public function hasSubProducts() {
        return true;
    }
    
    public function preProcess() {
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("merchantid", $_POST['merchantid']);
        $this->setConfigurationSetting("publickey", $_POST['publickey']);
        if (strpos($_POST['privatekey'], '****') === FALSE) {
            $this->setConfigurationSetting("privatekey", $_POST['privatekey'], true);
        }
        
        $this->setConfigurationSetting("isSandBox", $_POST['isSandBox']);
        
        $this->setConfigurationSetting("sandbox_merchantid", $_POST['sandbox_merchantid']);
        $this->setConfigurationSetting("sandbox_publickey", $_POST['sandbox_publickey']);
        
        if (strpos($_POST['sandbox_privatekey'], '****') === FALSE) {
            $this->setConfigurationSetting("sandbox_privatekey", $_POST['sandbox_privatekey'], true);
        }
    }
    
   
}
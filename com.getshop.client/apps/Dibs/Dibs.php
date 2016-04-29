<?php
namespace ns_d02f8b7a_7395_455d_b754_888d7d701db8;

class Dibs extends \PaymentApplication implements \Application {
    public $singleton = true;
    
    public function getDescription() {
        return $this->__("dibs payment services allows you to start charging with visa / mastercard / and other card vendors. its extremly easy to use. prices for using this application is provided by dibs, and you will be contacted by dibs when adding this application.");
    }

    public function getName() {
        return "DIBS";
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "/showApplicationImages.php?appNamespace=$namespace&image=Dibs.png", "dibs");
    }

    public function postProcess() {
        
    }
    
    public function getSavedCards($userId) {
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $cards = $user->savedCards;
        $toReturn = array();
        foreach($cards as $card) {
            if(!stristr($card->savedByVendor, "dibs")) {
                continue;
            }
            $toReturn[] = $card;
        }
        return $toReturn;
    }

    
    public function simplePayment() {
        $this->preProcess();
    }
    
    public function paymentCallback() {
        $orderId = $_GET['orderId'];
        $nextPage = $_GET['nextpage'];
        
        $paymentsuccess = $this->getConfigurationSetting("paymentsuccess");
        $paymentfailed = $this->getConfigurationSetting("paymentfailed");
        
        if (isset($_GET['orderId'])) {
            if($nextPage == "payment_success") {
                $order = $this->getApi()->getOrderManager()->getOrder($_GET['orderId']);
                $order->status = 7;
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

    public function preProcess() {
        
        $merchid = $this->getConfigurationSetting("merchantid");
        $currency = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::fetchCurrencyCode();
        $orderId = $this->getOrder()->incrementOrderId;
        $amount = $this->getApi()->getCartManager()->calculateTotalCost($this->order->cart)*100;
        if(isset($this->order->shipping) && isset($this->order->shipping->cost)) {
            $amount += ($this->order->shipping && $this->order->shipping->cost) ? $this->order->shipping->cost : 0;
        }
        
        $settings = $this->getFactory()->getSettings();
        $language = $this->getFactory()->getSelectedTranslation();
        $store = $this->getFactory()->getStore();
        
        $key = $merchid;
        if($this->getApi()->getStoreManager()->isProductMode()) {
            $key .= "-prod";
        } else {
            $key .= "-debug";
        }
        
        $callBack = "http://pullserver_".$key."_".$store->id.".nettmannen.no";
        $redirect_url = "http://" . $_SERVER["HTTP_HOST"] . "/callback.php?app=" . $this->applicationSettings->id. "&orderId=" . $this->order->id . "&nextpage=";
        
        echo '<form method="post" id="dibsform" action="https://sat1.dibspayment.com/dibspaymentwindow/entrypoint">
            <input value="' . $merchid . '" name="merchant" type="hidden" />
            <input value="' . $currency . '" name="currency" type="hidden" />
            <input value="' . $orderId . '" name="orderId" type="hidden" />
            <input value="' . $amount . '" name="amount" type="hidden" />
            <input value="' . $language .  '" name="language" type="hidden" />
            <input value="' . $redirect_url . 'payment_success" name="acceptReturnUrl" type="hidden" />
            <input value="' . $redirect_url . 'payment_failed" name="cancelReturnUrl" type="hidden" />
            <input value="' . $callBack . '" name="callbackUrl" type="hidden" />';
            if($this->saveCard()) {
                echo '<INPUT TYPE="hidden" NAME="createTicket" VALUE="1">';
            }
        if($this->isTestMode()) {
                echo '<input type="hidden" name="test" value="1"/>';
                echo "This is in test mode...";
            }
        echo '</form>';

        echo "<script>";
        echo "$('#dibsform').submit();";
        echo "</script>";
        
        /* @var $order core_ordermanager_data_Order */
        $order = $this->order;
        $order->payment->transactionLog->{time()*1000} = "Transferred to payment window";
        $this->getApi()->getOrderManager()->saveOrder($order);
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("dibsconfiguration");
    }

    public function render() {
        
    }
    
    public function isAvailable() {
        return true;
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
    
    public function saveCard() {
        return $this->getConfigurationSetting("savecard") == "true";
    }

    public function isTestMode() {
        if(!$this->getApi()->getStoreManager()->isProductMode()) {
            return true;
        }
        
        return $this->getConfigurationSetting("testmode") == "true";
    }

}

?>

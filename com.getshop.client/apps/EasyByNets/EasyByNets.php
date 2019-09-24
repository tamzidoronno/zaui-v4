<?php
namespace ns_be004408_e969_4dba_9b23_5922b8f1d7e2;

class EasyByNets extends \PaymentApplication implements \Application {
 public function getDescription() {
        return "Easy is the next generation payment gateway from nets, used for the scandinavian marked.";
    }

    private $address = "http://api.dibspayment.eu/v1/";
    
    public function getName() {
        return "Easy by nets";
    }

    public function render() {
        
    }
        
    public function isAvailable() {
        return true;
    }
    
    public function getStarted() {
        
    }
        
    public function hasPaymentLink() {
        return true;
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "/showApplicationImages.php?appNamespace=$namespace&image=Dibs.png", "dibs");
    }
    
    public function getSuccessPage() {
        $redirectUrl = $this->redirectUrl();
        return $redirectUrl . "payment_success";
    }
        
    public function getFailedPage() {
        $redirectUrl = $this->redirectUrl();
        return $redirectUrl . "payment_failed";
    }
        
    public function getCallbackUrl() {
        $redirectUrl = $this->redirectUrl();
        return $redirectUrl;
    }
    
    
    public function getIcon() {
        return "card.png";
    }
    
    public function preProcess() {
        $this->createPay();
    }
    
    
    public function postProcess() {
        
    }
    
    public function simplePayment() {
        $this->preProcess();
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("apikey", $_POST['data']['apikey']);
    }
    
    public function renderConfig() {
        $this->includeFile("config");
    }    
    
    public function getColor() {
        return "blue";
    }
    
    public function renderPaymentOption() {
        echo "JA";
    }
    
    public function hasPaymentProcess() {
         return ($this->order != null && $this->order->status != 7);
    }

    public function setHeaders($ch, $jsonData) {
        //Tell cURL that we want to send a POST request.
        curl_setopt($ch, CURLOPT_POST, 1);

        if($jsonData) {
            //Attach our encoded JSON string to the POST fields.
            curl_setopt($ch, CURLOPT_POSTFIELDS, $jsonData);
        }

        //Set the content type to application/json
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Accept: application/json','User-Agent: GetShop', 'cookie: WBS=gwapzFWhcm9LjfGhtQ0uDc0JJD4PRzB8')); 
    }

    public function redirectUrl() {
        $redirect_url = "https://" . $_SERVER["HTTP_HOST"] . "/callback.php?app=" . $this->applicationSettings->id. "&orderId=" . $this->order->id . "&nextpage=";
        return $redirect_url;
    }

    public function getApiKey() {
        return $this->getConfigurationSetting("apikey");
    }

    public function totalAmount() {
        return (int)($this->getApi()->getOrderManager()->getTotalAmount($this->order) * 100);
    }

    public function getCurrency() {
        return $this->getApi()->getStoreManager()->getSelectedCurrency();
    }

    public function createPay() {
        $datastring = $this->getDatastring();
        $addr = $this->address.'payments/';
        $ch = curl_init($addr);

        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
        curl_setopt($ch, CURLOPT_POSTFIELDS, $datastring);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);        
        
        curl_setopt($ch, CURLOPT_VERBOSE, 1);       
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json', 'Accept: application/json', 'Authorization: 00000000000000000000000009000000'));
        $result = curl_exec($ch);
        if(!$result) {
            echo "failed to excute: " . $addr;
            printf("cUrl error (#%d): %s<br>\n", curl_errno($ch), htmlspecialchars(curl_error($ch)));
        } else {
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "<br>";
            echo "RESULT " . $this->address.'payments' . " ". $result;
        }
    }

    public function getDatastring() {
        $object = new \stdClass();

        $order = new \stdClass();
        $order->amount = $this->totalAmount();
        $order->currency = $this->getCurrency();
        $order->reference = $this->order->incrementOrderId;
        
        $object->order = $order;
        
        $checkout = new \stdClass();
        $checkout->charge = true;
        $checkout->publicDevice = true;
        $checkout->integrationType = "HostedPaymentPage";
        $checkout->returnUrl = $this->getSuccessPage();
        $checkout->termsUrl = $this->getTermsLink();
        $object->checkout = $checkout;
        
        return json_encode($object);
    }

    public function getTermsLink() {
        return "https://" . $_SERVER["HTTP_HOST"] . "/scripts/loadContractPdf.php?readable=true&engine=default";
    }

}
?>

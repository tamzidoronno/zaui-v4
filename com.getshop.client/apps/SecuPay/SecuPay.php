<?php
namespace ns_3c41b0d9_e8e5_45d5_8054_2536159554f0;

class SecuPay extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Secupay is a german payment gateway widely used.";
    }

    private $address = "https://api-testing.secupay-ag.de";
    
    public function getName() {
        return "SecuPay";
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
         $url = $this->address . '/payment/init';
 
         $types = $this->getTypes();
        
        //Initiate cURL.
        $ch = curl_init($url);

        //The JSON data.
        $jsonData = array(
            'apikey' => $this->getApiKey(),
            'payment_type' => $types,
            'amount' => $this->totalAmount(),
            'currency' => $this->getCurrency(),
            'url_success' => $this->getSuccessPage(),
            'url_failure' => $this->getFailedPage(),
            'url_push' => $this->getCallbackUrl(),
            'language' => $this->getFactory()->getSelectedLanguage()
        );
        echo "<pre>";
        print_r($jsonData);
        echo "</pre>";
        
        //Encode the array into JSON.
        $jsonDataEncoded = json_encode($jsonData);
        
        $cg = $this->setHeaders($ch, $jsonDataEncoded);
        
        //Execute the request
        $result = curl_exec($ch);
    }
    
    public function getTypes() {
        $url = $this->address . '/payment/gettypes';
        $ch = curl_init($url);

        $jsonData = array(
            'apikey' => $this->getApiKey()
        );
        
        $cg = $this->setHeaders($ch, json_encode($jsonData));
        
        //Execute the request
        $result = curl_exec($ch);
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

}
?>

<?php
namespace ns_3c41b0d9_e8e5_45d5_8054_2536159554f0;

class SecuPay extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Secupay is a german payment gateway widely used.";
    }

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
    
        
    public function getIcon() {
        return "card.png";
    }
    
    public function preProcess() {
        $url = 'http://example.com/api/JSON/create';
 
        //Initiate cURL.
        $ch = curl_init($url);

        //The JSON data.
        $jsonData = array(
            'apikey' => 'MyUsername',
            'payment_type' => 'MyPassword',
            'amount' => 'MyPassword',
            'currency' => 'MyPassword',
            'url_success' => 'MyPassword',
            'url_failure' => 'MyPassword'
        );

        //Encode the array into JSON.
        $jsonDataEncoded = json_encode($jsonData);

        //Tell cURL that we want to send a POST request.
        curl_setopt($ch, CURLOPT_POST, 1);

        //Attach our encoded JSON string to the POST fields.
        curl_setopt($ch, CURLOPT_POSTFIELDS, $jsonDataEncoded);

        //Set the content type to application/json
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json')); 

        //Execute the request
        $result = curl_exec($ch);
    }
    
    public function postProcess() {
        
    }
    
    public function simplePayment() {
        $this->preProcess();
    }
    
    public function saveSettings() {
        $this->getConfigurationSetting("key", $_POST['data']['key']);
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

}
?>

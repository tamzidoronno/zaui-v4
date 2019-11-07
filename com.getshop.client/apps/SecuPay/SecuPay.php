<?php
namespace ns_3c41b0d9_e8e5_45d5_8054_2536159554f0;

class SecuPay extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Secupay is a german payment gateway widely used.";
    }

    private $address = "https://api.secupay.ag";
    
    public function getName() {
        return "SecuPay";
    }

    public function render() {
        if ($this->getCurrentOrder()->status != 7) {
            $app = $this->getApi()->getGetShopApplicationPool()->get("d96f955a-0c21-4b1c-97dc-295008ae6e5a");
            $appInstance = $this->getFactory()->getApplicationPool()->createInstace($app);
            $appInstance->order = $this->order;
            $appInstance->renderStandAlone();
        }

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
        return $redirectUrl . "payment_success&success=1";
    }
        
    public function getFailedPage() {
        $redirectUrl = $this->redirectUrl();
        return $redirectUrl . "payment_failed&success=0";
    }
        
    public function getCallbackUrl() {
        $redirectUrl = $this->redirectUrl();
        return $redirectUrl . "&secretid=" . $this->order->secretId;
    }
    
    
    public function getIcon() {
        return "card.png";
    }
    
    public function preProcess() {
         $url = $this->address . '/payment/init';
         
         if(!isset($_GET['paymentmethod'])) {
             
             if(isset($_GET['incid'])) {
                 $order = $this->getApi()->getOrderManager()->getOrderByincrementOrderIdAndPassword($_GET['incid'], "fdsafd4e3453ngdgdf");
                 $_GET['payorder'] = $order->id;
             }
             
             $types = $this->getTypes();
             echo "<div style='text-align:center;'>";
             echo "<h1>Please select a payment type</h1>";
             
             $txts = array();
             $txts['debit'] = "Debit / Lastschrifteinzug";
             $txts['creditcard'] = "Credit card / Kreditkarte";
             $txts['sofort'] = "Sofort / Sofortüberweisung";
             
             foreach($types as $type) {
                 if($type == "invoice") {
                     continue;
                 }
                 
                 $txt = $txts[$type];
                 
                 echo "<a href='/?changeGetShopModule=cms&page=cart&payorder=".$_GET['payorder']."&paymentmethod=$type'>";
                 echo "<div style='text-transform:uppercase; margin: 10px; cursor:pointer;' class='selectpaymentmethod'>";
                 echo $txt;
                 echo "</div>";
                 echo "</a>";
             }
             echo "</div>";
             echo "<br><br>";
             echo "<br><br>";
             echo "<br><br>";
             echo "<br><br>";
             echo "<br><br>";
             echo "<br><br>";
             echo "<br><br>";
             
             echo "<style>";
             echo ".selectpaymentmethod { border: solid 1px #bbb; display:inline-block; width: 300px;  background-color:#a2bef3; }";
             echo "</style>";
             
             return;
         }
        
        //Initiate cURL.
        $ch = curl_init($url);
        
        $type = $_GET['paymentmethod'];
        //The JSON data.
        $jsonData = array(
            'apikey' => $this->getApiKey(),
            'payment_type' => $type,
            'amount' => $this->totalAmount(),
            'currency' => $this->getCurrency(),
            'url_success' => $this->getSuccessPage(),
            "payment_action" => "sale",
            "apiversion" => "2.11",
            "firstname" => "",
            "lastname" => "",
            'url_failure' => $this->getFailedPage(),
            'url_push' => $this->getCallbackUrl(),
            "order_id" => $this->order->incrementOrderId,
            'language' => $this->getFactory()->getSelectedLanguage()
        );
        if(!$this->isProdMode()) {
            $jsonData['demo'] = "1";
        }
//        print_r($jsonData);
//        exit(0);
        $cg = $this->setHeaders($ch, $jsonData);
        
        //Execute the request
        $result = curl_exec($ch);
        $result = json_decode($result,true);

        if($result['status'] != "ok") {
            print_r($result['errors']);
            echo $result['errors']['message'];
            exit(0);
        } else {
            $order = $this->order;
            $order->payment->transactionLog->{time()*1000} = "Transferred to payment window";
            $this->getApi()->getOrderManager()->saveOrder($order);
            
            $url = $result['data']['iframe_url'];
            echo "<script>";
            echo "window.location.href='$url';";
            echo "</script>";
        }
        
    }
    
    
    public function getTypes() {
        $url = $this->address . '/payment/gettypes';
        $ch = curl_init($url);

        $jsonData = array(
            'apikey' => $this->getApiKey()
        );
        
        $this->setHeaders($ch, $jsonData);
        
        //Execute the request
        $result = curl_exec($ch);
        $result = json_decode($result, true);
        
        if($result['status'] != "ok") {
            print_r($result['errors']);
            exit(0);
        }
        
        return $result['data'];
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
        $this->includefile("paymentoption");
    }
    
    public function hasPaymentProcess() {
         return ($this->order != null && $this->order->status != 7);
    }

    public function setHeaders($ch, $jsonData) {
        //Tell cURL that we want to send a POST request.
        
        $test = array();
        $test['data'] = $jsonData;

        
        curl_setopt($ch, CURLOPT_POST, 1);

        if($jsonData) {
            //Attach our encoded JSON string to the POST fields.
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($test));
        }
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_VERBOSE, 0);       

        //Set the content type to application/json
        curl_setopt($ch, CURLOPT_HTTPHEADER, array("Content-Type: application/json; charset=utf-8;", "Accept: application/json", "User-Agent: GetShop")); 
    }

    public function redirectUrl() {
        $protocol = "https";
        if(!$this->isProdMode()) {
            $protocol = "http";
        }
        $redirect_url = $protocol. "://" . $_SERVER["HTTP_HOST"] . "/callback.php?app=" . $this->applicationSettings->id. "&orderId=" . $this->order->id . "&nextpage=";
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
    
    public function paymentCallback() {
        
        $methods = "GET:" . json_encode($_GET);
        $methods .= "POST:" . json_encode($_POST);
        $methods .= "BODY:" . json_encode(file_get_contents('php://input'));
        $methods .= "Referer: " . $_SERVER['HTTP_REFERER'];
        
//        file_put_contents("secupay.txt", $methods);
        
        if(isset($_GET['nextpage']) && $_GET['nextpage']) {
            //Redirect from payment window
            if($_GET['success'] == "1") {
                $this->getApi()->getOrderManager()->changeOrderStatusWithPassword($_GET['orderId'], 9, "gfdsabdf034534BHdgfsdgfs#!");
            }
            header('location:' . "/?page=" . $_GET['nextpage']);
        } else {
            //Callback from secupay.
            $ref = $_SERVER['HTTP_REFERER'];
            if (strpos($ref, 'api.secupay.ag') !== FALSE){
                echo" Der Push ist von $ref";
            }
            
            
            if($_POST['status_id'] == "11" || $_POST['status_id'] == 11 || $_POST['status_id'] == "6" || $_POST['status_id'] == 6) {
                $orderId = $_GET['orderId'];
                $order = $this->getApi()->getOrderManager()->getOrderWithIdAndPassword($orderId, "gfdsg9o3454835nbsfdg");
                
                $secret = $_GET['secretid'];
                if($order->secretId == $secret) {
                    $this->getApi()->getOrderManager()->markAsPaidWithPassword($order->id, $this->convertToJavaDate(time()), $this->getApi()->getOrderManager()->getTotalAmount($order), "fdsvb4354345345");
                } else {
                    $this->getApi()->getOrderManager()->logTransactionEntry($order->id, "Security check failed, secret id is not correct..");
                }
            }
            echo "ack=Approved";
        }
    }

    public function isProdMode() {
//        return true;
        return $this->getApi()->getStoreManager()->isProductMode();
    }

}
?>

<?php
namespace ns_be004408_e969_4dba_9b23_5922b8f1d7e2;

class EasyByNets extends \PaymentApplication implements \Application {
 public function getDescription() {
        return "Easy is the next generation payment gateway from nets, used for the scandinavian marked.";
    }

    private $address = "https://api.dibspayment.eu/v1/";
    
    public function getName() {
        return "Easy by nets";
    }

    public function printButton() {
        echo "Easy by nets<div style='margin-top: 5px; font-size: 12px;'>Visa / mastercard / vipps</div>";
    }

    public function render() {
        if ($this->getCurrentOrder()->status != 7) {
            $this->renderOnlinePaymentMethod();
        } else {
            $this->renderDefault();
        }
    }
    
    public function hasPaymentProcess() {
         return ($this->order != null && $this->order->status != 7);
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
        return $redirectUrl . "payment_success&status=success";
    }
        
    public function getFailedPage() {
        $redirectUrl = $this->redirectUrl();
        return $redirectUrl . "payment_failed&status=failed";
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
    
    public function renderPaymentOption() {
        $this->includefile("paymentoption");
    }
    
    public function renderConfig() {
        $this->includeFile("config");
    }    
    
    public function getColor() {
        return "blue";
    }
    
    public function paymentCallback() {
        $postdata = json_encode($_POST);
        $getdata = json_encode($_GET);
        $headers = getallheaders();
        
        if(isset($_GET['webhookcompletion'])) {
            $orderId = $_GET['orderId'];
            $auth = $headers['Authorization'];

            
            $order = $this->getApi()->getOrderManager()->getOrderWithIdAndPassword($orderId, "gfdsg9o3454835nbsfdg");
            $orderSecret = str_replace("-","", $order->secretId);

            if($orderSecret == $auth) {
                $this->getApi()->getOrderManager()->markAsPaidWithPassword($order->id, $this->convertToJavaDate(time()), $this->getApi()->getOrderManager()->getTotalAmount($order), "fdsvb4354345345");
            } else {
                $this->getApi()->getOrderManager()->changeOrderStatusWithPassword($_GET['orderId'], 9, "gfdsabdf034534BHdgfsdgfs#!");
                $this->getApi()->getOrderManager()->logTransactionEntry($_GET['orderId'], "Order has been marked as status 9 from a webhook, POST" . $postdata . " GET: " . $getdata . " Headers: " . json_encode($headers));
            }
            
            echo "OK";
            return;
        }
        
        if(isset($_GET['status']) && $_GET['status'] == "success") {
            $this->getApi()->getOrderManager()->changeOrderStatusWithPassword($_GET['orderId'], 9, "gfdsabdf034534BHdgfsdgfs#!");
            $this->getApi()->getOrderManager()->logTransactionEntry($_GET['orderId'], "Order has been marked as status 9 from a web request, POST" . $postdata . " GET: " . $getdata . " Headers: " . json_encode($headers));
        } else {
            $this->getApi()->getOrderManager()->changeOrderStatusWithPassword($_GET['orderId'], 3, "gfdsabdf034534BHdgfsdgfs#!");
        }
        if($_GET['nextpage'] == "payment_success" || $_GET['nextpage'] == "payment_failed") {
            header('location: /?page=' . $_GET['nextpage']);
        } else {
            header('location:' . $_GET['nextpage']);
        }
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
        if(!$this->isProductMode()) {
            $redirect_url = str_replace(".local.", ".mdev.", $redirect_url);
        }
        return $redirect_url;
    }

    public function getApiKey() {
        $key = $this->getConfigurationSetting("apikey");
        $key = str_replace("test-secret-key-", "", $key);
        $key = str_replace("live-secret-key-", "", $key);
        $key = str_replace("live-checkout-key-", "", $key);
        $key = str_replace("test-checkout-key-", "", $key);
        
        
        if(!$key) {
            echo "No secret set, please find it from the easy admin panel and add it to the pms settings area.";
        }
        
        if(!$this->getApi()->getStoreManager()->isProductMode()) {
            $key = "53146f5438544bafb4c360e164c50dce";
        }
        
        return $key;
    }

    public function totalAmount() {
        return (int)($this->getApi()->getOrderManager()->getTotalAmount($this->order) * 100);
    }

    public function getCurrency() {
        return $this->getApi()->getStoreManager()->getSelectedCurrency();
    }

    public function createPay() {
        $datastring = $this->getDatastring();
        $addr = $this->getApiEndpointAdress().'payments';
        $ch = curl_init($addr);
        
        /* @var $order \core_ordermanager_data_Order */
        $order = $this->order;
        $order->payment->transactionLog->{time()*1000} = "Transferred to payment window";
        $this->getApi()->getOrderManager()->saveOrder($order);

        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
        curl_setopt($ch, CURLOPT_POSTFIELDS, $datastring);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json', 'Accept: application/json', 'Authorization: ' . $this->getApiKey()));
        $result = curl_exec($ch);
        
        $total = $this->getApi()->getOrderManager()->getTotalAmount($order);
        if($total <= 1) {
            echo "The amount for the order is too low.";
            return;
        }
        
        if(!$result) {
            echo "failed to excute: " . $addr;
            printf("cUrl error (#%d): %s<br>\n", curl_errno($ch), htmlspecialchars(curl_error($ch)));
        } else {
            $result = json_decode($result);
            $address = $result->hostedPaymentPageUrl;
            if($result->hostedPaymentPageUrl) {
                echo "<div style='text-align:center; font-size: 30px;'>";
                echo "<i class='fa fa-spin fa-spinner'></i>";
                echo "</div>";
                echo "<script>";
                echo "window.location.href='".$address."';";
                echo "</script>";
            } else {
                print_r($result);
                $msg = json_encode($result) . "<br>Datastring:"  . json_encode($datastring);
                $this->getApi()->getMessageManager()->sendErrorNotify($msg);
            }
        }
    }

    public function getDatastring() {
        $object = new \stdClass();

        $order = new \stdClass();
        $order->amount = $this->totalAmount();
        $order->currency = $this->getCurrency();
        $order->reference = $this->order->incrementOrderId;
        
        if(!$order->currency) {
            $order->currency = "NOK";
        }
        
        $items = array();
        $itemobject = new \stdClass();
        $itemobject->name = "Payment for order: " . $this->order->incrementOrderId;
        $itemobject->quantity = 1;
        $itemobject->reference = $this->order->incrementOrderId;
        $itemobject->unit = "pcs";
        $itemobject->unitPrice = $this->totalAmount();
        $itemobject->taxRate = 0.0;
        $itemobject->taxAmount = $this->totalAmount();
        $itemobject->grossTotalAmount = $this->totalAmount();
        $itemobject->netTotalAmount = $this->totalAmount();
        $items[] = $itemobject;
        
        $order->items = $items;
        
        $object->order = $order;
        
        $checkout = new \stdClass();
        $checkout->charge = true;
        $checkout->publicDevice = true;
        $checkout->integrationType = "HostedPaymentPage";
        $checkout->returnUrl = $this->getSuccessPage();
        $checkout->termsUrl = $this->getTermsLink();
        $checkout->merchantHandlesConsumerData = true;
        $object->checkout = $checkout;
        
        
        $notifications = new \stdClass();
        $webhooks = array();
        $webhook = new \stdClass();
        $webhook->eventName = "payment.checkout.completed";
        $webhook->url = $this->getCallbackUrl();
        $webhook->authorization = str_replace("-","", $this->order->secretId);
        $webhooks[] = $webhook;
        $notifications->webhooks = $webhooks;
        
        $webhook->url = str_replace(".3.0.mdev.", ".", $webhook->url) . "&webhookcompletion=true";
        
        $object->notifications = $notifications;
        
        return json_encode($object);
    }

    public function getTermsLink() {
        return "https://" . $_SERVER["HTTP_HOST"] . "/scripts/loadContractPdf.php?readable=true&engine=default";
    }

    public function isProductMode() {
        return $this->getApi()->getStoreManager()->isProductMode();
    }

    public function getApiEndpointAdress() {
        if(!$this->isProductMode()) {
            return "https://test.api.dibspayment.eu/v1/";
        }
        return $this->address;
    }

}
?>

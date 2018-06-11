<?php

namespace ns_def1e922_972f_4557_a315_a751a9b9eff1;

class Netaxept extends \PaymentApplication implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $transactionId;
    var $order;

    function __construct() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "/showApplicationImages.php?appNamespace=$namespace&image=Netaxept.png", "Nets");
    }

    public function getDescription() {
        return "NexAxept is a online payment processor for handling online payments. Whenever a customer pay with this application they are transferred to the netaxept payment window to handle visa / mastercard / bankaxept";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function hasPaymentLink() {
        return true;
    }

    public function getName() {
        return "Netaxept";
    }

    public function postProcess() {
        
    }
    
    public function simplePayment() {
        $this->preProcess();
    }
    
    public function getWsdl() {
        $wsdl = "https://epayment.nets.eu/Netaxept.svc?wsdl";
        if ($this->getConfigurationSetting("debugmode") != "false" || !$this->getFactory()->productionMode) {
            $wsdl = "https://test.epayment.nets.eu/Netaxept.svc?wsdl";
        }
        return $wsdl;
    }

    public function getMerchantId() {
        $id = $this->getConfigurationSetting("merchantid");
        if (!$id) {
            echo "Merchant id is missing";
        }
        
        if(!$this->getFactory()->productionMode) {
            $id = "12001627";
        }
        
        return $id;
    }

    public function getToken() {
        $token = $this->getConfigurationSetting("token");
        
        if (!$token) {
            echo "Token is missing";
        }
        
        if(!$this->getFactory()->productionMode) {
            $token = "3n+Z/6D";
        }
        
        return $token;
    }

    public function getInputParameters() {
####  PARAMETERS IN REGISTER REQUEST  ####
        $AvtaleGiro = null; // Optional parameter
        $CardInfo = null; // Optional parameter
        $Customer = null; // Optional parameter
        $description = null; // Optional parameter
        $DnBNorDirectPayment = null; // Optional parameter
        $MicroPayment = null; // Optional parameter
        $serviceType = "M";  // Optional parameter
        $Recurring = null; // Optional parameter
        $transactionId = null; // Optional parameter
        $transactionReconRef = null; // Optional parameter

        $Environment = new Environment(null, null, 'PHP5');

        ####  START REGISTER REQUEST  ####
        $RegisterRequest = new RegisterRequest();
        $RegisterRequest->AvtaleGiro = null; // Optional parameter
        $RegisterRequest->CardInfo = null; // Optional parameter
        $RegisterRequest->Customer = null;  // Optional parameter
        $RegisterRequest->Description = null; // Optional parameter
        $RegisterRequest->DnBNorDirectPayment = null; // Optional parameter
        $RegisterRequest->Environment = $this->createEnvironment();
        $RegisterRequest->MicroPayment = null; // Optional parameter
        $RegisterRequest->Order = $this->createOrder();
        $RegisterRequest->Recurring = null; // Optional parameter
        $RegisterRequest->ServiceType = null;
        $RegisterRequest->Terminal = $this->createTerminal();
        $RegisterRequest->TransactionId = $this->order->incrementOrderId . "___" . uniqid(); // Optional parameter
        $RegisterRequest->TransactionReconRef = null; // Optional parameter
        ####  ARRAY WITH REGISTER PARAMETERS  ####
        $InputParametersOfRegister = array
            (
            "token" => $this->getToken(),
            "merchantId" => $this->getMerchantId(),
            "request" => $RegisterRequest
        );
        return $InputParametersOfRegister;
    }

    public function registerCall() {
        try {
            if (strpos($_SERVER["HTTP_HOST"], 'uapp') > 0) {
                // Creating new client having proxy
                $client = new \SoapClient($this->getWsdl(), array('proxy_host' => "isa4", 'proxy_port' => 8080, 'trace' => true, 'exceptions' => true));
            } else {
                // Creating new client without proxy
                $client = new \SoapClient($this->getWsdl(), array('trace' => true, 'exceptions' => true));
            }

            $OutputParametersOfRegister = $client->__call('Register', array("parameters" => $this->getInputParameters()));

            // RegisterResult
            $RegisterResult = $OutputParametersOfRegister->RegisterResult;

            $terminal_parameters = "?merchantId=" . $this->getMerchantId() . "&transactionId=" . $RegisterResult->TransactionId;
            $process_parameters = "?transactionId=" . $RegisterResult->TransactionId;

            $this->transactionId = $RegisterResult->TransactionId;
            $this->order->paymentTransactionId = $this->transactionId;
            $this->order->status = 2;
            $this->getApi()->getOrderManager()->saveOrder($this->order);
        } catch (SoapFault $fault) {
            print_r($fault);
        }
    }

    public function collectOrder() {
        $order = $this->order;
        if (!$this->isOrderPaidByThisPaymentMethod()) {
            return;
        }
        if($this->order->status == 7) {
            return;
        }
        
        if ($this->order->testOrder) {
            return;
        }
        
        $orderId = $this->order->id;
        $amount = (int)($this->getApi()->getOrderManager()->getTotalAmount($this->getOrder()) * 100);
        /* var $order \core_ordermanager_data_Order */
        $result = $this->processPaymentExtended($amount, $order->paymentTransactionId, $orderId, "CAPTURE");
        if (!$result) {
            $settings = $this->getSettings();
            $user = $this->getApi()->getUserManager()->getUserById($this->order->userId);
            $email = $user->emailAddress;
            $name = $user->fullName;
            $fromName = "Payment Handler";
            if (isset($settings->emailpaymentfailedbody)) {
                $emailbody = $settings->emailpaymentfailedbody->value;
                $emailtitle = $settings->emailpaymentfailedtitle->value;
                $this->getApi()->getMessageManager()->sendMail($email, $name, $emailtitle, $emailbody, $fromMail, $fromName);
                $this->getApi()->getMessageManager()->sendMail($fromMail, $name, $emailtitle, $emailbody, $fromMail, $fromName);
            }
            $this->order->status = 8;
            $this->getApi()->getOrderManager()->saveOrder($this->order);
        } else {
            $this->order->captured = true;
            $this->getApi()->getOrderManager()->saveOrder($this->order);
        }
        
        return $this->order->captured;
    }

    public function paymentCallback() {
        $orderId = $_GET['orderId'];
        $code = $_GET['responseCode'];
        
        if (isset($_GET['orderId']) && !$this->order) {
            $this->order = $this->getApi()->getOrderManager()->getOrder($_GET['orderId']);
        }

        $okpage = false;
        $canceledpage = false;
        $paymentfailed = false;
        $okpage = "/?page=payment_success";
        $paymentfailed = "/?page=payment_failed";
        $canceledpage = "/?page=payment_canceled";

        $found = false;
        if ($code == "OK") {
            $authing = $this->processPaymentExtended($this->getAmount(), $_GET['transactionId'], $orderId, "AUTH");
            if (!$authing) {
                $this->saveOrderStatus(3);
                if ($paymentfailed) {
                    header('Location: ' . $paymentfailed);
                    $found = true;
                }
            }
            if (isset($authing) && $authing->ResponseCode == "OK") {
                if($this->collectOrder()) {
                    $this->saveOrderStatus(7);
                }
                if ($okpage) {
                    header('Location: ' . $okpage);
                    $found = true;
                }
            } else {
                echo "Sorry, but your payment did not validate.";
            }
        } else {
            $this->saveOrderStatus(5);
            if ($canceledpage) {
                header('Location: ' . $canceledpage);
                $found = true;
            }
        }
        if (!$found) {
            echo "Thank you page and canceled page not found yet";
        }
    }

    public function getTerminal() {
        $terminal = "https://epayment.nets.eu/terminal/default.aspx";
        if ($this->getConfigurationSetting("debugmode") == "true" || !$this->getFactory()->productionMode) {
            $terminal = "https://test.epayment.nets.eu/terminal/default.aspx";
        }
        return $terminal;
    }

    public function getTransactionId() {
        return $this->transactionId;
    }

    public function preProcess() {
        $this->registerCall();
        $this->includefile("paymentform");
    }

    public function getStarted() {
        
    }

    public function renderConfig() {
        $this->includefile("Netaxept");
    }

    public function render() {
        
    }

    public function createOrder() {
        $merchid = $this->getConfigurationSetting("merchantid");
        $currency = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::fetchCurrencyCode();;
        $orderId = $this->getOrder()->incrementOrderId;
        $amount = (int)($this->getApi()->getOrderManager()->getTotalAmount($this->getOrder()) * 100);

        $Order = new Order();
        $Order->Amount = $amount; // The amount described as the lowest monetary unit, example: 100,00 NOK is noted as "10000", 9.99 USD is noted as "999".
        $Order->CurrencyCode = "NOK";  //The currency code, following ISO 4217. Typical examples include "NOK" and "USD".
        $Order->Force3DSecure = null;   // Optional parameter
        $Order->Goods = $items;
        $Order->OrderNumber = $orderId;
        $Order->UpdateStoredPaymentInfo = null;


        return $Order;
    }

    public function createTerminal() {
        $redirect_url = "http://" . $_SERVER["HTTP_HOST"] . "/callback.php?app=" . $this->applicationSettings->id. "&orderId=" . $this->order->id;

        $Terminal = new Terminal();
        $Terminal->AutoAuth = null; // Optional parameter
        $Terminal->Language = null; // Optional parameter
        $Terminal->OrderDescription = null; // Optional parameter
        $Terminal->RedirectOnError = $redirect_url; // Optional parameter
        $Terminal->RedirectUrl = $redirect_url;

        return $Terminal;
    }

    public function createEnvironment() {
        $environment = new Environment();
        $environment->Language = null;
        $environment->OS = null;
        $environment->WebServicePlatform = "PHP5";
        return $environment;
    }

    public function setOrderId($orderId) {
        $this->orderId = $orderId;
    }

    public function processPaymentExtended($transactionAmount, $transactionId, $orderId, $operation) {

        ####  PROCESS OBJECT  ####
        $ProcessRequest = new ProcessRequest();
        $ProcessRequest->Description = "order id : " . $orderId;
        $ProcessRequest->Operation = $operation;
        $ProcessRequest->TransactionAmount = $transactionAmount;
        $ProcessRequest->TransactionId = $transactionId;
        $ProcessRequest->TransactionReconRef = "";

        $InputParametersOfProcess = array
            (
            "token" => $this->getToken(),
            "merchantId" => $this->getMerchantId(),
            "request" => $ProcessRequest
        );
        
        try {
            $logentry = $ProcessRequest->Description . " : " . $ProcessRequest->Operation . " : " . $ProcessRequest->TransactionAmount . " : " . $ProcessRequest->TransactionId;
            $this->getApi()->getOrderManager()->logTransactionEntry($orderId, $logentry);

            $wsdl = $this->getWsdl();
            $client = new \SoapClient($wsdl, array('trace' => true, 'exceptions' => true));
            $OutputParametersOfProcess = $client->__call('Process', array("parameters" => $InputParametersOfProcess));
            $result = $OutputParametersOfProcess->ProcessResult;
            $this->logTransaction($result, $orderId);
            return $result;
        } catch (\SoapFault $fault) {
            $this->logTransaction($fault, $orderId);
            return false;
        }
    }

    public function logTransaction($fault, $orderId) {
        if ($fault instanceof \SoapFault) {
            $error = $fault->getCode() . " " . $fault->getMessage();
        } else {
            $error = $fault->{'AuthorizationId'} . " responsecode: " . $fault->{'ResponseCode'};
        }
        $this->getApi()->getOrderManager()->logTransactionEntry($orderId, $error);
    }

    public function getAmount() {
        $items = array();
        $amount = 0;
        foreach ($this->order->cart->items as $cartItem) {
            /* @var $cartItem core_cartmanager_data_CartItem */
            /** @var product \core_productmanager_data_Product */
            $product = $cartItem->product;
            $item = new Item();
            if (isset($product->taxGroupObject)) {
                $item->VAT = ($product->taxGroupObject->taxRate + 100) / 100;
            } else {
                $item->VAT = 1;
            }
            $item->Amount = (int) (($product->price * 10) * 100);
            $item->Handling = true;
            $item->IsVatIncluded = true;
            $item->Quantity = $cartItem->count;
            $item->Shipping = true;
            $item->Title = $product->name;
            $amount += $item->Amount;
//            $items[] = $item;
        }
        return $amount;
    }

    public function saveSettings() {        
        $this->setConfigurationSetting("merchantid", $_POST['merchantid']);
        $this->setConfigurationSetting("token", $_POST['token']);
        $this->setConfigurationSetting("okpage", $_POST['okpage']);
        $this->setConfigurationSetting("canceledpage", $_POST['canceledpage']);
        $this->setConfigurationSetting("paymentfailed", $_POST['paymentfailed']);
        $this->setConfigurationSetting("emailpaymentfailedtitle", $_POST['emailpaymentfailedtitle']);
        $this->setConfigurationSetting("emailpaymentfailedbody", $_POST['emailpaymentfailedbody']);
        $this->setConfigurationSetting("debugmode", $_POST['debugmode']);

    }

    public function saveOrderStatus($status) {
        if (isset($this->order)) {
            $this->order->status = $status;
            $this->getApi()->getOrderManager()->saveOrder($this->order);
        }
    }

    public function isOrderPaidByThisPaymentMethod() {
        if (!$this->order) {
            return false;
        }
        
        if (!$this->order->payment) {
            return false;
        }
        
        if ($this->order->payment->paymentType != "ns_def1e922_972f_4557_a315_a751a9b9eff1\Netaxept") {
            return false;
        }
        
        return true;
    }

    public function parseCsv() {

        $lines = explode(PHP_EOL, $_POST['content']);
        $array = array();
        
        echo "<div class='gss_settings_header'><i class='fa fa-check-circle'></i><div class='gss_settings_header_description'>Read CSV successfully<br/><br/>";

        foreach ($lines as $line) {
            $line = str_getcsv($line);
            if ($line[0] == "Transaksjon") {
                $orderId = $line[6];
                $mictroTransactionId = $line[9];
                
                $order = $this->getApi()->getOrderManager()->getOrderByincrementOrderId($orderId);
                if ($order) {
                    $found = false;
                    
                    foreach ($order->transactions as $transaction) {
                        if ($transaction->microTransactionReference == $mictroTransactionId) {
                            $found = true;
                        }
                    }
                    
                    if ($found) {
                        continue;
                    }
                    
                    $transaction = new \core_ordermanager_data_CardTransaction();
                    $transaction->date = $this->formatTimeToJavaDate(strtotime($line[2]));
                    $transaction->currency = $line[3];
                    $transaction->amount = $line[4];
                    $transaction->microTransactionReference = $mictroTransactionId;
                    
                    echo "<div>Order: ".$order->incrementOrderId." Amount: " . $transaction->amount."  Currency: ".$transaction->currency."</div>";
                    $order->transactions[] = $transaction;
                    $this->getApi()->getOrderManager()->saveOrder($order);
                }
            }
        }
        
        echo '</div></div>';
        
    }

    public function handleCallBack($transactionId) {
        //100379___5b1e87046e5e2r
        echo "transaction id : " . $transactionId;
        $transactionIdSplitted = explode("___", $transactionId);
        $incOrderId = $transactionIdSplitted[0];
        $this->order = $this->getApi()->getOrderManager()->getOrderByincrementOrderIdAndPassword($incOrderId, "fdsafd4e3453ngdgdf");
        $this->order->paymentTransactionId = $transactionId;
        $_GET['orderId'] = $incOrderId;
        $_GET['responseCode'] = "OK";
        $_GET['transactionId'] = $transactionId;
        if($this->order->status != 7) {
            $this->paymentCallback();
        }
    }

}

?>

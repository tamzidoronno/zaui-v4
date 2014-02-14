<?php
/**
 * Sisow is a payment solutions suitable for Holland and belgium. 
 * 
 * More information can be found on : 
 * https://www.sisow.nl/
 *
 * @author ktonder
 */
namespace ns_c4d7bec0_185f_11e3_8ffd_0800200c9a66;

class Sisow extends \PaymentApplication implements \Application {
    private $selectedBank = 0;
    private $eBillEmailAddress = "";
    private $paymentMethod = "";
    private $response;
    private $merchantRequest = false;
    private $merchantKey = "";
    
    public function getName() {
        return "Sisow";
    }

    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        
        if ($this->getConfigurationSetting("merchantkey_ok") !== "true") {
            return;
        }
        
        if ($this->getConfigurationSetting("on_ideal") == "true") {
            $this->addPaymentMethod("iDeal", "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/ideal_logo.jpg", "ideal");
        }
        
        if ($this->getConfigurationSetting("on_ebill") == "true") {
            $this->addPaymentMethod("eBill", "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/ebill.png", "ebill");
        }
        
        if ($this->getConfigurationSetting("on_mistercash") == "true") {
            $this->addPaymentMethod("Mister Cash", "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/mistercash.png", 'mistercash');
        }
        
        if ($this->getConfigurationSetting("on_sofort") == "true") {
            $this->addPaymentMethod("Sofort Banking", "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/sofort.png", 'sofortbanking');
        }
        
        if ($this->getConfigurationSetting("on_fjincadeau") == "true") {
            $this->addPaymentMethod("Fjincadeau", "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/Fijncadeau.png", 'fjincadeau');
        }
        
        if ($this->getConfigurationSetting("on_podium") == "true") {
            $this->addPaymentMethod("Podium", "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/unkown.png", 'podium');
        }
    }

    public function getDescription() {
        $description = $this->__w("By using sisow as your payment provider, you will simply offer your customers to pay with iDeal/Visa and lots more. More information can be found at https://www.sisow.nl");
        return $description;
    }
    
    public function paymentCallback() {
        $sha1 = sha1($_GET['trxid'].$_GET['ec'].$_GET['status'].$this->getMerchantId().$this->getMerchantKey());
        if ($sha1 != $_GET['sha1']) {
            echo "Sorry, the validation check failed, please try again";
        } else {
            $this->startAdminImpersonation("OrderManager", "changeOrderStatus");
            if($_GET['status'] == "Success") {
                $this->getApi()->getOrderManager()->changeOrderStatus($_GET['trxid'], 4);
                $this->includefile("paymentsuccessfully");
            } elseif ($_GET['status'] == "Failure" || $_GET['status'] == "Cancelled" || $_GET['status'] == "Expired") {
                $this->getApi()->getOrderManager()->changeOrderStatus($_GET['trxid'], 3);
                $this->includefile("paymentfailure");
            } else {
                echo "Unexpected error happend, please contact store owner";
            }
            $this->stopImpersionation();
        }
    }
    
    public function renderConfig() {
        $this->includeFile("sisowconfig");
    }
 
    public function preProcess() {
        if ($this->isIdeal()) {
            $this->processIdeal();
        }
        
        if ($this->isEBill()) {
            $this->processEBill();
        }
        
        if ($this->isMisterCash()) {
            $this->processMisterCash();
        }
        
        if ($this->isSofortBanking()) {
            $this->processSofortBanking();
        }
        
        if ($this->isFjincadeau()) {
            $this->processFjincadeau();
        }
        
        if ($this->isPodium()) {
            $this->processPodium();
        }
    }
    
    private function processSofortBanking() {
        $url = $this->getIdealRedirectUrl("sofort");
        $response = file_get_contents($url);
        $xml = new \SimpleXMLElement($response);
        $urlRedirect = $xml->transaction->issuerurl;
        $this->addTransactionIdToOrder($xml);
        $this->redirect($urlRedirect);
    }
    
    private function processMisterCash() {
        $url = $this->getIdealRedirectUrl("mistercash");
        $response = file_get_contents($url);
        $xml = new \SimpleXMLElement($response);
        $this->addTransactionIdToOrder($xml);
        $urlRedirect = $xml->transaction->issuerurl;
        $this->redirect($urlRedirect);
    }
    
    private function processFjincadeau() {
        $url = $this->getIdealRedirectUrl("fijncadeau");
        $response = file_get_contents($url);
        $xml = new \SimpleXMLElement($response);
        $urlRedirect = $xml->transaction->issuerurl;
        $this->addTransactionIdToOrder($xml);
        $this->redirect($urlRedirect);
    }
    
    private function processPodium() {
        $url = $this->getIdealRedirectUrl("podium");
        $response = file_get_contents($url);
        $xml = new \SimpleXMLElement($response);
        $urlRedirect = $xml->transaction->issuerurl;
        $this->addTransactionIdToOrder($xml);
        $this->redirect($urlRedirect);
    }
    
    private function processEBill() {
        $url = $this->getIdealRedirectUrl("ebill");
        $url .= "&billing_mail=".$this->eBillEmailAddress;
        $response = file_get_contents($url);
        $this->response = new \SimpleXMLElement($response);
        $this->addTransactionIdToOrder($this->response);
        $this->includefile("ebillsuccess");
    }
    
    private function addTransactionIdToOrder($xml) {
        if (isset($xml) && isset($xml->transaction) && isset($xml->transaction->trxid)) {
            $transaction = $xml->transaction->trxid;
            $txId = "".$transaction[0];
            $this->order->paymentTransactionId = $txId;
            $this->getApi()->getOrderManager()->saveOrder($this->order);
        }
    }
    
    private function processIdeal() {
        $url = $this->getIdealRedirectUrl("iDEAL");
        $url .= "&issuerid=".$this->selectedBank;
        $response = file_get_contents($url);
        $xml = new \SimpleXMLElement($response);
        $urlRedirect = $xml->transaction->issuerurl;
        $this->addTransactionIdToOrder($xml);
        $this->redirect($urlRedirect);
    }
    
    private function redirect($url) {
        echo "<script> window.location = '/redirect.php?url=$url'</script>";
        $this->includefile("redirected");
    }
    
    public function render() {
        
    }
    
    private function isTestMode() {
        $config = $this->getConfigurationSetting("testmode");
        return $config === "true";
    }
    
    private function getMerchantRequest() {
        if ($this->merchantRequest) {
            return $this->merchantRequest;
        }
        $merchantId = $this->getMerchantId();
        $merchantKey = $this->getMerchantKey();
        $sha1 = sha1($merchantId.$merchantKey);
        $url = "https://www.sisow.nl/Sisow/iDeal/RestHandler.ashx/CheckMerchantRequest?merchantid=$merchantId&sha1=$sha1";
        $response = file_get_contents($url);
        $this->merchantRequest = new \SimpleXMLElement($response);
        return $this->merchantRequest;
    }
    
    public function isMerchankeyOk() {
        $request = $this->getMerchantRequest();
        if (isset($request->merchant) && $request->merchant->merchantid == $this->getMerchantId()) {
            $this->setConfigurationSetting("merchantkey_ok", "true");
            return true;
        } 
       
        $this->setConfigurationSetting("merchantkey_ok", "false");
        return false;        
    }
    
    public function isMerchantActivated() {
        $request = $this->getMerchantRequest();
        if (isset($request->merchant) && !$request->merchant->active) {
            return true;
        }
       
        return false;
    }
    
    public function getIdealBanks() {
        $url = "https://www.sisow.nl/Sisow/iDeal/RestHandler.ashx/DirectoryRequest";
        
        if ($this->isTestMode()) {
            $url .= "?test=true";
        }
        
        $response = file_get_contents($url);
        $request = new \SimpleXMLElement($response);

        $banks = array();
        foreach ($request->directory->issuer as $issuer) {
            $banks[] = $issuer;
        }
        return $banks;
    }
    
    private function curPageUrl() {
        return @$_SERVER['HTTP_ORIGIN'];
    }
    
    private function getIdealRedirectUrl($paymentType) {
        $merchantId = $this->getMerchantId();
        $merchantKey = $this->getMerchantKey();
        $purchaseId = $this->order->incrementOrderId;
        // Sepecified in cents, multipleying with 100.
        $amount = $this->getTotal() * 100;
        $description = "Order:".$this->order->incrementOrderId;
        $entrancecode = rand(100000000, 999999999);
        $sha1 = sha1("$purchaseId$entrancecode$amount$merchantId$merchantKey");
        $callBackUrl = $this->curPageURL()."/?page=callback%26app=".$this->getConfiguration()->id;
        $cancelUrl = $this->curPageURL()."/?page=callback%26app=".$this->getConfiguration()->id;
        $returnUrl = $this->curPageURL()."/?page=callback%26app=".$this->getConfiguration()->id;
        $url = "https://www.sisow.nl/Sisow/iDeal/RestHandler.ashx/TransactionRequest?shopid=&merchantid=$merchantId&payment=$paymentType&purchaseid=$purchaseId&amount=$amount&entrancecode=$entrancecode&description=$description&returnurl=$returnUrl&cancelurl=$cancelUrl&callbackurl=$callBackUrl&sha1=$sha1&notifyurl=$callBackUrl";
        
        return $url;
    }
    
    private function getTotal() {
        $total = $this->getApi()->getOrderManager()->getTotalAmount($this->order);
        return $total;
    }
    
    public function isAvailable() {
        return true;
    }
    
    public function getMerchantId() {
        return trim($this->getConfigurationSetting("merchantid"));
    }
    
    public function getMerchantKey() {
        if ($this->merchantKey == "") {
            $this->startAdminImpersonation("PageManager", "getSecuredSettings");
            $settings = $this->getApi()->getPageManager()->getSecuredSettings($this->getConfiguration()->id);
            if(isset($settings->merchantkey->value)) {
                $this->merchantKey = $settings->merchantkey->value;
            }
            $this->stopImpersionation();
        }
        return trim($this->merchantKey);
    }
    
    public function hasSubProducts() {
        return true;
    }
    
    public function doSubProductProccessing() {
        if (!isset($_POST['data']['paymentMethod'])) {
            return;
        }
        
        $this->paymentMethod = $_POST['data']['paymentMethod'];
        if ($this->isIdeal()) {
            $this->selectedBank = isset($_POST['data']['issuer']) ? $_POST['data']['issuer'] : $this->selectedBank;
        }
        
        if ($this->isEBill()) {
            $this->eBillEmailAddress = isset($_POST['data']['ebillname']) ? $_POST['data']['ebillname'] : $this->eBillEmailAddress;
        }
    }
    
    private function isIdeal() {
        return $this->paymentMethod == "ideal";
    }
    
    private function isMisterCash() {
        return $this->paymentMethod == "mistercash";
    }
    
    private function isSofortBanking() {
        return $this->paymentMethod == "sofortbanking";
    }
    
    public function isReadyToPay() {
        
        if ($this->isIdeal()) {
            return $this->selectedBank > 0;
        }
        
        if ($this->isEBill()) {
            return filter_var($this->eBillEmailAddress, FILTER_VALIDATE_EMAIL);
        }
        
        if ($this->isMisterCash()) {
            return true;
        }
        
        if ($this->isSofortBanking()) {
            return true;
        }
        
        if ($this->isFjincadeau()) {
            return true;
        }
        
        if ($this->isPodium()) {
            return true;
        }
        
        return false;
    }
    
    public function getExtendedPaymentForm(\PaymentMethod $paymentMethod) {
        if ($paymentMethod->getId() == "ideal") {
            ob_start();
            $this->includefile("sisowpaymentwindow");
            $html = ob_get_contents();
            ob_end_clean();
            return $html;
        }
        
        if ($paymentMethod->getId() == "ebill") {
            ob_start();
            $this->includefile("ebill");
            $html = ob_get_contents();
            ob_end_clean();
            return $html;
        }
    }

    public function isEBill() {
        return $this->paymentMethod == "ebill";
    }
    
    public function getResponse() {
        return $this->response;
    }
    
    private function isFjincadeau() {
        return $this->paymentMethod == "fjincadeau";
    }
    
    private function isPodium() {
        return $this->paymentMethod == "podium";
    }
    
    public function requestAdminRights() {
        $this->requestAdminRight("PageManager", "getSecuredSettings", $this->__o("The merchant key is stored securly in the backend, need admin to retreive it."));
        $this->requestAdminRight("OrderManager", "changeOrderStatus", $this->__o("Require admin right for this function because it needs to update the orderstatus upon callback"));
    }
}
?>
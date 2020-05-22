<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of InvoicePayment
 *
 * @author ktonder
 */
namespace ns_70ace3f0_3981_11e3_aa6e_0800200c9a66;

class InvoicePayment extends \PaymentApplication implements \Application{
    var $overrideSentInvoiceCallback;
    
    public function getDescription() {
        return $this->__f("Allows customers to pay by invoice.");
    }

    public function render() {
        $this->includefile("sendorder");
    }
    
    public function hasPaymentProcess() {
        return true;
    }
    
    public function hasAttachment() {
        return true;
    }
    
    public function preProcess() {
        $orderId = $this->order->id;
        $this->getApi()->getInvoiceManager()->createInvoice($orderId);
        $this->includeFile("invoicesuccess");
    }
    
     public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod($this->getName(), "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/invoice.png", "Invoice");
    }

    public function getName() {
        return $this->__w("InvoicePayment");
    }
    
    public function renderConfig() {
        $this->includeFile("config");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("accountNumber", $_POST['accountNumber']);
        $this->setConfigurationSetting("vatNumber", $_POST['vatNumber']);
        $this->setConfigurationSetting("address", $_POST['address']);
        $this->setConfigurationSetting("companyName", $_POST['companyName']);
        $this->setConfigurationSetting("postCode", $_POST['postCode']);
        $this->setConfigurationSetting("city", $_POST['city']);
        $this->setConfigurationSetting("contactEmail", $_POST['contactEmail']);
        $this->setConfigurationSetting("webAddress", $_POST['webAddress']);
        $this->setConfigurationSetting("duedays", $_POST['duedays']);
        $this->setConfigurationSetting("defaultKidMethod", $_POST['defaultKidMethod']);
        $this->setConfigurationSetting("kidSize", $_POST['kidSize']);
        $this->setConfigurationSetting("type", $_POST['type']);
        $this->setConfigurationSetting("iban", $_POST['iban']);
        $this->setConfigurationSetting("swift", $_POST['swift']);
        $this->setConfigurationSetting("language", $_POST['language']);
        $this->setConfigurationSetting("phoneNumber", $_POST['phoneNumber']);
        $this->setConfigurationSetting("logo", $_POST['logo']);
        $this->setConfigurationSetting("useSingleRoomTypes", $_POST['useSingleRoomTypes']);
        $this->setConfigurationSetting("unpaidinvoicetext", $_POST['unpaidinvoicetext']);
    }
    
    public function renderPaymentOption() {
        $this->includefile("paymentoption");
    }
    
    public function doPayment($cart = false) {
        $order = parent::doPayment($cart);
        $order->expiryDate = $this->convertToJavaDate(strtotime($_POST['data']['expirydate']));
        $order->invoiceNote = $_POST['data']['invoicenote']; 
        $this->getApi()->getOrderManager()->saveOrder($order);
        return $order;
    }
    
    public function getExtraInformation($order) {
        if (!isset($order->expiryDate)) {
            return;
        }
        
        $expdate = $this->__f("Expiry date").": ".date('d/m-Y', strtotime($order->expiryDate));
        if ($order->invoiceNote) {
            $expdate .= "<div style='display: block;'><b>".$this->__f("Note")."</b>:<br/>".nl2br($order->invoiceNote)."</div>";
        }
        return $expdate;
    }
    
    public function getColor() {
        return "pink";
    }
    
    public function getIcon() {
        return "invoice.svg";
    }
    
    public function sendEmail() {
        $email = $_POST['data']['bookerEmail'];
        $bookingId = $_POST['data']['bookingid'];
        $orderid = $_POST['data']['orderid'];
        $msg = $_POST['data']['emailMessage'];
        $subject = $_POST['data']['subject'];
        $this->getApi()->getOrderManager()->closeOrder($orderid, "Sent to customer");
        $res = $this->getApi()->getPmsInvoiceManager()->sendRecieptOrInvoiceWithMessage($this->getSelectedMultilevelDomainName(), $orderid, $email, $bookingId, $msg, $subject);
        
        $salesPoint = new \ns_11234b3f_452e_42ce_ab52_88426fc48f8d\SalesPointTabPayment();
        unset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment']);
        $salesPoint->cancelCurrentOrder();
        $salesPointNew = new \ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale();
        $salesPointNew->deleteCurrentTab();
    }
    
    public function sendEhf() {
        $orderList = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
        
        $orderid = $_POST['data']['orderid'];
        $xml = $this->getApi()->getOrderManager()->getEhfXml($orderid);
        if ($xml == "failed") {
            echo "<span style='color: red'><i class='fa fa-warning'></i> Something wrong happend while creating EHF invoice, GetShop has been notified and will contact you once its sorted out.</span>";
            return;
        }
        
        $res = $orderList->sendDocument($xml);
        if ($res[0] != "ok") {
            echo "<span style='color: red'><i class='fa fa-warning'></i> Something went wrong during sending EHF, please contact GetShop Support.</span>";
            return;
        }
        
        $this->getApi()->getOrderManager()->registerSentEhf($orderid);
        $this->getApi()->getOrderManager()->closeOrder($orderid, "Invoice sent by EHF to customer.");
        echo "<span style='color: green'><i class='fa fa-check'></i> EHF Sent successfully</span>";
        $this->order = $this->getApi()->getOrderManager()->getOrder($orderid);
    }
    
    
    function endsWith($haystack, $needle) {
        $length = strlen($needle);
        if ($length == 0) {
            return true;
        }

        return (substr($haystack, -$length) === $needle);
    }
    
    public function getEhfProblems($order, $user) {
        $ret = array();
  
        $accountingdetails = $this->getApi()->getInvoiceManager()->getAccountingDetails();
        $vatnumber = strtolower($accountingdetails->vatNumber);

        if(!$this->endsWith($vatnumber, "mva")) {
            $ret[] = "In your invoice details you need to set up your vat number to be exactly: 123456789MVA, vat number registered: $vatnumber, missing the MVA postfix";
        } else {
            $vatnumber = str_replace("mva", "", $vatnumber);
            if(!is_numeric($vatnumber) || $vatnumber < 100000000 || $vatnumber > 999999999) {
                $ret[] = "In your invoice details you need to set up your vat number to be exactly: 123456789MVA, the number need to be exact 9 digits";
            }
        }
        
        
        
        if (!$user->fullName) {
            $ret[] = "The name of the customer can not be blank";
        }
        
        if (!$user->address || !$user->address->address) {
            $ret[] = "Street address of the customer can not be blank";
        }
        
        if (!$user->address || !$user->address->city) {
            $ret[] = "City of the customer address can not be blank";
        }
        
        if (!$user->address || !$user->address->postCode) {
            $ret[] = "Postcode of the customer address can not be blank";
        }
        
        
        if (!$user->companyObject || !$user->companyObject->name) {
            $ret[] = "Customer company name can not be blank";
        }
        
        return $ret;
    }

   

}
?>

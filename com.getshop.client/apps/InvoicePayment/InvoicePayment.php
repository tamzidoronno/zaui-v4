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
    public function getDescription() {
        return $this->__f("Allows customers to pay by invoice.");
    }

    public function render() {
        
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
        $this->setConfigurationSetting("currency", $_POST['currency']);
        $this->setConfigurationSetting("type", $_POST['type']);
        $this->setConfigurationSetting("iban", $_POST['iban']);
        $this->setConfigurationSetting("swift", $_POST['swift']);
        $this->setConfigurationSetting("language", $_POST['language']);
        $this->setConfigurationSetting("phoneNumber", $_POST['phoneNumber']);
        $this->setConfigurationSetting("logo", $_POST['logo']);
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
        return "darkgreen";
    }
}
?>

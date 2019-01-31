<?php
namespace ns_e8cc6f68_d194_4820_adab_6052377b678d;

class SendRegning extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Send regning is a platform for sending invoices. They support most of the common payment methods like invoice, ocr scanning, ehf, etc. Transfer your bills to sendregning and get more control on your invoices.";
    }

    public function getName() {
        return "SendRegning";
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("sendregningconfig");
    }    
    
    public function render() {
        
    }
    
    public function isPublicPaymentApp() {
        return false;
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("email", $_POST['email']);
        $this->setConfigurationSetting("password", $_POST['password']);
        $this->setConfigurationSetting("originatorid", $_POST['originatorid']);
    }    
}
?>

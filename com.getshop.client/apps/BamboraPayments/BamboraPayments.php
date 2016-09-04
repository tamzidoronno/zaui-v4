<?php
namespace ns_a92d56c0_04c7_4b8e_a02d_ed79f020bcca;

class BamboraPayments extends \PaymentApplication implements \Application {
    
    public function getDescription() {
        return $this->__("bambora payment services allows you to start charging with visa / mastercard / and other card vendors. its extremly easy to use. prices for using this application is provided by dibs, and you will be contacted by dibs when adding this application.");
    }

    public function getName() {
        return "BamboraPayments";
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "");
    }
    
    public function simplePayment() {
        $orderId = $this->getOrder()->id;
        $url = $this->getApi()->getBamboraManager()->getCheckoutUrl($orderId);
        echo "URL : " . $url . "(" . $orderId . ")";
    }
    
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("configuration");
        
    }

    public function saveSettings() {
        $this->setConfigurationSetting("merchant_id", $_POST['merchant_id']);
        $this->setConfigurationSetting("access_token", $_POST['access_token']);
        $this->setConfigurationSetting("secret_token", $_POST['secret_token']);
    }
    
    public function render() {
        
    }
}
?>

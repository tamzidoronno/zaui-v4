<?php

/**
 * This application is used to redirect to the correct payment method
 * for the callback page.
 * 
 * @Author ktonder.
 */
namespace ns_5474c225_cc7b_4576_83bb_1ad8bf35be8f;
class Callback extends \SystemApplication implements \Application {
    public function getDescription() {
        return "";
    }

    public function getName() {
        return $this->__o("Callback");
    }
    
    private function getPaymentApplication() {
        if (!isset($_GET['app']))
            return null;
        
        $id = $_GET['app'];
        $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($id);
        return $app;
    }

    public function render() {
        $paymentApp = $this->getPaymentApplication();
        $paymentApp->paymentCallback();
    }    
}

?>

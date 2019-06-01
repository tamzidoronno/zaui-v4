<?php
namespace ns_34143422_f8af_4b46_9473_8cf86ca1a1a6;

class SalesPointSelectPayment extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointSelectPayment";
    }

    public function render() {
        $this->includefile("selectpayment");
    }

    
    /**
     * 
     * @return \ApplicationBase[]
     */
    public function getPaymentApps() {
        $paymentApps = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        $instances = array();
        
        foreach ($paymentApps as $app) {
            $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
            $instances[] = $instance;
        }
        
        return $instances;
    }

    public function paymentMethodSelected() {
        $apps = $this->getPaymentApps();
        $cart = $this->getApi()->getCartManager()->getCart();
        
        foreach ($apps as $app) {
            if ($app->getApplicationSettings()->id == $_POST['data']['paymentmethod']) {
                $app->processPayment(null, "SalesPoint");
            }
        }
    }
}
?>

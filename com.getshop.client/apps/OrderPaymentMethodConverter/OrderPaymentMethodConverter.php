<?php
namespace ns_b3dce8c9_b77b_4c4b_a398_af98863c2f91;

class OrderPaymentMethodConverter extends \PaymentApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "OrderPaymentMethodConverter";
    }

    public function render() {
        $this->includefile("paymentconverter");
    }
    
    public function convertPaymentMethods() {
        $fromtype = str_replace("-","_", $_POST['data']['fromtype']);
        $totype = str_replace("-","_", $_POST['data']['totype']);
        $state = (int)$_POST['data']['state'];
        $orders = $this->getApi()->getOrderManager()->getOrders(null, null, null);

        $methods = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        $methodToConvertTo = null;
        foreach($methods as $m) {
            if($m->id == $_POST['data']['totype']) {
                $methodToConvertTo = $m;
            }
        }
        $namespace = "ns_" . $totype . "\\" . $methodToConvertTo->appName;
        
        foreach($orders as $order) {
            if(stristr($order->payment->paymentType, $fromtype)) {
                if($state == 0 || $state == $order->status) {
                    echo "do convert: " . $order->incrementOrderId . "<br>";
                    $order->payment->paymentType = $namespace;
                    $this->getApi()->getOrderManager()->saveOrder($order);
                }
            }
        }
    }
}
?>

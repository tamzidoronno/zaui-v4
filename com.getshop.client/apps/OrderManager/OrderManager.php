<?php

namespace ns_27716a58_0749_4601_a1bc_051a43a16d14;

class OrderManager extends \SystemApplication implements \Application {

    public $orders;
    public $showOrder = false;
    public $showInvoice = false;
    public $pageSize = 10;
    public $currentPage = 0;

    public function getDescription() {
        
    }

    public function getName() {
        
    }

    public function postProcess() {
        
    }
    
    public function collect() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);

        /* @var $paymentApp \PaymentApplication */
        $namespace = substr($order->payment->paymentType, 0, strpos($order->payment->paymentType, "\\"));

        $apps = $this->getFactory()->getApplicationPool()->getApplicationsInstancesByNamespace($namespace);
        $app = $apps[0];
        $app->order = $order;
        $app->collectOrder();
    }

    public function isOrderOverviewSelected() {
        return !($this->showOrder || $this->showInvoice);
    }

    private function cancelOrder() {
        if (isset($_GET['cancel']) && $_GET['cancel'] == "true") {
            $order = $this->getCurrentOrder();
            $order->status = 5;
            $this->getApi()->getOrderManager()->saveOrder($order);
        }
    }

    public function ChangeOrderStatus() {
        $orderId = $_POST['data']['orderid'];
        $status = $_POST['data']['status'];
        $this->getApi()->getOrderManager()->changeOrderStatus($orderId, $status);
    }
    
    public function translateStatusText($status) {
        $statustext = ($status == 1 || $status == 0) ? $this->__w("Order Created") : "";
        $statustext = ($status == 2) ? $this->__w("Waiting for payment") : $statustext;
        $statustext = ($status == 3) ? $this->__w("Payment failed") : $statustext;
        $statustext = ($status == 4) ? $this->__w("Completed") : $statustext;
        $statustext = ($status == 5) ? $this->__w("Canceled") : $statustext;
        $statustext = ($status == 6) ? $this->__w("Order sent") : $statustext;
        $statustext = ($status == 7) ? $this->__w("Need collecting") : $statustext;
        return $statustext;
    }

    public function preProcess() {
        if (!isset($_GET['showPage'])) {
            $_GET['showPage'] = 1;
        }

        $this->currentPage = $_GET['showPage'];
        $this->showOrder = (isset($_GET['action']) && $_GET['action'] == 'showorder');
        $this->showInvoice = (isset($_GET['action']) && $_GET['action'] == 'showinvoice');
    }

    private function populateAnswers() {
        $orderIds = array();
        if ($this->showOrder) {
            $orderIds[] = $_GET['orderid'];
        }
        $this->orders = $this->getApi()->getOrderManager()->getOrders($orderIds, $this->currentPage - 1, 10);
    }

    public function render() {
        $this->populateAnswers();
        $this->cancelOrder();
        $this->includefile('index');
    }

    public function getOrders() {
        return $this->orders;
    }

    public function getCurrentOrder() {
        if (!is_array($this->orders)) {
            $this->orders = array();
        }

        if (!isset($_GET['orderid']))
            return null;

        foreach ($this->orders as $order) {
            if ($order->id == $_GET['orderid'])
                return $order;
        }

        return null;
    }

    public function getYoutubeId() {
        return false;
    }

    public function getPageCount() {
        return 1;
    }

    public function getCurrentPage() {
        return $this->currentPage;
    }
    
    public function getPriceWithTaxSpecifed($price, $product) {
        if (isset($this->getFactory()->getSettings()->remove_taxes) && $this->getFactory()->getSettings()->remove_taxes->value === "true") {
            if (isset($product->taxGroupObject) && $product->taxGroupObject->taxRate > 0 ) {
                $factor = ($product->taxGroupObject->taxRate/100)+1;
                $price = $price / $factor;
            }
        }
        
        return $price;
    }

}

?>

<?php

namespace ns_27716a58_0749_4601_a1bc_051a43a16d14;

class OrderManager extends GSTableCommon implements \Application {


    public function getDescription() {
        
    }
    
    public function changeOrderDate() {
        $newDate = $_POST['data']['date'];
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['id']);
        $order->rowCreatedDate = $this->convertToJavaDate(strtotime($newDate));
        $order->createdDate = $this->convertToJavaDate(strtotime($newDate));
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function updatePaidDate( ){
        $date = $this->convertToJavaDate(strtotime($_POST['newDate']));
        $orderId = $_POST['value'];
        $this->getApi()->getOrderManager()->markAsPaid($orderId, $date, 0.0);
    }

    public static function getOrderStatuses() {
        $states[1] = "Created";
        $states[2] = "Waiting for payment";
        $states[3] = "Payment failed";
        $states[4] = "Completed";
        $states[5] = "Canceled";
        $states[6] = "Sent";
        $states[7] = "Payment completed";
        $states[8] = "Collection failed";
        $states[9] = "Need collecting";
        $states[10] = "Send to invoice";
        return $states;
    }
    
    public function getName() {
        return $this->__f("OrderManager");
    }

    public function postProcess() {
        
    }
    
    public function updateAdditionalData() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['value']);
        $type = $_POST['type'];
        foreach($order->cart->items as $item) {
            if($item->cartItemId == $_POST['itemid']) {
                if($type == "startDate") { $item->startDate = $this->convertToJavaDate(strtotime($_POST['newValue'])); }
                if($type == "endDate") { $item->endDate = $this->convertToJavaDate(strtotime($_POST['newValue'])); }
            }
        }
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function updateInvoiceInformation() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['orderId']);
        $order->invoiceNote = $_POST['text'];
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function updateOrderState() {
        $state = $_POST['state'];
        $orders = $_POST['orders'];
        foreach($orders as $orderId) {
            $order = $this->getApi()->getOrderManager()->getOrder($orderId);
            $order->status = $state;
            $this->getApi()->getOrderManager()->saveOrder($order);
        }
    }

    public function lockOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['value']);
        
        if ($order->closed) {
            echo "H";
            $order->closed = false;
            $order->forcedOpen = true;
        } else {
            echo "D";
            $order->closed = true;
            $order->forcedOpen = false;   
        }
        
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function renderConfig() {
        $this->includefile("orderoverview");
    }
    
    public function payWithCard() {
        $orderId = $_POST['value'];
        $cardId = $_POST['card'];
        
        $this->getApi()->getOrderManager()->payWithCard($orderId, $cardId);
    }
    
    public function renderDashBoardWidget() {
        $this->includefile("dashboardwidget");
    }
    
    public function getDashboardChart($year = false) {
        $statistics = $this->getApi()->getOrderManager()->getSalesNumber($year);
        $newStat = [];
        foreach ($statistics as $stat) {
            $newStat[$stat->month] = $stat->count;
        }
        echo "<script>";
            echo "app.OrderManager.yearStats = ".json_encode($newStat);
        echo "</script>";
        
        return ['fa-credit-card', 'app.OrderManager.drawChart', $this->__f("Orders")];
    }
    
    public function render() {
  
    }
    
    public function gsEmailSetup($model) {
        if (!$model) {
            $this->includefile("emailsettings");
            return;
        } 
        
        $this->setConfigurationSetting("ordersubject", $_POST['ordersubject']);
        $this->setConfigurationSetting("orderemail", $_POST['emailconfig']);
        $this->setConfigurationSetting("shouldSendEmail", $_POST['shouldSendEmail']);
    }
    
    public function updateOrder() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['value']);
        if(!$order->captured && $_POST['isCaptured']) {
            $order->status = 7;
        }
        $order->captured = $_POST['isCaptured'];
        $order->testOrder = $_POST['isTestOrder'];
        $this->getApi()->getOrderManager()->saveOrder($order);    
    }
    
    public function showOrder() {
        
    }
    
    /**
     * 
     * @param \core_ordermanager_data_Order $order
     * @return type
     */
    public function canChangeOrder($order) {
        if($order->createByManager == "PmsDailyOrderGeneration") {
            return false;
        }
        if($order->transferedToAccountingSystem || $order->transferredToAccountingSystem) {
            return false;
        }
        return !$order->closed;
    }
    
    public function creditOrder() {
        $orderId = $_POST['value'];
        $this->getApi()->getOrderManager()->creditOrder($orderId);
    }
    
    public function updateOrderCount() {
        $count = $_POST['count'];
        $orderId = $_POST['value'];
        $order = $this->getApi()->getOrderManager()->getOrder($orderId);
        if($order->isCreditNote && $count > 0) {
            $count *= -1;
        }
        $this->getApi()->getOrderManager()->updateCountForOrderLine($_POST['cartItemId'], $orderId, $count);
    }
    
    public function addItemToOrder() {
        $orderID =  $_POST['value'];
        $productId =  $_POST['productId'];
        $this->getApi()->getOrderManager()->addProductToOrder($orderID, $productId, 1);
    }
    
    public function updateOrderLine() {
        $price = $_POST['price'];
        if($price < 0) {
            $price *= -1;
        }
        $this->getApi()->getOrderManager()->updatePriceForOrderLine($_POST['cartItemId'], $_POST['value'], $price);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['value']);
    }
    
    public function changePaymentType() {
        $this->getApi()->getOrderManager()->changeOrderType($_POST['value'], $_POST['newPayemntIdType']);
    }
    
    public function markOrderAsPaid() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['value']);
        $order->status = 7;
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function resendToAccounting() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['value']);
        $order->transferedToAccountingSystem = false;
        $order->transferredToAccountingSystem = false;
        $this->getApi()->getOrderManager()->saveOrder($order);
    }

    public function loadData() {
        if (!$this->filteredData) {
            $this->filteredData = $this->getApi()->getOrderManager()->getOrdersFiltered($this->createFilter());
        }  
    }
    
    public function getPriceWithTaxSpecifed($price, $product) {
        $cartManager = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
        return $cartManager->getPriceWithTaxSpecifed($price, $product);
    }
    
    /**
     * 
     * @return \core_ordermanager_data_Order
     */
    public function getCurrentOrder() {
        return $this->order;
    }
    
}

?>

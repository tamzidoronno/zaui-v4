<?php

namespace ns_27716a58_0749_4601_a1bc_051a43a16d14;

class OrderManager extends GSTableCommon implements \Application {


    public function getDescription() {
        
    }

    public function getName() {
        return $this->__f("OrderManager");
    }

    public function postProcess() {
        
    }
    
    public function updateInvoiceInformation() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['orderId']);
        $order->invoiceNote = $_POST['text'];
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function renderConfig() {
        $this->includefile("orderoverview");
    }
    
    public function renderDashBoardWidget() {
        $this->includefile("dashboardwidget");
    }
    
    public function getDashboardChart($year) {
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
        if($order->transferredToAccountingSystem) {
            return false;
        }
        if($order->status == 7) {
            return false;
        }
        
        return true;
    }
    
    public function creditOrder() {
        $orderId = $_POST['value'];
        $this->getApi()->getOrderManager()->creditOrder($orderId);
    }
    
    public function updateOrderCount() {
        $this->getApi()->getOrderManager()->updateCountForOrderLine($_POST['cartItemId'], $_POST['value'], $_POST['count']);
    }
    
    public function addItemToOrder() {
        $orderID =  $_POST['value'];
        $productId =  $_POST['productId'];
        $this->getApi()->getOrderManager()->addProductToOrder($orderID, $productId, 1);
    }
    
    public function updateOrderLine() {
        $this->getApi()->getOrderManager()->updatePriceForOrderLine($_POST['cartItemId'], $_POST['value'], $_POST['price']);
    }
    
    public function changePaymentType() {
        $this->getApi()->getOrderManager()->changeOrderType($_POST['value'], $_POST['newPayemntIdType']);
    }
    
    public function markOrderAsPaid() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['value']);
        $order->status = 7;
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

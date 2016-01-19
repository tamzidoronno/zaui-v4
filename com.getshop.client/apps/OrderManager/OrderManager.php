<?php

namespace ns_27716a58_0749_4601_a1bc_051a43a16d14;

class OrderManager extends \WebshopApplication implements \Application {
    /**
     *
     * @var \core_common_FilteredData
     */
    private $ordersData = null;
    
    public $totalPageCount = false;

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

    public function loadData() {
        if (!$this->ordersData) {
            $this->ordersData = $this->getApi()->getOrderManager()->getOrdersFiltered($this->createFilter());
        }
        
    }
    
    public function getOrders() {
        $this->loadData();
        return $this->ordersData->datas;
    }
    
    public function getPageNumber() {
        if (!isset($_SESSION['gss_orders_currentPageNumber'])) {
            return 1;
        }
        
        return $_SESSION['gss_orders_currentPageNumber'];   
    }
    
    public function nextOrderPage() {
        $pageNumber = $this->getPageNumber();
        $pageNumber++;
        $_SESSION['gss_orders_currentPageNumber'] = $pageNumber;
    }
    
    public function prevOrderPage() {
        $pageNumber = $this->getPageNumber();
        $pageNumber--;
        if ($pageNumber < 1) {
            $pageNumber = 1;
        }
        $_SESSION['gss_orders_currentPageNumber'] = $pageNumber;
    }
    
    public function hasPrevPage() {
        return $this->getPageNumber() > 1;
    }
    
    public function hasNextPage() {
        if ($this->getPageNumber() < $this->getTotalPageCount()) {
            return true;
        }
        return false;
    }

    public function getPageSize() {
        if (isset($_SESSION['current_pagesize']))
            return $_SESSION['current_pagesize'];
        
        return 30;
    }

    public function getTotalPageCount() {
        $this->loadData();
        return $this->ordersData->totalPages;
    }

    public function filterOrders() {
        $_SESSION['gss_orders_currentPageNumber'] = 1;
        $_SESSION['gss_orders_filter'] = $_POST['order_filter'];
        $_SESSION['gss_orders_startdate'] = $_POST['start_date'];
        $_SESSION['gss_orders_enddate'] = $_POST['end_date'];
        $_SESSION['gss_orders_status'] = $_POST['status'];
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

    public function createFilter() {
        $filter = new \core_common_FilterOptions();
        $filter->searchWord = isset($_SESSION['gss_orders_filter']) ? $_SESSION['gss_orders_filter'] : "";
        $filter->pageSize = $this->getPageSize();
        $filter->pageNumber = $this->getPageNumber();
        
        if (isset($_SESSION['gss_orders_startdate']) && $_SESSION['gss_orders_startdate']) {
            $filter->startDate = $this->convertToJavaDate(strtotime($_SESSION['gss_orders_startdate']));
        }
        
        if (isset($_SESSION['gss_orders_enddate']) && $_SESSION['gss_orders_enddate']) {
            $filter->endDate = $this->convertToJavaDate(strtotime($_SESSION['gss_orders_enddate']));
        }
        
        if (isset($_SESSION['gss_orders_status']) && $_SESSION['gss_orders_status']) {
            $filter->extra["orderstatus"] = $_SESSION['gss_orders_status'];
        }
        
        
        
        return $filter;
    }
    
    public function setPageSize() {
        $_SESSION['current_pagesize'] = $_POST['value'];
    }

    public function setCurrentOrder($order) {
        $this->order = $order;
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

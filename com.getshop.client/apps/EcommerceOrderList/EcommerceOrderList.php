<?php
namespace ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d;

class EcommerceOrderList extends \MarketingApplication implements \Application {
    private $selectedOrder;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "EcommerceOrderList";
    }

    public function render() {
        $this->printTable();
    }
    
    public function formatUser($order) { 
        $user = $this->getApi()->getUserManager()->getUserById($order->userId);
        if (!$user) {
            return "N/A";
        }
        
        return $user->fullName;
    }
    
    public function formatIncTaxes($order) {
        return \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::formatPrice($this->getApi()->getOrderManager()->getTotalAmount($order));
    }
    
    public function formatExTaxes($order) {
        return \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::formatPrice($this->getApi()->getOrderManager()->getTotalAmountExTaxes($order));
    }
    
    public function formatPaymentDate($order) {
        if ($order->paymentDate) {
            return \GetShopModuleTable::formatDate($order->paymentDate);
        }
        
        return "N/A";
    }
    
    public function formatTransferredToAccounting($order) {
        if ($order->transferredToAccountingSystem) {
            return "<i class='fa fa-check'></i>";
        } else {
            return "<i class='fa fa-close'></i>";
        }
    }
    
    public function formatState($order) {
        if ($order->closed) {
            return '<i class="fa fa-lock"></i>';
        }
        
        return "";
    }
    
    public function formatRowCreatedDate($order) {
        return \GetShopModuleTable::formatDate($order->rowCreatedDate);
    }
    
    public function OrderManager_getOrders() {
        $app = new \ns_bce90759_5488_442b_b46c_a6585f353cfe\EcommerceOrderView();
        $app->loadOrder($_POST['data']['id']);
        $app->renderApplication(true, $this);
    }

    public function printTable() {
        $args = array(null, 1, 20);
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('incrementOrderId', 'ORDER ID', 'incrementOrderId'),
            array('rowCreatedDate', 'CREATED', 'rowCreatedDate', 'formatRowCreatedDate'),
            array('paymentDate', 'PAYMENT DATE', null, 'formatPaymentDate'),
            array('transferredToAccounting', '<span title="Transferred to accounting">TFA</span>', null, 'formatTransferredToAccounting'),
            array('user', 'CUSTOMER', null, 'formatUser'),
            array('inctaxes', 'INC TAXES', null, 'formatIncTaxes'),
            array('extaxes', 'EX TAXES', null, 'formatExTaxes'),
            array('state', 'STATE', null, 'formatState')
        );
        
        $table = new \GetShopModuleTable($this, 'OrderManager', 'getOrders', $args, $attributes);
        $table->render();
    }


    /**
     * 
     * @return \core_ordermanager_data_Order 
     */
    public function getSelectedOrder() {
        $this->setData();
        return $this->selectedOrder;
    }

    public function setData($force=false) {
        if (!$this->selectedOrder || $force) {
            $this->selectedOrder = $this->getApi()->getOrderManager()->getOrder($_POST['data']['id']);
        }
    }
}
?>

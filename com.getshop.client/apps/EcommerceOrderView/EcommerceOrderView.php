<?php
namespace ns_bce90759_5488_442b_b46c_a6585f353cfe;

class EcommerceOrderView extends \MarketingApplication implements \Application {
    private $order;
    public $externalReferenceIds;

    public function getDescription() {
        
    }

    public function getName() {
        return "EcommerceOrderView";
    }

    public function getExternalReferenceIds() {
        return $this->externalReferenceIds;
    }
    
    public function render() {
        $this->setData();
        $this->includefile("overview");
    }
    
    public function updateOrder() {
        $this->setData();
        $order = $this->getOrder();
        
        $order->invoiceNote = $_POST['data']['invoiceNote'];
        
        foreach($order->cart->items as $item) {
            $item->startDate = null;
            if($_POST['data'][$item->cartItemId."_startdate"]) {
                $item->startDate = $this->convertToJavaDate(strtotime($_POST['data'][$item->cartItemId."_startdate"]));
            }
            if($_POST['data'][$item->cartItemId."_enddate"]) {
                $item->endDate = $this->convertToJavaDate(strtotime($_POST['data'][$item->cartItemId."_enddate"]));
            }
            $item->product->name = $_POST['data'][$item->cartItemId."_productname"];
            $item->product->additionalMetaData = $_POST['data'][$item->cartItemId."_additionalMetaData"];
            $item->product->metaData = $_POST['data'][$item->cartItemId."_metaData"];
            $item->product->price = $_POST['data'][$item->cartItemId."_price"];
            $item->count = $_POST['data'][$item->cartItemId."_count"];
        }
        
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->order = $this->getApi()->getOrderManager()->getOrder($order->id);
    }
    
    public function setData() {
        if ($this->getModalVariable("orderid")) {
            $_SESSION['EcommerceOrderView_current_order'] = $this->getModalVariable("orderid");
        }
        if (isset($_SESSION['EcommerceOrderView_current_order'])) {
            $this->order = $this->getApi()->getOrderManager()->getOrder($_SESSION['EcommerceOrderView_current_order']);
        }
    }
    
    public function subMenuChanged() {
        $_SESSION['EcommerceOrderView_current_tab'] = $_POST['data']['selectedTab'];
    }

    public function loadOrder($orderId) {
        $_SESSION['EcommerceOrderView_current_order'] = $orderId;
        $this->setData();
    }
    
    public function markForResending() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['id']);
        $order->transferredToAccountingSystem = false;
        $order->triedTransferredToAccountingSystem  = false;
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function directTransferOrder() {
        $errors = $this->getApi()->getGetShopAccountingManager()->transferDirect($_POST['data']['id']);
        if (count($errors)) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "<b>".$this->__f("Could not transfer the item due to the following reasons").":</b>";
            foreach ($errors as $error) {
                $obj->fields->errorMessage .= "<br/>$error";
            }
            $this->doError($obj); // Code will stop here.
        }
    }
    
    /**
     * 
     * @return \core_ordermanager_data_Order 
     */
    public function getOrder() {
        return $this->order;
    }
    
    
    public function isTabActive($tabName) {
        if (isset($_SESSION['EcommerceOrderView_current_tab'])) {
            return ($tabName == $_SESSION['EcommerceOrderView_current_tab']) ? "active" : false;
        }
        
        if ($tabName == "overview") {
            return "active";
        }
        
        return false;
    }
    
    public function test() {
        
    }
    
    public function deleteOrder() {
        $this->setData();
        $order = $this->getOrder();
        $this->getApi()->getOrderManager()->deleteOrder($order->id);
        if ($this->getSelectedMultilevelDomainName()) {
            $this->getApi()->getPmsManager()->orderChanged($this->getSelectedMultilevelDomainName(), $order->id);
        }
    }

    public function getAvailablePaymentMethods() {
        $paymentApps = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        $instances = array();
        foreach ($paymentApps as $app) {
            $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
            $instances[] = $instance;
        }
        return $instances;
    }

    public function changePaymentMethod() {
        $this->setData();
        $order = $this->getOrder();
        $this->getApi()->getOrderManager()->changeOrderType($order->id, $_POST['data']['paymentappid']);
    }
}
?>

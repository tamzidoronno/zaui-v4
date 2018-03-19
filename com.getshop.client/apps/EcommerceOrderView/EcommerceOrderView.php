<?php
namespace ns_bce90759_5488_442b_b46c_a6585f353cfe;

class EcommerceOrderView extends \MarketingApplication implements \Application {
    private $order;
    public $externalReferenceIds;

    public function getDescription() {
        
    }
    
    public function saveSpecialCartItem() {
        $orderid = $_POST['data']['orderid'];
        $itemid = $_POST['data']['itemid'];
        $order = $this->getApi()->getOrderManager()->getOrder($orderid);
        $items = (array)$order->cart->items;
        $count = 0;
        $amount = 0;
        foreach($items as $item) {
            if($item->cartItemId != $itemid) {
                continue;
            }
            $itemsAdded = (array)$item->itemsAdded;
            $priceMatrix = (array)$item->priceMatrix;
            if(sizeof($priceMatrix) > 0) {
                foreach($priceMatrix as $day => $value) {
                    $priceMatrix[$day] = $_POST['data']['pricematrix_'.$day];
                    $amount += $priceMatrix[$day];
                    $count++;
                }
            }
            if(sizeof($itemsAdded) > 0) {
                foreach($itemsAdded as $addon) {
                    $addon->count = $_POST['data']['addoncount_'.$addon->addonId];
                    $addon->price = $_POST['data']['addonprice_'.$addon->addonId];
                    $count += $addon->count;
                    $amount += $addon->count * $addon->price;
                }
            }
            
            $finalamount = ($amount/$count);
            if($finalamount < 0) {
                $finalamount *= -1;
                if($count > 0) {
                    $count *= -1;
                }
            }
            $result = array();
            $result['count'] = $count;
            $result['price'] = $finalamount;
            
            $item->count = $count;
            $item->product->price = $finalamount;
            
            $this->getApi()->getOrderManager()->saveOrder($order);
            
            
            echo json_encode($result);
            break;
        }
    }
    
    public function displaySpecialDataOnItem() {
        $this->includefile("specialeditonitem");
    }

    public function markAsPaid() {
        $this->getApi()->getOrderManager()->markAsPaid($_POST['data']['orderid'], $this->convertToJavaDate(strtotime($_POST['data']['date'])));
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
    
    public function removeCartItem() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $itemlist = array();
        foreach($order->cart->items as $item) {
            if($item->cartItemId == $_POST['data']['itemid']) {
                continue;
            }
            $itemlist[] = $item;
        }
        $order->cart->items = $itemlist;
        $this->getApi()->getOrderManager()->saveOrder($order);
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

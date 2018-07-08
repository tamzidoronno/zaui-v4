<?php
namespace ns_bce90759_5488_442b_b46c_a6585f353cfe;

class EcommerceOrderView extends \MarketingApplication implements \Application {
    private $order;
    public $externalReferenceIds;

    public function getDescription() {
        
    }
    
    public function additemtoorder() {
        $count = 1;
        $orderId = $_POST['data']['orderid'];
        $productId = $_POST['data']['productid'];
        $this->getApi()->getOrderManager()->addProductToOrder($orderId, $productId, $count);
        $this->setData();
        $this->printOrderLines();
    }
    
    public function saveInvoiceNote() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->invoiceNote = $_POST['data']['invoiceNote'];
        $this->getApi()->getOrderManager()->saveOrder($order);
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
        $amount = $_POST['data']['amount'];
        if(!$amount) {
            $amount = 0.0;
        } else {
            $amount = str_replace(",",".", $amount);
        }
        $this->getApi()->getOrderManager()->markAsPaid($_POST['data']['orderid'], $this->convertToJavaDate(strtotime($_POST['data']['date'])), $amount);
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
    public function refreshOrderLines() {
        $this->setData();
        $this->printOrderLines();
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
     * @param \core_usermanager_data_User $user
     */
    public function saveUser($user) {
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function changeUser($user) {
        
        $this->setData();
        $order = $this->getOrder();
        $order->userId = $user->id;
        $order->cart->address = $user->address;
        $order->cart->address->fullName = $user->fullName;
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user->address = new \core_usermanager_data_Address();
        $user = $this->getApi()->getUserManager()->createUser($user);
        
        $this->setData();
        $order = $this->getOrder();
        $order->userId = $user->id;
        $order->cart->address = $user->address;
        $order->cart->address->fullName = $user->fullName;
        $this->getApi()->getOrderManager()->saveOrder($order);
        return $user;
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $user = $this->getApi()->getUserManager()->createCompany($vat, $name);
        
        $this->setData();
        $order = $this->getOrder();
        $order->userId = $user->id;
        $order->cart->address = $user->address;
        $order->cart->address->fullName = $user->fullName;
        $this->getApi()->getOrderManager()->saveOrder($order);
        return $user;
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

    public function printOrderLines() {
        $order = $this->getOrder();
        if ($order->closed) {
            $this->includefile("closedorderlines");
        } else {
            $this->includefile("openorderlines");
        }
    }

}
?>

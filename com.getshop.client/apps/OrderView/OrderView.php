<?php
namespace ns_4be8e427_bead_491e_8d9f_7dd16356d8eb;

class OrderView extends \MarketingApplication implements \Application {
    
    /**
     *
     * @var \core_ordermanager_data_Order 
     */
    private $order = null;
    
    public function getDescription() {
        
    }

    public function updateCurrencyOnOrder() {
        $order = $this->getOrder();
        $order->currency = $_POST['data']['currency'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->rePrintTab('orderlines');
    }
    
    public function updateLanguageOnOrder() {
        $order = $this->getOrder();
        $order->language = $_POST['data']['language'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->rePrintTab('orderlines');
    }
    
    public function getName() {
        return "OrderView";
    }
    
    
    public function saveInternalCommentOnOrder() {
        $this->setOrder();
        $order = $this->getOrder();
        $order->internalComment = $_POST['data']['ordercomment'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->rePrintTab("history");
    }
    
    public function creditOrder() {
        $this->setOrder();
        $this->getApi()->getOrderManager()->creditOrder($this->getOrder()->id);
        $this->order = $this->getApi()->getOrderManager()->getOrder($this->getOrder()->id);
        $this->render();
    }
    
    public function deleteOrder() {
        $this->setOrder();
        $this->getApi()->getOrderManager()->deleteOrder($this->getOrder()->id);
        $this->order = $this->getApi()->getOrderManager()->getOrder($this->getOrder()->id);
        $this->render();
        die();
    }

    public function render() {
        $this->setOrder();
        if ($this->getOrder()->virtuallyDeleted) {
            echo "<div style='text-align: center; font-size: 20px; padding: 30px;'>This order has been deleted</div>";
            return;
        }
        
        $orderId = $this->getOrder()->id;
        
        $typenamespace = explode('\\', $this->getOrder()->payment->paymentType);
        $paymentType = $this->getPaymentMethodName($typenamespace[0]);
        
        ?>
        <div class='workareaheader'>
            <div class="headertitle">
                <? echo $this->__f("Order id") . ": " . $this->getOrder()->incrementOrderId; ?> ( <? echo $paymentType; ?> )
            </div>
            
            <div class="actions">
                <?
                $this->includefile("actions");
                ?>
            </div>
        </div>
        <?
        
        $this->includefile("headerwarnings");
        
        echo "<div class='orderview' orderid='$orderId'>";
            echo "<div class='leftmenu'>";
                $this->includefile("leftmenu");
            echo "</div>";

            echo "<div class='workarea'>";
                $this->includefile("workarea");
            echo "</div>";
        echo "</div>";
        ?>
        <script>
            app.OrderView.orderviewLoaded('<? echo $orderId; ?>');
        </script>
        <?
        
        if (isset($_GET['tab'])) {
            ?>
            <script>
                app.OrderView.showTab('<? echo $this->getOrder()->id; ?>', '<? echo $_GET['tab']; ?>')
            </script>
            <?
        }
    }

    public function setOrder() {
        $orderid = null;
        
        if (isset($_GET['orderid'])) {
            $orderid = $_GET['orderid'];
        }
        
        if (isset($_POST['data']['orderid'])) {
            $orderid = $_POST['data']['orderid'];
        }
        
        if (!$this->order) {
            $this->order = $this->getApi()->getOrderManager()->getOrder($orderid);
        }
    }
    
    public function addTransactionRecord() {
        $order = $this->getOrder();
        $date = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $amountInLocalCurrency = isset($_POST['data']['localCurrency']) ? $_POST['data']['localCurrency'] : null;
        $agio = isset($_POST['data']['agio']) ? $_POST['data']['agio'] : null;
        $accountingDetailId = isset($_POST['data']['type']) ? $_POST['data']['type'] : "";
        
        $this->getApi()->getOrderManager()->addOrderTransaction($order->id, $_POST['data']['amount'], $_POST['data']['comment'], $date, $amountInLocalCurrency, $agio, $accountingDetailId);
        $this->rePrintTab("paymenthistory");
    }
    
    /**
     * @return \core_ordermanager_data_Order
     */
    public function getOrder() {
        $this->setOrder();
        return $this->order;
    }
    
    public function getEhfProblems($order, $user) {
        $ret = array();
  
        if (!$user->fullName) {
            $ret[] = "The name of the customer can not be blank";
        }
        
        if (!$user->address || !$user->address->address) {
            $ret[] = "Street address of the customer can not be blank";
        }
        
        if (!$user->address || !$user->address->city) {
            $ret[] = "City of the customer address can not be blank";
        }
        
        if (!$user->address || !$user->address->postCode) {
            $ret[] = "Postcode of the customer address can not be blank";
        }
        
        
        if (!$user->companyObject || !$user->companyObject->name) {
            $ret[] = "Customer company name can not be blank";
        }
        
        return $ret;
    }
    
    /**
     * 
     * @return \core_cartmanager_data_CartItem[]
     */
    public function getPmsCartItems() {
        $order = $this->getOrder();
        $ret = array();
        
        foreach ($order->cart->items as $item) {
            if ($item->priceMatrix != null && count((array)$item->priceMatrix) > 0) {
                $ret[] = $item;
            }
            
            if ($item->itemsAdded != null && count((array)$item->itemsAdded) > 0) {
                $ret[] = $item;
            }
        }
        
        return $ret;
    }
    
    private function gen_uuid() {
        return sprintf( '%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
            // 32 bits for "time_low"
            mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ),

            // 16 bits for "time_mid"
            mt_rand( 0, 0xffff ),

            // 16 bits for "time_hi_and_version",
            // four most significant bits holds version number 4
            mt_rand( 0, 0x0fff ) | 0x4000,

            // 16 bits, 8 bits for "clk_seq_hi_res",
            // 8 bits for "clk_seq_low",
            // two most significant bits holds zero and one for variant DCE1.1
            mt_rand( 0, 0x3fff ) | 0x8000,

            // 48 bits for "node"
            mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff )
        );
    }
    
    /**
     * 
     * @return \core_cartmanager_data_CartItem[]
     */
    public function getNormalCartItems() {
        $order = $this->getOrder();
        $ret = array();
        foreach ($order->cart->items as $item) {
            if ($item->priceMatrix != null && count((array)$item->priceMatrix) > 0) {
                continue;
            }
            
            if ($item->itemsAdded != null && count((array)$item->itemsAdded) > 0) {
                continue;
            }
            
            $ret[] = $item;
        }
        
        return $ret;
    }

    public function addNewCartItemLine() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productIdToAdd']);
        $order = $this->getOrder();
        $cartItem = new \core_cartmanager_data_CartItem();
        $cartItem->cartItemId = $this->gen_uuid();
        $cartItem->product = $product;
        $cartItem->count = 1;
        $order->cart->items[] = $cartItem;
        $this->getApi()->getOrderManager()->saveOrder($order);
        
        $this->rePrintTab('orderlines');
    }

    public function clearCachedOrderObject() {
        $this->order = null;
    }

    public function removeCartItemLine() {
        $order = $this->getOrder();
        $itemsToKeep = array();
        foreach ($order->cart->items as $item) {
            if ($item->cartItemId != $_POST['data']['cartItemId']) {
                $itemsToKeep[] = $item;
            }
        }
        
        $order->cart->items = $itemsToKeep;
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->rePrintTab('orderlines');
        
    }

    public function rePrintTab($filename="") {
        if (isset($_POST['data']['tabName']) && !$filename) {
            $filename = $_POST['data']['tabName'];
        }
        
        $this->clearCachedOrderObject();
        $this->includefile($filename);
        die();
    }
    
    public function searchForProduct() {
        $this->includefile("productsearchresult");
        die();
    }
    
    public function changeProductOnCartItem() {
        $order = $this->getOrder();
        $this->getApi()->getOrderManager()->changeProductOnCartItem($order->id, $_POST['data']['cartItemId'], $_POST['data']['productId']);
        $this->rePrintTab('orderlines');
    }

    public function updateCartItem() {
        $order = $this->getOrder();
        
        $itemFound = false;
        
        foreach ($order->cart->items as $item) {
            if ($item->cartItemId == $_POST['data']['cartItemId']) {
                $itemFound = $item;
            }
        }
        
        if (!$itemFound)
            return;
        
        $itemFound->count = $_POST['data']['count'];
        $itemFound->product->price = $_POST['data']['price'];
        $itemFound->product->name = $_POST['data']['productDescription'];
        $itemFound->product->description  = $_POST['data']['productDescription'];
        $itemFound->product->taxgroup = $_POST['data']['taxGroup'];
        
        $this->getApi()->getOrderManager()->updateCartItemOnOrder($order->id, $itemFound);
        
        $this->rePrintTab("orderlines");
    }
    
    public function getPaymentMethodName($paymentNameSpaceId) {
        if (isset($this->cachedPaymentNames[$paymentNameSpaceId])) {
            return $this->cachedPaymentNames[$paymentNameSpaceId];
        }
        
        $paymentAppId = str_replace("ns_", "", $paymentNameSpaceId);
        $paymentAppId = str_replace("_", "-", $paymentAppId);
        
        
        $app = $this->getApi()->getStoreApplicationPool()->getApplication($paymentAppId);
        $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
        $name = $instance->getName();
        $this->cachedPaymentNames[$paymentNameSpaceId] = $name;
        return $name;
    }
    
    public function changeOverrideDate() {
        $order = $this->getOrder();
        $order->overrideAccountingDate = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->rePrintTab("accounting");
    }
    
    public function disableGsTypes() {
        return true;
    }
    
    public function sendByEmail() {
        $subject = "Invoice attached";
        $body = "Attached is the invoice for your order";
        $this->getApi()->getOrderManager()->sendRecieptWithText($this->getOrder()->id, $_POST['data']['emailaddress'], $subject, $body);
        $this->rePrintTab('history');
    }
    
    public function sendEhf() {
        $orderList = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
        
        $this->setOrder();
        $orderid = $this->getOrder()->id;
        $xml = $this->getApi()->getOrderManager()->getEhfXml($orderid);
        if ($xml == "failed") {
            echo "<span style='color: red'><i class='fa fa-warning'></i> Something wrong happend while creating EHF invoice, GetShop has been notified and will contact you once its sorted out.</span>";
            return;
        }
        
        $res = $orderList->sendDocument($xml);
        if ($res[0] != "ok") {
            echo "<span style='color: red'><i class='fa fa-warning'></i> Something went wrong during sending EHF, please contact GetShop Support.</span>";
            return;
        }
        
        $this->getApi()->getOrderManager()->registerSentEhf($orderid);
        $this->getApi()->getOrderManager()->closeOrder($orderid, "Invoice sent by EHF to customer.");
        echo "<span style='color: green'><i class='fa fa-check'></i> EHF Sent successfully</span>";
    }
    
    public function saveUser($user) {
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function changeUser($user) {
        $order = $this->getOrder();
        $order->userId = $user->id;
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->order = $this->getApi()->getOrderManager()->getOrder($order->id);
    }
    
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $createUser = $this->getApi()->getUserManager()->createUser($user);
        
        $order = $this->getOrder();
        $order->userId = $createUser->id;
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->order = $this->getApi()->getOrderManager()->getOrder($order->id);
        
        return $createUser;
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $user = $this->getApi()->getUserManager()->createCompany($vat, $name);
        
        $order = $this->getOrder();
        $order->userId = $user->id;
        $order->cart->address = $user->address;
        $order->cart->address->fullName = $user->fullName;
        $this->getApi()->getOrderManager()->saveOrder($order);
        return $user;
    }
    
}
?>

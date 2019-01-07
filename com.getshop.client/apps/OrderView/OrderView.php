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

    public function getName() {
        return "OrderView";
    }

    public function render() {
        $this->setOrder();
        
        $orderId = $this->getOrder()->id;
        
        $typenamespace = explode('\\', $this->getOrder()->payment->paymentType);
        $paymentType = $this->getPaymentMethodName($typenamespace[0]);
        
        ?>
        <div class='workareaheader'>
            <? echo $this->__f("Order id") . ": " . $this->getOrder()->incrementOrderId; ?> ( <? echo $paymentType; ?> )
        </div>
        <?
        
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
    
    /**
     * @return \core_ordermanager_data_Order
     */
    public function getOrder() {
        $this->setOrder();
        return $this->order;
    }
    
    /**
     * 
     * @return \core_cartmanager_data_CartItem[]
     */
    public function getPmsCartItems() {
        $order = $this->getOrder();
        $ret = array();
        
        foreach ($order->cart->items as $item) {
            if ($item->priceMatrix != null && count($item->priceMatrix)) {
                $ret[] = $item;
            }
            
            if ($item->itemsAdded != null && count($item->itemsAdded)) {
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
            if ($item->priceMatrix != null && count($item->priceMatrix)) {
                continue;
            }
            
            if ($item->itemsAdded != null && count($item->itemsAdded)) {
                continue;
            }
            
            $ret[] = $item;
        }
        
        return $ret;
    }

    public function addNewCartItemLine() {
        $order = $this->getOrder();
        $cartItem = new \core_cartmanager_data_CartItem();
        $cartItem->cartItemId = $this->gen_uuid();
        
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

    public function rePrintTab($filename) {
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
}
?>

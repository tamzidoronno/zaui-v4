<?php
namespace ns_90d14853_2dd5_4f89_96c1_1fa15a39babd;

class SalesPointCartCheckout extends \MarketingApplication implements \Application {
    private $currentCart = null;
    
    private $readOnly = false;
    
    public function getDescription() { 
    }

    public function getName() {
        return "SalesPointCartCheckout";
    }
    
    public function loadCartFilter() {
        $this->includefile("cartfilter");
    }

    public function render() {
        echo "<div class='checkoutarea'>";
        $this->includefile("checkout");
        echo "</div>";
    }

    public function renderPreview() {
        $this->includefile("preview");
    }

    public function setCurrentItem($item) {
        $this->currentItem = $item;
    }
    
    public static function sortByDate($a, $b) {
        $ad = strtotime($a->date);
        $bd = strtotime($b->date);
        
        if ($ad == $bd) {
            return 0;
        }

        return $ad < $bd ? -1 : 1;
    }
        
    /**
     * @return \core_cartmanager_data_CartItem;
     */
    public function getCurrentItem() {
        return $this->currentItem;
    }

    public function updateCartAndPrice() {
        if (isset($_POST['data']['roomId'])) {
            $this->setRoomCart($_POST['data']['roomId']);
        }
        
        $orginalCart = $this->getOriginalCartFromSession();
        
        $newItemsArray = array();
        foreach ($orginalCart->items as $cartItem) {
            if (isset($cartItem->priceMatrix)) {
                $cartItem->priceMatrix = $this->createNewPriceMatrix($cartItem);
            }
            
            if (isset($cartItem->itemsAdded)) {
                $cartItem->itemsAdded = $this->createAddons($cartItem);
            } 
            
            $newItemsArray[] = $cartItem;
        }
        
        $orginalCart->items = $newItemsArray;
        $orginalCart = $this->getApi()->getCartManager()->recalculateMetaDataCart($orginalCart);
        
        echo $this->getApi()->getCartManager()->getCartTotal($orginalCart);
        $this->saveTmpCart($orginalCart, @$_POST['data']['roomId']);
        $this->getApi()->getOrderManager()->updateOrderUnderConstructionItems($this->getModalVariable("orderUnderConstrcutionId"), $orginalCart);
        die();
    }

    public function setCurrentCart($cart) {
        $this->currentCart = $cart;
    }
    
    public function setOriginalCart($cart = false) {
        $this->originalCart = $this->getApi()->getCartManager()->getCart();
    }

    public function filterCartByDate() {
        $id = $this->getModalVariable("orderUnderConstrcutionId");
        $start = $this->convertToJavaDate(strtotime($_POST['date']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['date']['end']));
        $this->getApi()->getOrderManager()->filterOrdersUnderConstructionByDate($id, $start, $end);
    }
    
    public function removeItem() {
       $this->getApi()->getOrderManager()->removeRoomForOrderUnderConstruction($this->getModalVariable("orderUnderConstrcutionId"), $_POST['data']['id']);
    }
    
    public function createOrder() {
        if(isset($_POST['data']['appendtoorder']) && $_POST['data']['appendtoorder']) {
            $originalCart = $this->getOriginalCartFromSession();
            $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['appendtoorder']);
            foreach($originalCart->items as $item) {
                $order->cart->items[] = $item;
            }
            $this->getApi()->getOrderManager()->saveOrder($order);
        } else {
            $id = $this->getModalVariable("orderUnderConstrcutionId");
            $type = "all";
            if(isset($_POST['data']['paymenttypeselection'])) {
                $type = $_POST['data']['paymenttypeselection'];
            }
            $orderIds = $this->getApi()->getPmsInvoiceManager()->convertCartToOrders($this->getSelectedMultilevelDomainName(), $id, null, $_POST['data']['payment'], $type);
            foreach($orderIds as $orderId) {
                $this->getApi()->getPmsManager()->orderCreated($this->getSelectedMultilevelDomainName(), $orderId);
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                if($type == "uniqueorder" || $type == "uniqueordersendpaymentlink") {
                    $room = $this->getRoom($orderId, $order);
                    foreach($room->guests as $guest) {
                        if($type == "uniqueordersendpaymentlink" && ($guest->phone || $guest->email)) {
                            $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedMultilevelDomainName(), 
                                $orderId, 
                                $room->pmsBookingRoomId, 
                                $guest->email,
                                $guest->prefix,
                                $guest->phone);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @return \core_cartmanager_data_Cart
     */
    public function getOriginalCartFromSession() {
        if (!isset($this->originalCart)) {
            $this->setOriginalCart();
        }
        return $this->originalCart;
    }

    public function getCartItemFromCart($cart, $id) {
        foreach ($cart->items as $cartItem) {
            if ($cartItem->cartItemId === $id) {
                return $cartItem;
            }
        }
        
        return null;
    }

    public function createNewPriceMatrix($cartItemToCheck) {
        $retMatrix = new \stdClass();
        $postedCartItem = $this->getPostedCartItem($cartItemToCheck->cartItemId);
        
        foreach ($postedCartItem['priceMatrix'] as $priceMatrix) {
            if ($priceMatrix['enabled'] === "true") {
                $retMatrix->{$priceMatrix['date']} = $priceMatrix['value'];
            } else {
                $retMatrix->{$priceMatrix['date']} = 0;
            }
        }
        
        return $retMatrix;
    }


    public function createAddons($originalCartItem) {
        $postedItem = $this->getPostedCartItem($originalCartItem->cartItemId);
        $postedAddons = $postedItem['addons'];
        $items = $originalCartItem->itemsAdded;
        $retAddons = array();
        
        foreach ($postedAddons as $postedAddon) {
            
            
            $addonItem = null;
            
            foreach ($items as $itemAdded) {
                if ($itemAdded->addonId == $postedAddon['id']) {
                    $addonItem = $itemAdded;
                }
            }    
            
            if (!$addonItem) {
                continue;
            }
            
            $addonItem->price = $postedAddon['price'];
            $addonItem->count = $postedAddon['count'];
            
            if ($postedAddon['enabled'] !== "true") {
                $addonItem->price = 0;
            }
            
            $retAddons[] = $addonItem;
        }
        
        return $retAddons; 
    }

    public function addCurrentCartForPaymentProcess($pmsRoomId) {
        if (isset($_SESSION['payment_process_cart__tmp'])) {
            $_SESSION['payment_process_cart_'.$pmsRoomId] = $_SESSION['payment_process_cart__tmp'];
            unset($_SESSION['payment_process_cart__tmp']);
        } else if (isset($_SESSION['payment_process_cart_'.$pmsRoomId."_tmp"])) {
            $_SESSION['payment_process_cart_'.$pmsRoomId] = $_SESSION['payment_process_cart_'.$pmsRoomId."_tmp"];
            unset($_SESSION['payment_process_cart_'.$pmsRoomId."_tmp"]);
        } else {
            $cart = $this->getApi()->getCartManager()->getCart();
            $_SESSION['payment_process_cart_'.$pmsRoomId] = json_encode($cart);
        }
        
    }

    public function cancelPaymentProcessForRoom($roomId) {
        unset($_SESSION['payment_process_cart_'.$roomId]);
    }

    public function cancelAllPaymentProcesses() {
        foreach ($_SESSION as $key => $val) {
            if (strstr($key, "payment_process_cart") > -1) {
                unset($_SESSION[$key]);
            }
        }
    }

    public function getCurrentCart() {
        if (isset($this->currentCart) && $this->currentCart) {
            return $this->currentCart;
        }
        
        return $this->getApi()->getCartManager()->getCart();
    }

    public function setRoomCart($pmsRoomId) {
        if (isset($_SESSION['payment_process_cart_'.$pmsRoomId])) {
            $cart = json_decode($_SESSION['payment_process_cart_'.$pmsRoomId]);
            $this->setOriginalCart($cart);
            $this->setCurrentCart($cart);
            $this->getApi()->getCartManager()->setCart($cart);
        }
        
    }

    public function getPostedCartItem($id) {
        $items = $_POST['data']['cartItems'];
        foreach ($items as $item) {
            
            if ($item['id'] == $id) {
                return $item;
            }
        }
        
        return null;
    }

    public function saveTmpCart($cart, $roomId) {
        $key = 'payment_process_cart_'.$roomId."_tmp";
        $_SESSION[$key] = json_encode($cart);
    }
    
    public function getTmpCartForRoom($roomId) {
        $key = 'payment_process_cart_'.$roomId."_tmp";
        
        if (isset($_SESSION[$key])) {
            $cart = json_decode($_SESSION[$key]);
            unset($_SESSION[$key]);
            return $cart;
        }
        
        return null;
    }
    
    public function setReadOnly() {
        $this->readOnly = true;
    }

    public function isReadOnly() {
        return $this->readOnly;
    }

    /**
     * 
     * @param type $orderId
     * @param type $order
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getRoom($orderId, $order) {
        $roomId = "";
        foreach($order->cart->items as $item) {
            $roomId = $item->product->externalReferenceId;
            break;
        }
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomId);
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $roomId) {
                return $room;
            }
        }
        return;
    }

}
?>

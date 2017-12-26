<?php
namespace ns_90d14853_2dd5_4f89_96c1_1fa15a39babd;

class SalesPointCartCheckout extends \MarketingApplication implements \Application {
    private $currentCart = null;
    
    public function getDescription() { 
    }

    public function getName() {
        return "SalesPointCartCheckout";
    }

    public function render() {
        $this->includefile("checkout");
    }

    public function renderPreview() {
        $this->includefile("preview");
    }

    public function setCurrentItem($item) {
        $this->currentItem = $item;
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
        die();
    }

    public function setCurrentCart($cart) {
        $this->currentCart = $cart;
    }
    
    public function setOriginalCart($cart = false) {
        if ($cart) {
            $this->originalCart = $cart;
        } else {
            $this->originalCart = $this->getApi()->getCartManager()->getCart();
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

}
?>

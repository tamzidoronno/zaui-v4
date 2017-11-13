<?php
namespace ns_90d14853_2dd5_4f89_96c1_1fa15a39babd;

class SalesPointCartCheckout extends \MarketingApplication implements \Application {
    
    public function getDescription() { 
    }

    public function getName() {
        return "SalesPointCartCheckout";
    }

    public function render() {
        $this->setOriginalCart();
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
        $cartItems = $_POST['data']['cartItems'];
        $orginalCart = $this->getOriginalCartFromSession();
        
        $newCartItems = array();
        
        foreach ($cartItems as $cartItem) {
            $originalCartItem = $this->getCartItemFromCart($orginalCart, $cartItem['id']);
            
            if (isset($cartItem['priceMatrix'])) {
                $originalCartItem->priceMatrix = $this->createNewPriceMatrix($cartItem);
            }
            
            if (isset($cartItem['addons'])) {
                $originalCartItem->itemsAdded = $this->createAddons($cartItem['addons'], $originalCartItem);
            }
            
            $this->getApi()->getCartManager()->updateCartItem($originalCartItem);
        }
        
        $this->getApi()->getCartManager()->recalculateMetaData();
        
        echo $this->getApi()->getCartManager()->getCartTotalAmount();
        die();
    }

    private function setOriginalCart() {
        $cart = $this->getApi()->getCartManager()->getCart();
        if (!isset($_SESSION['SalesPointCartCheckout_current_cartid'])) {
            $_SESSION['SalesPointCartCheckout_current_cartid'] = json_encode($cart);
        }
    }

    /**
     * 
     * @return \core_cartmanager_data_Cart
     */
    public function getOriginalCartFromSession() {
        if (!isset($_SESSION['SalesPointCartCheckout_current_cartid'])) {
            return null;
        }
        
        return json_decode($_SESSION['SalesPointCartCheckout_current_cartid']);
    }

    public function getCartItemFromCart($cart, $id) {
        foreach ($cart->items as $cartItem) {
            if ($cartItem->cartItemId === $id) {
                return $cartItem;
            }
        }
        
        return null;
    }

    public function createNewPriceMatrix($postedCartItem) {
        $retMatrix = new \stdClass();
        
        foreach ($postedCartItem['priceMatrix'] as $priceMatrix) {
            if ($priceMatrix['enabled'] === "true") {
                $retMatrix->{$priceMatrix['date']} = $priceMatrix['value'];
            }
        }
        
        return $retMatrix;
    }

    public static function clearCheckout() {
        unset($_SESSION['SalesPointCartCheckout_current_cartid']);
    }

    public function createAddons($postedAddons, $originalCartItem) {
        $items = $originalCartItem->itemsAdded;
        $retAddons = array();
        
        foreach ($postedAddons as $postedAddon) {
            if ($postedAddon['enabled'] !== "true") {
                continue;
            }
            
            $addonItem = null;
            
            foreach ($items as $itemAdded) {
                if ($itemAdded->addonId == $postedAddon['id']) {
                    $addonItem = $itemAdded;
                }
            }    
            
            if ($addonItem) {
                $addonItem->price = $postedAddon['price'];
                $addonItem->count = $postedAddon['count'];
                $retAddons[] = $addonItem;
            }
        }
        
        return $retAddons;
    }

}
?>

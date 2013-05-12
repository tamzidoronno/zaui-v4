<?php

class HelperCart {

    var $cart;

    function HelperCart($cart) {
        $this->cart = $cart;
    }

    public function getTotal() {
        $price = 0;

        foreach ($this->cart->products as $product) {
            $price += $product->price * $this->getProductCount($product);
        }

        return $price;
    }

    public function getProductCount($product) {
        if (isset($this->cart->counter->{$product->id})) {
            return $this->cart->counter->{$product->id};
        }

        return 0;
    }

    public function getProductList() {
        return $this->cart->products;
    }
    
    public static function clearSession($includeAddress=true) {
        if ($includeAddress) {
            unset($_SESSION['tempaddress']);
        }
        unset($_SESSION['checkoutstep']);
        unset($_SESSION['paymentMethod']);
        unset($_SESSION['shippingtype']);
        unset($_SESSION['shippingproduct']);
    }

}

?>

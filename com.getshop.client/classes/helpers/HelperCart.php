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

}

?>

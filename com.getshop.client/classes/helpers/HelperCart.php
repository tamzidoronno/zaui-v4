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
        
        if (isset($this->cart->counter->{$product->jsonKey})) {
            return $this->cart->counter->{$product->jsonKey};
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

    public static function getVartionsText($product) {
        $variationsprint = array();
        foreach ($product->compKey->variations as $variation) {
            if ($variation == "") {
                continue;
            }

            $object = HelperCart::getVariationObject($product, $variation);
            if ($object) {
                $variationsprint[] = $object->title;
            }
        }
        $translator = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
        return  $translator->__w("Your selected").": ".implode(", ", $variationsprint);
    }
    
    private static function getText($variation, $id) {
        $retobject = null;
        foreach ($variation as $varobject) {
            if ($varobject->id == $id) {
                $retobject = $varobject;
            }
            if ($retobject == null && $varobject->children) {
                $retobject =  HelperCart::getText($varobject->children, $id);
            }
        }
        
        return $retobject;
    }
    
    private static function getVariationObject($product, $variation) {
        return HelperCart::getText($product->variations, $variation);
    }
}

?>

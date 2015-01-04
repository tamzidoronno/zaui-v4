<?php

class HelperCart {

    var $cart;

    function HelperCart($cart) {
        $this->cart = $cart;
    }

    public static function clearSession($includeAddress=true) {
        if ($includeAddress) {
            unset($_SESSION['tempaddress']);
        }
        unset($_SESSION['checkoutstep']);
        unset($_SESSION['appId']);
        unset($_SESSION['shippingtype']);
        unset($_SESSION['cartCustomerId']);
        unset($_SESSION['shippingproduct']);
    }

    public static function getVartionsText($cartItem) {
        if (count(@$cartItem->variations) == 0) {
            return;
        }
        
        $variationsprint = array();
        foreach ($cartItem->variations as $variation) {
            if ($variation == "") {
                continue;
            }

            $object = HelperCart::getVariationObject($cartItem->product, $variation);
            if ($object) {
                $variationsprint[] = $object->title;
            }
        }
        $translator = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
        return  implode(", ", $variationsprint);
    }
    
    private static function getText($variation, $id) {
        $retobject = null;
        foreach ($variation as $varobject) {
            if ($varobject->id == $id) {
                $retobject = $varobject;
            }
            if ($retobject == null && isset($varobject->children) && $varobject->children) {
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

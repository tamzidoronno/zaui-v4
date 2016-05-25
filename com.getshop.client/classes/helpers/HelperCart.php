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
        
        $factory = IocContainer::getFactorySingelton();
        
        $text = "";
        
        
        $arr = (array)$cartItem->variations;
        
        if (!empty($arr)) {
            $text .= " ( ";
            $i = 0;
            foreach ($cartItem->variations as $key => $value) {
                $i++;
                $keyNode = $factory->getApi()->getListManager()->getJSTreeNode($key);
                $valueNode = $factory->getApi()->getListManager()->getJSTreeNode($value);

                if ($keyNode && $valueNode) {
                    $text .= $keyNode->text." ".$valueNode->text.", ";
                }
            }
            
            
            $text = substr($text, 0, -2);
            $text .= ")";
        }
        
        return $text;
    }

    public static function hasVariations($product) {
        $factory = IocContainer::getFactorySingelton();
        $nodelist = $factory->getApi()->getListManager()->getJsTree("variationslist_product_".$product->id);
        $values = $nodelist->nodes && count($nodelist->nodes) > 0;
        return $values;
    }

}
?>
<?php
namespace ns_900e5f6b_4113_46ad_82df_8dafe7872c99;

class Cart {
    private $products = array();
    
    /**
     * @return Cart 
     */
    public static function getCart() {
        $cart = new Cart();
        if (isset($_SESSION['cartproducts'])) {
            $cart->products = unserialize($_SESSION['cartproducts']);
        }
        
        return $cart;
    }

    public function addProduct($productid) {
        $this->products[$productid] = 1; 
        $this->saveToSession();
    }
    
    public function clear() {
        unset($_SESSION['cartproducts']);
    }
    
    private function saveToSession() {
        $_SESSION['cartproducts'] = serialize($this->products);
    }

    public function getProductIds() {
        $ids = array();
        foreach (array_keys($this->products) as $productId) {
            $ids[] = $productId;
        }
        return $ids;
    }

    public function remove($productid) {
        unset($this->products[$productid]);
        $this->saveToSession();
    }
    
    public function getAmount($productid) {
        return $this->products[$productid];
    }
}

?>

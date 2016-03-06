<?php
namespace ns_91b1fd6f_251f_4cd5_993f_96069fd1533e;

class CartHeader extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CartHeader";
    }

    public function render() {
        $this->includefile("cartheader");
    }
    
    public function getCartCount() {
        $cart = $this->getApi()->getCartManager()->getCart();
        $count = 0;
        foreach($cart->items as $item) {
            $count += $item->count;
        }
        echo $count;
    }
}
?>

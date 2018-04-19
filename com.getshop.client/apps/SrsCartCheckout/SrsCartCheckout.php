<?php
namespace ns_74a61004_afeb_4627_b563_9c85b880b6ed;

class SrsCartCheckout extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SrsCartCheckout";
    }

    public function render() {
        if ($this->getModalVariable("reservationid")) {
            $this->getApi()->getResturantManager()->createCartForReservation($this->getModalVariable("reservationid")); 
        }
        
        echo "<div class='checkoutarea'>";
        $this->includefile("checkout");
        echo "</div>";
    }
    
    public function updateCartItem() {
        $cart = $this->getApi()->getCartManager()->getCart();
        foreach ($cart->items as $item) {
            if ($item->cartItemId == $_POST['data']['cartItemId']) {
                $item->product->price = $_POST['data']['price'];
                $item->count = $_POST['data']['count'];
                $this->getApi()->getCartManager()->updateCartItem($item);
                break;
            }
        }
        
        $cart = $this->getApi()->getCartManager()->getCart();
        echo $this->getApi()->getCartManager()->getCartTotal($cart);
        die();
    }
    
    public function doPayment() {
        $order = $this->getApi()->getOrderManager()->createOrder(null);
        $this->getApi()->getOrderManager()->changeOrderType($order->id, $_POST['data']['paymentid']);
    }
}
?>

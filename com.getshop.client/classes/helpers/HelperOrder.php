<?php

class HelperOrder {

    var $order;

    function helperOrder($order) {
        $this->order = $order;
    }

    public function getTotal() {
        $total = 0;
        if (isset($this->order->shipping) && isset($this->order->shipping->cost))
            $total = $this->order->shipping->cost;

        if ($this->order->cart != null) {
            $cart = new \HelperCart($this->order->cart);
            $total += $cart->getTotal();
        }

        return $total;
    }
}
?>

<?php
namespace ns_6e930536_eca4_4742_9712_bf2042c8cf86;

class GuestBill extends \PaymentApplication implements \Application {
    public function getDescription() {
        return $this->__f("If you are uncertain of what payment method the guest will chose, you can use this method to complete the day income");
    }
    public function getIcon() {
        return "invoice.svg";
    }


    public function getName() {
        return "GuestBill";
    }

    public function render() {
        
    }
    
    public function getColor() {
        return "pink";
    }
}
?>

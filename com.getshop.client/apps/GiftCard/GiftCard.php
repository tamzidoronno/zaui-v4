<?php
namespace ns_8650475d_ebc6_4dfb_86c3_eba4a8aba979;

class GiftCard extends \PaymentApplication implements \Application {
    public $paidResult;
    public function getDescription() {
        return $this->__f("Used for collecting payments after you have sold a Gift Card");
    }

    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod($this->getName(), "/showApplicationImages.php?appNamespace=$namespace&image=skin/images/invoice.png", "Invoice");
    }
    
    public function getIcon() {
        return "invoice.svg";
    }

    
    public function getName() {
        return "GiftCard";
    }

    public function findGiftCardPrice() {
        $amount = $_POST['data']['amount'];
        $card = $this->getApi()->getGiftCardManager()->getGiftCard($_POST['data']['code']);
        if($card) {
            echo "<br>";
            if($card->remainingValue < $amount) {
                echo "<b>This giftcard does not have enough funds to complete this order.</b><br><br>";
                echo "<script>$('.submitgiftcard').addClass('disabled');</script>";
            } else {
                echo "<b><i class='fa fa-check'></i> This giftcard is valid</b><br><br>";
                echo "<script>$('.submitgiftcard').removeClass('disabled');</script>";
            }
            
            echo "Card: " . $card->cardCode . "<br>";
            echo "Value: " . $card->giftCardValue . "<br>";
            echo "Remaining: " . $card->remainingValue . "<br>";
            echo "Created: " . date("d.m.Y H:i", strtotime($card->rowCreatedDate)) . "<br>";
        } else {
            echo "<script>$('.submitgiftcard').addClass('disabled');</script>";
        }
    }
    
    public function render() {
        if ($this->getCurrentOrder()->status != 7) {
            $this->includefile("paywithgiftcard");
        }
    }
    
    public function paywithcode() {
        $code = $_POST['data']['code'];
        $orderId = $_POST['data']['orderid'];
        $this->paidResult = $this->getApi()->getGiftCardManager()->payOrderWithGiftCard($code, $orderId);
    }
    
    public function hasPaymentProcess() {
         return ($this->order != null && $this->order->status != 7);
    }
    
    public function isPublicPaymentApp() {
        return true;
    }
}
?>

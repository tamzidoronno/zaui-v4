<?php
namespace ns_d96f955a_0c21_4b1c_97dc_295008ae6e5a;

class OnlinePaymentMethod extends \PaymentApplication implements \Application {
    public $overrideDefault = true;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "OnlinePaymentMethod";
    }

    public function render() {
        $this->includefile("sendpayment");
    }
    
    public function sendPaymentLink() {
        $orderid = $_POST['data']['orderid'];
        $bookingid = $_POST['data']['bookingid'];
        $email = $_POST['data']['bookerEmail'];
        $prefix = $_POST['data']['prefix'];
        $phone = $_POST['data']['phone'];
        $msg = $_POST['data']['emailMessage'];
        $subject = $_POST['data']['subject'];

        $this->getApi()->getPmsManager()->sendPaymentLinkWithText($this->getSelectedMultilevelDomainName(), $orderid, $bookingid, $email, $prefix, $phone, $msg, $subject);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $this->render();
    }
    
    public function updatePaymentLinkText() {
        $booking = $this->getApi()->getPmsManager()->getBookingWithOrderId($this->getSelectedMultilevelDomainName(), $_POST['data']['orderid']);
        $selectedPrefix = $_POST['data']['prefix'];
        $mesage = $this->getApi()->getPmsNotificationManager()->getSpecificMessage($this->getSelectedMultilevelDomainName(), "booking_sendpaymentlink", $booking, null, "sms", $selectedPrefix);
        if(!$mesage) {
            $mesage = $this->getApi()->getPmsNotificationManager()->getSpecificMessage($this->getSelectedMultilevelDomainName(), "booking_sendpaymentlink", $booking, null, "email", $selectedPrefix);
        } 
        $msg = $mesage->content;
        $msg = str_replace("{name}", $_POST['data']['name'], $msg);
        echo $msg;
    }
}
?>

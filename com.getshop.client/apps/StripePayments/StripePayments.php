<?php
namespace ns_3d02e22a_b0ae_4173_ab92_892a94b457ae;

class StripePayments extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Start accepting payments using stripe.";
    }

    public function getName() {
        return "StripePayments";
    }

    public function render() {
        
    }
}
?>

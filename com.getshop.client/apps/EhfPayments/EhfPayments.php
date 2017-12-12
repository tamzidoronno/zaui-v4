<?php
namespace ns_bd13472e_87ee_4b8d_a1ae_95fb471cedce;

class EhfPayments extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Send payments using the Norwegian electronic invoicing standard.";
    }

    public function getName() {
        return "EHF";
    }

    public function render() {
        
    }
}
?>

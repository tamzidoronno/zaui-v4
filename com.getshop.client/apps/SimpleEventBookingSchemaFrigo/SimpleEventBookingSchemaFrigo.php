<?php
namespace ns_bea0c467_dd4d_4066_891c_172adc42bb9f;

class SimpleEventBookingSchemaFrigo extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SimpleEventBookingSchemaFrigo";
    }

    public function render() {
        $this->includefile("schema");
    }
}
?>

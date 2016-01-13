<?php
namespace ns_cb8e7f1e_8659_4e08_9e26_145c548d5873;

class SedoxPartners extends \ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b\SedoxCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxPartners";
    }

    public function render() {
        $this->includefile("sedoxpartners");
    }

    /**
     * 
     * @param \core_sedox_SedoxUser $partner
     */
    public function getLatestTranscation($partner) {
        
    }

}
?>

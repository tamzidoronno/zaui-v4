<?php
namespace ns_9ab6923e_3d6b_4b7c_b94e_c14e5ebe5364;

class PmsMonthlyBilling extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsMonthlyBilling";
    }

    public function render() {
        $this->includefile("instructions");
    }
}
?>

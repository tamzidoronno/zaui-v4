<?php
namespace ns_6ce66971_051e_40cc_a108_d907cbf36a69;

class ProductCopies extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductCopies";
    }

    public function render() {
        $this->includefile("copies1");
    }
}
?>

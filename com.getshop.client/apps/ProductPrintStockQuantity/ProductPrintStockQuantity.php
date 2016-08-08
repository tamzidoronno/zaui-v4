<?php
namespace ns_80eec0c8_29be_49c0_b6b6_b83568dba54c;

class ProductPrintStockQuantity extends \ns_e4e2508c_acf8_4064_9e94_93744881ff00\ProductCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductPrintStockQuantity";
    }

    public function render() {
        echo $this->getProduct()->stockQuantity;
    }
}
?>

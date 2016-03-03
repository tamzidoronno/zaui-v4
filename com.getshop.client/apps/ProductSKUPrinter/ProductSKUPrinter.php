<?php
namespace ns_89c66c51_ce15_4480_b76d_69826cf661ea;

class ProductSKUPrinter extends \ns_e4e2508c_acf8_4064_9e94_93744881ff00\ProductCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductSKUPrinter";
    }

    public function render() {
        echo $this->getProduct()->sku;
    }
}
?>

<?php
namespace ns_82f02286_a483_4b4c_8f0d_a2239487c055;

class ProductVariations extends \ns_e4e2508c_acf8_4064_9e94_93744881ff00\ProductCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductVariations";
    }

    public function render() {
        $this->includefile("variationspicker");
    }
}
?>

<?php
namespace ns_87a1fd28_fae0_444d_b4b3_8008c7acac55;

class ProductAddToCart extends \ns_e4e2508c_acf8_4064_9e94_93744881ff00\ProductCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductAddToCart";
    }

    public function render() {
        $this->includefile("addtocartbutton");
    }
}
?>

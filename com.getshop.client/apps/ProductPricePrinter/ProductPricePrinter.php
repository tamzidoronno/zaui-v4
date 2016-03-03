<?php
namespace ns_ea6ed221_d18f_44db_bfaa_e5252ba0895c;

class ProductPricePrinter extends \ns_e4e2508c_acf8_4064_9e94_93744881ff00\ProductCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductPricePrinter";
    }

    public function render() {
        echo \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::formatPrice($this->getProduct()->price);
    }
}
?>

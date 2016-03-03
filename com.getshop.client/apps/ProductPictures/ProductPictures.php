<?php
namespace ns_3be5feb0_6ce8_4341_bf52_02a945ed550e;

class ProductPictures extends \ns_e4e2508c_acf8_4064_9e94_93744881ff00\ProductCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductPictures";
    }

    public function render() {
        $this->includefile("pictures");
    }
}
?>

<?php
namespace ns_e4e2508c_acf8_4064_9e94_93744881ff00;

class ProductTitle extends ProductCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductTitle";
    }

    public function render() {
        echo "<h1>".$this->getProduct()->name."</h1>";
    }
}
?>

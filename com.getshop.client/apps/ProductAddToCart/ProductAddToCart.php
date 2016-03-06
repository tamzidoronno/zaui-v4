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

    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $variants = array();
        $count = 1;
        if(isset($_POST['data']['count'])) {
            $count = $_POST['data']['count'];
        }
        $this->getApi()->getCartManager()->addProduct($productId, $count, $variants);
    }

}
?>

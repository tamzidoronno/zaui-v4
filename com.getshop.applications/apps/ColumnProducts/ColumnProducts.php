<?php
namespace ns_de0b9b83_a41d_4fca_a2d4_3d3945cc8b9e;

/**
 * Description of RelatedProducts
 *
 * @author ktonder
 */
class ColumnProducts extends \WebshopApplication implements \Application {
    public $products;
    
    public function getDescription() {
       return $this->__("Add this left or right column at a product, and a new related products will be displayed.");
    }
    public function postProcess() {}
    
    public function getName() {
        return $this->__("Related products");
    }
    
    public function preProcess() {
        $productId = $this->getPage()->id;
        $this->products = $this->getApi()->getProductManager()->getRandomProducts(5, $productId);
    }
    
    public function render() {
        $this->includefile("view");
    }
    
    
    public function getStarted() {
        echo $this->__f("When you have added this application, it will automatically start relating products related to the main product added to this page.");
    }
}
?>
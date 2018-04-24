<?php
namespace ns_480bdbdd_4da9_44ca_95c9_2fcb044eaf22;

class SrsBarSystem extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SrsBarSystem";
    }

    public function render() {
        echo "<div class='productlistcontainer'>";
        $this->includefile("productlist");
        echo "</div>";
        
        echo "<div class='checkoutarea'>";
        $checkout = new \ns_74a61004_afeb_4627_b563_9c85b880b6ed\SrsCartCheckout();
        $checkout->renderApplication(true, $this);
        
        echo "</div>";
    }

    /**
     * 
     * @return \core_productmanager_data_Product[]
     */
    public function getProducts() {
        return $this->getApi()->getProductManager()->getAllProducts();
    }

}
?>

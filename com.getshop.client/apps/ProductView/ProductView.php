<?php
namespace ns_f52bea98_9616_4b43_bd28_32e62fa95c91;

class ProductView extends \MarketingApplication implements \Application {
    private $product = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductView";
    }

    public function render() {
        $this->includefile("overview");
    }

    public function setProductId($id) {
        $_SESSION['f52bea98_9616_4b43_bd28_32e62fa95c91_ProductView'] = $id;
    }

    public function getProduct() {
        if(!$this->product) {
            $this->product = $this->getApi()->getProductManager()->getProduct($_SESSION['f52bea98_9616_4b43_bd28_32e62fa95c91_ProductView']);
        }
        
        return $this->product;
    }

    public function isTabActive($tabName) {
        if (!isset($_SESSION['f52bea98_9616_4b43_bd28_32e62fa95c91_selectedTab'])) {
            return $tabName === "details" ? "active" : "";
        }
        
        return $_SESSION['f52bea98_9616_4b43_bd28_32e62fa95c91_selectedTab'] == $tabName ? "active" : "";
    }
    
    public function subMenuChanged() {
        $_SESSION['f52bea98_9616_4b43_bd28_32e62fa95c91_selectedTab'] = $_POST['data']['selectedTab'];
        if ($_POST['data']['selectedTab'] == "details") { $this->includefile("details"); }
        if ($_POST['data']['selectedTab'] == "lists") { $this->includefile("lists"); }
        if ($_POST['data']['selectedTab'] == "categories") { $this->includefile("categories"); }
        if ($_POST['data']['selectedTab'] == "variations") { $this->includefile("variations"); }
    }

}
?>

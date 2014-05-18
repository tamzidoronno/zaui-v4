<?php

namespace ns_23fac58b_5066_4222_860c_a9e88196b8a1;

class SedoxProductView extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "SedoxProductView";
    }

    public function getName() {
        return "SedoxProductView";
    }

    public function render() {
        $this->includefile("productview");
    }

    public function getCurrentProduct() {
        if (isset($_GET['productId'])) {
            $_SESSION['sedox_current_productid'] = $_GET['productId'];
        }
        
        if (!isset($_SESSION['sedox_current_productid'])) {
            return null;
        }
        
        return $this->getApi()->getSedoxProductManager()->getProductById($_SESSION['sedox_current_productid']);
    }    
    
    public function getPriceForFile() {
        return 0;
    }
}
?>

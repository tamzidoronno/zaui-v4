<?php

namespace ns_b23a3767_1f7b_40e3_93c5_65504ebaa73c;

class SedoxMenu extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return "SedoxMenu";
    }

    public function getName() {
        return "SedoxMenu";
    }

    public function render() {
        $this->includefile("sedoxmenu");
    }
    
    public function isHomePage() {
        return $this->getPage()->getId() == "home";
    }
    
    public function searchProduct() {
        // Magic ? 
        // Well, sort of. this sets the session variable then the render function in
        // SedoxProductSearch picks it up and uses it to search for products.
        $_SESSION['searchKey'] = $_POST['data']['searchKey'];
    }
}
?>

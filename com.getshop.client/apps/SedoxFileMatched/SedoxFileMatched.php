<?php
namespace ns_c7b87917_6a26_43cf_9876_7e3ef9eeab3e;

class SedoxFileMatched extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxFileMatched";
    }

    public function render() {
        $this->product = $this->getApi()->getSedoxProductManager()->getProductById($_GET['productId']);
        if (isset($_POST['data']['productId'])) {
            echo '<script>thundashop.common.goToPage("upload_successful");</script>';
        } else {
            $this->includefile("productview");
        }
        
    }

    public function getCurrentProduct() {
        return $this->product;
    }
    
    public function requestSpecialFile() {
        $this->getApi()->getSedoxProductManager()->requestSpecialFile($_POST['data']['productId'], $_POST['data']['description']);
        
    }

}
?>

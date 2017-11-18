<?php
namespace ns_c5a4b5bf_365c_48d1_aeef_480c62edd897;

class PsmConfigurationAddons extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function test() {
        return "TESWT";
    }
    
    public function getName() {
        return "PsmConfigurationAddons";
    }

    public function render() {
        $this->includefile("loadproducts");
    }
    
    public function createProduct() {
        $product = $this->getApi()->getProductManager()->createProduct();
        $product->name = $_POST['data']['productname'];
        $product->tag = "addon";
        $product = $this->getApi()->getProductManager()->saveProduct($product);
        $notifications = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        
        $conf = new \core_pmsmanager_PmsBookingAddonItem();
        $found = false;
        foreach($notifications->addonConfiguration as $tmpaddon) {
            if($tmpaddon->productId == $product->id) {
                $conf = $tmpaddon;
                $found = true;
            }
        }
        if(!$found) {
            $notifications->addonConfiguration->{-100000} = $conf;
        }
            
        $conf->productId = $product->id;
        $conf->isSingle = true;

        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notifications);        
    }
    
    
    public function saveProductConfig() {
        
    }
    
}
?>

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
    
    public function loadTranslationText() {
        $this->includefile("translations");
    }
    
    public function savetranslationforproduct() {
        
        $notifications = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $productId = $_POST['data']['productid'];

        $languages = $this->getFactory()->getLanguageCodes();
        $selected = $this->getFactory()->getSelectedLanguage();
        $languages[] = $this->getFactory()->getSelectedLanguage();

        foreach($notifications->addonConfiguration as $tmpaddon) {
            if($tmpaddon->productId == $productId) {
                foreach($languages as $language) {
                    $tmpaddon->translationStrings->{$language."_descriptionWeb"} = json_encode($_POST['data'][$language."_descriptionWeb"]);
                }
                $tmpaddon->descriptionWeb = $_POST['data'][$selected."_descriptionWeb"];
            }
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notifications);
        
        //Avoid reloading addons page hack.
        echo "done";
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
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        
        foreach($_POST['data']['products'] as $productId => $res) {
            $found = false;
            foreach($config->addonConfiguration as $addon) {
                /* @var $addon \core_pmsmanager_PmsBookingAddonItem */
                if($addon->productId == $productId) {
                    $addon->onlyForBookingItems = array();
                    if(isset($res['onlyForItems'])) {
                        $addon->onlyForBookingItems = $res['onlyForItems'];
                    }
                    
                    $found = true;
                    $addon->isSingle = $res['daily'] != "true";
                    $addon->price = $res['price'];
                    $addon->dependsOnGuestCount = $res['perguest'] == "true";
                    $addon->noRefundable = $res['nonrefundable'] == "true";
                    
                    $isIncluded = array();
                    foreach($res as $id => $isSelected) {
                        if(stristr($id, "includeinroom_") && $isSelected == "true") {
                            $isIncluded[] = str_replace("includeinroom_", "", $id);
                        }
                    }
                    
                    $displayInBookingProcess = array();
                    foreach($res as $id => $isSelected) {
                        if(stristr($id, "sellonroom_") && $isSelected == "true") {
                            $displayInBookingProcess[] = str_replace("sellonroom_", "", $id);
                        }
                    }
                    $addon->includedInBookingItemTypes = $isIncluded;
                    $addon->descriptionWeb = $res['descriptionWeb'];
                    $addon->bookingicon = $res['bookingicon'];
                    $addon->count = $res['count'];
                    $addon->displayInBookingProcess = $displayInBookingProcess;
                    $addon->channelManagerAddonText = $res['channelManagerAddonText'];
                }
                
            }
            if(!$found) {
                echo "Not found: " . $productId;
            }
            
            $product = $this->getApi()->getProductManager()->getProduct($productId);
            $product->price = $res['price'];
            $product->taxgroup = $res['taxgroup'];
            $this->getApi()->getProductManager()->saveProduct($product);
        }
        
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
        
    }
    
}
?>

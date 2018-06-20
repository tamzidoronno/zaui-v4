<?php
namespace ns_c5a4b5bf_365c_48d1_aeef_480c62edd897;

class PsmConfigurationAddons extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function loadExtendedProductInfo() {
        $this->includefile("extendedproductinfo");
    }

    public function test() {
        return "TESWT"; 
    }
    
    public function readAddons() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $roomId = $_POST['data']['roomid'];
        $productId = $_POST['data']['productId'];
        $addonToAdd = null;
        foreach($config->addonConfiguration as $addonItem) {
            if($addonItem->productId == $productId) {
                $addonToAdd = $addonItem;
                break;
            }
        }
        
        $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedMultilevelDomainName(), null);
        foreach($bookings as $booking) {
            foreach($booking->rooms as $room) {
                if($this->hasAddon($productId, $room)) {
                    $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedMultilevelDomainName(), $addonToAdd->addonType, $room->pmsBookingRoomId, true);
                    $this->getApi()->getPmsManager()->addAddonsToBooking($this->getSelectedMultilevelDomainName(), $addonToAdd->addonType, $room->pmsBookingRoomId, false);
                }
            }
        }
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
                if ($addon->productId == $productId) {
                    $found = true;
                    $addon->isSingle = $res['daily'] != "true";
                    $addon->price = $res['price'];
                    $addon->dependsOnGuestCount = $res['perguest'] == "true";
                    $addon->noRefundable = $res['nonrefundable'] == "true";

                    $isIncluded = array();
                    foreach ($res as $id => $isSelected) {
                        if (stristr($id, "includeinroom_") && $isSelected == "true") {
                            $isIncluded[] = str_replace("includeinroom_", "", $id);
                        }
                    }

                    $displayInBookingProcess = array();
                    foreach ($res as $id => $isSelected) {
                        if (stristr($id, "sellonroom_") && $isSelected == "true") {
                            $displayInBookingProcess[] = str_replace("sellonroom_", "", $id);
                        }
                    }
                    $addon->includedInBookingItemTypes = $isIncluded;
                    $addon->displayInBookingProcess = $displayInBookingProcess;
                }
            }
            if (!$found) {
                echo "Not found: " . $productId;
            }

            $product = $this->getApi()->getProductManager()->getProduct($productId);
            $product->price = $res['price'];
            $product->taxgroup = $res['taxgroup'];
            $this->getApi()->getProductManager()->saveProduct($product);
        }

        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
        
    }

    public function removeValidTimeRange() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        foreach($config->addonConfiguration as $conf) {
            if($_POST['data']['id'] == $conf->productId) {
                foreach($conf->validDates as $idx => $vdate) {
                    if($vdate->id == $_POST['data']['rangeid']) {
                        unset($conf->validDates[$idx]);
                    }
                }
                $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
                break;
            }
        }

        $this->includefile("singleProductConfig");
    }
    
    public function addDateRangeForm() {
        if($_POST['data']['start'] && $_POST['data']['end']) {
            $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
            foreach($config->addonConfiguration as $conf) {
                if($_POST['data']['id'] == $conf->productId) {
                    $range = new \core_pmsmanager_PmsBookingAddonItemValidDateRange();
                    $range->start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
                    $range->end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
                    $range->validType = $_POST['data']['validtype'];
                    $conf->validDates[] = $range;
                    $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
                    break;
                }
            }
        }
    }
    
    public function saveExtendedProductInfo() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        foreach($config->addonConfiguration as $addon) {
            if($addon->productId != $_POST['data']['productId']) {
                continue;
            }
            $addon->onlyForBookingItems = $_POST['data']['onlyForItems'];
            $addon->descriptionWeb = $_POST['data']['descriptionWeb'];
            $addon->bookingicon = $_POST['data']['bookingicon'];
            $addon->count = $_POST['data']['count'];
            $addon->channelManagerAddonText = $_POST['data']['channelManagerAddonText'];
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    /**
     * 
     * @param type $productId
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function hasAddon($productId, $room) {
        foreach($room->addons as $addon) {
            if($addon->productId == $productId) {
                return true;
            }
        }
        return false;
    }

}
?>

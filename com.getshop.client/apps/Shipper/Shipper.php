<?php
namespace ns_098bb0fe_eb51_42c6_9fbb_dadb7b52dd56;

class Shipper extends \ShipmentApplication implements \Application {
    public $singleton = true;

    public function getDescription() {
        return $this->__("this application gives you the ability to setup fixed shipping price");
    }

    public function postProcess() {
        
    }
    
    public function getYoutubeId() {
        return "U2baWNNpyKI";
    }

    public function preProcess() {
        
    }

    public function render() {
        
    }
    
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("Configuration");
    }

    public function getName() {
        return $this->__w("Shipment settings");        
    }

    public function getStartUpCost() {
        return $this->getConfigurationSetting("shippingstart");
    }
    
    public function getItemPrice() {
        return $this->getConfigurationSetting("priceeachproduct");
    }
    
    public function getFreeShipmentPrice() {
        return $this->getConfigurationSetting("freeshipment");
    }
    
    private function getSimpleShippingCost($shipmentProduct, $variations) {
        $totalCost = $this->getStartUpCost();
        $priceEachProduct = $this->getItemPrice();
        $freeShipment = $this->getFreeShipmentPrice();
        
        if($shipmentProduct != null) {
            $totalCost = $shipmentProduct->price;
        }
        
        $cart = $this->getApi()->getCartManager()->getCart();
        $cartHelper = new \HelperCart($cart);
        $cost = (double)$this->getApi()->getCartManager()->getShippingPriceBasis();
        if ($freeShipment && $cost > $freeShipment) {
            return 0;
        }
         
        if ($priceEachProduct > 0) {
            foreach ($cart->items as $item) {
                /* @var $item \core_cartmanager_data_CartItem */
                $totalCost += $item->count * $priceEachProduct;
            }
        } 
        
        return ($totalCost == "") ? 0 : $totalCost;
    }
    
    public function getShippingType() {
        if (isset($this->getConfiguration()->settings->{"shippingtype"})) {
            return $this->getConfiguration()->settings->{"shippingtype"}->value;
        } else {
            return "simple";
        }
    }
    
    public function getShippingCost($shipmentProduct = null, $variations = array()) {
        $shippingtype = $this->getShippingType();
        return $this->getSimpleShippingCost($shipmentProduct, $variations);
    }
    
    public function additionalInformation() {
        echo "<div class='section selected' type=''>";
            echo "<div class='title'>";
                echo $this->__w("Fixed price");
                echo "<span class='price'>";
                    echo $this->getShippingCost();
                echo "</span>";
            echo "</div>";
        echo "</div>";
     }
     
     public function saveSettings() {
         $this->setConfigurationSetting("shippingstart", $_POST['startupCost']);
         $this->setConfigurationSetting("priceeachproduct", $_POST['itemPrice']);
         $this->setConfigurationSetting("freeshipment", $_POST['freeShipping']);
     }
}
?>
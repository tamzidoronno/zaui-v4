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

    private function getSimpleShippingCost($shipmentProduct, $variations) {
        $totalCost = 0;
        
        if (isset($this->getConfiguration()->settings->{"shippingstart"})) {
            $totalCost = $this->getConfiguration()->settings->{"shippingstart"}->value;
        } 
        
        if($shipmentProduct != null) {
            $totalCost = $shipmentProduct->price;
        }
        
        $priceEachProduct = 0;
        if (isset($this->getConfiguration()->settings->{"priceeachproduct"})) {
            $priceEachProduct = $this->getConfiguration()->settings->{"priceeachproduct"}->value;
        }
        
        $freeShipment = 0;
        if (isset($this->getConfiguration()->settings->{"freeshipment"}->value)) {
            $freeShipment = $this->getConfiguration()->settings->{"freeshipment"}->value;
        }
        
        $cart = $this->getApi()->getCartManager()->getCart();
        $cartHelper = new \HelperCart($cart);
        $cost = (double)$this->getApi()->getCartManager()->getShippingPriceBasis();
        if ($cost > $freeShipment) {
            return 0;
        }
         
        if ($priceEachProduct > 0) {
            foreach ($cart->items as $item) {
                /* @var $item \core_cartmanager_data_CartItem */
                $totalCost += $item->count*$priceEachProduct;
            }
        } 
        
        return ($totalCost == "") ? 0 : $totalCost;
    }
    
    public function getAdvancedCost($shipmentProduct, $variations) {
        $cost = $this->getApi()->getCartManager()->getShippingPriceBasis();
        
        if($shipmentProduct != null) {
            $cost = $this->getApi()->getProductManager()->getPrice($shipmentProduct->id, $variations);
        }
        
        $ranges = array();
        if (isset($this->getConfiguration()->settings->shippingrange) && is_array($this->getConfiguration()->settings->shippingrange->value)) {
            $ranges = $this->getConfiguration()->settings->shippingrange->value;
        }
        
        foreach ($ranges as $range ) {
            if ((double)trim($range["col0"]) < $cost && $cost <= (double)trim($range['col1'])) {
                return $range["col2"];
            }
        }
        
        if (isset($this->getConfiguration()->settings->outofrangeprice) && $this->getConfiguration()->settings->outofrangeprice->value != "") {
            return $this->getConfiguration()->settings->outofrangeprice->value;
        }
        
        return 0;
    }
    
    private function getShippingType() {
        if (isset($this->getConfiguration()->settings->{"shippingtype"})) {
            return $this->getConfiguration()->settings->{"shippingtype"}->value;
        } else {
            return "simple";
        }
    }
    
    public function getShippingCost($shipmentProduct = null, $variations = array()) {
        $shippingtype = $this->getShippingType();
        
        if ($shippingtype == "advanced") {
            return $this->getAdvancedCost($shipmentProduct, $variations);
        } else {
            return $this->getSimpleShippingCost($shipmentProduct, $variations);
        }
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
}
?>
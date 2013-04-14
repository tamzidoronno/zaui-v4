<?php
class ShipmentApplication extends ApplicationBase {
    public $cart;
    
    /**
     * @return core_cartmanager_data_Cart
     **/
    function getCart() {
        return $this->cart;
    }
    
    public function getShippingCost($shipmentProduct = "") {
        return 0;
    }
    
    public function getYoutubeId() {
        
    }
    
    public function getDefaultShipmentType() {
        return "";
    }
}
?>

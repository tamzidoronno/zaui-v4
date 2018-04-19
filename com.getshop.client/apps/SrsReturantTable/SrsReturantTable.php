<?php
namespace ns_686d5674_8c58_46a5_b38f_09772c9e5e02;

class SrsReturantTable extends \MarketingApplication implements \Application {
    private $reservation;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SrsReturantTable";
    }

    public function render() {
        if (isset($_GET['reservertionid'])) {
            $_SESSION['SrsRestaurantTable_reservationid'] = $_GET['reservertionid'];
        }
        
        $this->loadCart();
        $this->includefile("table");
    }
    
    /**
     * 
     * @return \core_resturantmanager_ResturantTable
     */
    public function getCurrentTable() {
        return $this->getApi()->getResturantManager()->getTableById($_SESSION['SrsReturantTable_tableid']);
    }

    public function setCurrentTableId() {
        $_SESSION['SrsReturantTable_tableid'] = $_GET['tableid'];
    }

    public function groupItemsOnPersons($cartItems) {
        /* @var $cartItems \core_resturantmanager_ResturantCartItem[] */
        
        $ret = array();
    
        foreach($cartItems as $cartItem)
        { 
            $ret[$cartItem->tablePersonNumber][] = $cartItem;
        }
        
        return $ret;
    }
    
    public function formatName($item) {
        return $this->products[$item->productId]->name;
    }
    
    public function formatPrice($item) {
        return $this->products[$item->productId]->price;
    }

    public function printTable($cartItems) {
        $this->setProductsNeeded($cartItems);
        
        $args = array();
        
        $attributes = array(
            array('name', 'Name', null, 'formatName'),
            array('price', 'Price', null, 'formatPrice'),
        );

        $table = new \GetShopModuleTable($this, 'ResturantManager', "showData", $args, $attributes);
        $table->setData($cartItems);
        $table->render();
    }

    public function setProductsNeeded($cartItems) {
        $this->products = array();
        foreach ($cartItems as $item) {
            $this->products[$item->productId] = $this->getApi()->getProductManager()->getProduct($item->productId);
        }
    }
    
    public function loadCart() {
        $this->reservation = $this->getApi()->getResturantManager()->getTableReservation($_SESSION['SrsRestaurantTable_reservationid']);
        $this->getApi()->getResturantManager()->createCartForReservation($this->reservation->reservationId);        
    }

    /**
     * 
     * @return \core_resturantmanager_TableReservation
     */
    public function getCurrentReservation() {
        return $this->reservation;
    }
}
?>

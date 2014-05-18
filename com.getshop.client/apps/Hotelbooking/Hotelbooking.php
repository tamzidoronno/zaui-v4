<?php
namespace ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2;

class Hotelbooking extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }
 
   public function getDescription() {
        return "Hotelbooking";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    function checkavailability() {
        $start = strtotime($_POST['data']['start']);
        $end =  strtotime($_POST['data']['stop']);
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['roomProduct']);
        
        $numbers = $this->getApi()->getHotelBookingManager()->checkAvailable($start,$end,$product->sku);
        if($numbers) {
            echo $numbers;
        }
        
        $_SESSION['hotelbooking']['start'] = $start;
        $_SESSION['hotelbooking']['end'] = $end;
        $_SESSION['hotelbooking']['product'] = $product->id;
    }
    
    public function getProduct() {
        return $this->getApi()->getProductManager()->getProduct($_SESSION['hotelbooking']['product']);
    }
    
    public function getStart() {
        return $_SESSION['hotelbooking']['start'];
    }
    
    public function getEnd() {
        return $_SESSION['hotelbooking']['end'];
    }
    
    public function getType() {
        return $_SESSION['hotelbooking']['type'];
    }
    
    public function getName() {
        return "Hotelbooking";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() { 
   }

    public function render() {
        $this->includefile("Hotelbooking");
    }
}
?>

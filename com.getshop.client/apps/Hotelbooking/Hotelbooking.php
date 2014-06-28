<?php
namespace ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2;

class Hotelbooking extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $failedReservation = false;
    var $invalid = false;
    
    function __construct() {
    }
 
    public function updatePersonCount() {
        $this->setPersonCount($_POST['data']['count']);
    }
    
   public function getDescription() {
        return "Hotelbooking";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    function getNumberOfAvailableRooms($type) {
        return $this->getApi()->getHotelBookingManager()->checkAvailable($this->getStart(),$this->getEnd(),$type);
    }
    
    function getDayCount() {
        return ($this->getEnd() - $this->getStart())/86400;
    }
    
    function checkavailability() {
        $start = strtotime($_POST['data']['start']);
        $end =  strtotime($_POST['data']['stop']);
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['roomProduct']);
        
        $numbers = $this->getApi()->getHotelBookingManager()->checkAvailable($start,$end,$product->sku);
        if($numbers) {
            echo $numbers;
        }
        
        $this->setStartDate($start);
        $this->setEndDate($end);
        $this->setProductId($product->id);
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

   public function getContinuePage() {
       return $this->getConfigurationSetting("contine_page");
   }
   
    public function render() {
        if(isset($_GET['set_order_page'])) {
            $this->setConfigurationSetting("contine_page", $_GET['set_order_page']);
        }
        if($this->getPage()->id == "home") {
            $this->includefile("Hotelbooking");
        } else {
            
            if(isset($_GET['subpage']) && $_GET['subpage'] == "summary") {
                 $this->includefile("booking_part3");
            } else {
                 $this->includefile("booking_part2");
            }
        }
    }

    public function updateCalendarDate() {
        $type = $_POST['data']['type'];
        $day = $_POST['data']['day'];
        $month = $_POST['data']['month'];
        $year = $_POST['data']['year'];
        
        if($type == "startDate") {
            $this->setStartDate(strtotime($year."-".$month."-".$day));
        }
        if($type == "endDate") {
            $this->setEndDate(strtotime($year."-".$month."-".$day));
        }
    }
    
    public function printCalendar($time, $checkbefore, $id) {
        $month = date("m", $time);
        $timestamp = mktime(0,0,0,$month,1,date("y", $time));
        $maxday = date("t",$timestamp);
        $thismonth = getdate ($timestamp);
        $startday = $thismonth['wday'];
        $year = date('y', $time);
        if($startday == 0) {
            $startday = 7;
        }
        echo "<table width='100%' class='booking_table' type='$id' year='$year' month='$month'>";
        echo "<tr>";
        echo "<th>".$this->__w("Mo")."</th>";
        echo "<th>".$this->__w("Tu")."</th>";
        echo "<th>".$this->__w("We")."</th>";
        echo "<th>".$this->__w("Th")."</th>";
        echo "<th>".$this->__w("Fr")."</th>";
        echo "<th>".$this->__w("Sa")."</th>";
        echo "<th>".$this->__w("Su")."</th>";
        echo "</tr>";
        echo "<tr>";
        for ($i=1; $i<($maxday+$startday); $i++) {
            if($i < $startday) 
                echo "<td></td>";
            else {
                $class = "";
                $day = $i - $startday + 1;
                if(date('d', $time) ==(($i - $startday)+1)) {
                    $class = "selected";
                }
                if($checkbefore) {
                    $aftertime = strtotime($year . "-" . $month . "-" . $day);
                    if($checkbefore > $aftertime || (date("d", $checkbefore) == date("d",$aftertime) && date("m", $checkbefore) == date("m",$aftertime))) {
                        $class .= " disabled";
                    }
                }
                
                echo "<td align='center' valign='middle' height='20px'><span class='cal_field $class' day='$day'>".$day."</span></td>";
            }
            if(($i % 7) == 0) echo "</tr><tr>";
        }
        echo "</tr>";
        echo "</table>";
    }
    

    public function updateRoomCount() {
        $this->setRoomCount($_POST['data']['count']);
    }
    
    public function getRoomCount() {
        if(isset($_SESSION['hotelbooking']['count'])) {
            return $_SESSION['hotelbooking']['count'];
        }
        return 1;
    }
    
    public function changeProduct() {
        $this->setProductId($_POST['data']['productid']);
    }
    
    private function setRoomCount($count) {
        $_SESSION['hotelbooking']['count'] = $count;
    }
    
    public function setStartDate($start) {
        $_SESSION['hotelbooking']['start'] = $start;
    }

    public function setEndDate($end) {
        $_SESSION['hotelbooking']['end'] = $end;
    }

    public function setProductId($id) {
        $_SESSION['hotelbooking']['product'] = $id;
    }

    public function setCleaningOption() {
        $_SESSION['hotelbooking']['cleaning'] = $_POST['data']['product'];
    }
    
    public function continueToCart() {
        $count = $this->getRoomCount();
        
        if($count < $this->getRoomCount()) {
            $this->failedReservation = true;
            return;
        }
        
        $type = $this->getProduct()->sku;
        $start = $this->getStart();
        $end = $this->getEnd();
       
        $contact = $this->getContactData();
        
        $reference = $this->getApi()->getHotelBookingManager()->reserveRoom($type, $start, $end, $count, $contact);
        if(($reference) > 0) {
            $cartmgr = $this->getApi()->getCartManager();
            $cartmgr->clear();
            $cartmgr->addProduct($this->getProduct()->id, $this->getDayCount(), array());
            $cleaningid = $this->getCleaningOption();
            if($cleaningid) {
                $cleaningproduct = $this->getApi()->getProductManager()->getProduct($cleaningid);
                $interval = $cleaningproduct->stockQuantity;
                $cleaningcount = floor($this->getDayCount() / $interval);
                $cartmgr->addProduct($cleaningid, $cleaningcount, array());
            }
            
            $_SESSION['tempaddress']['emailAddress'] = $_POST['data']['email'];
            $_SESSION['tempaddress']['fullName'] = $_POST['data']['name_1'];
            $_SESSION['tempaddress']['phone'] = $_POST['data']['phone_1'];
            $_SESSION['tempaddress']['city'] = $_POST['data']['city'];
            $_SESSION['tempaddress']['postCode'] = $_POST['data']['postal_code'];
            $_SESSION['tempaddress']['address'] = $_POST['data']['address'];
            
            $_GET['page'] = "cart";
            $_GET['subpage'] = "gotopayment";
            if($this->partnerShipChecked()) {
                $_GET['subpage'] = "confirmation";
            }
        } else {
            $_GET['failedreservation'] = true;
            $this->failedReservation = true;
        }
    }
    
    public function checkFailedReservation() {
        if(isset($_GET['failedreservation'])) {
            return  true;
        }
        return $this->failedReservation;
    }
    
    public function getCleaningOption() {
        if(isset($_SESSION['hotelbooking']['cleaning'])) {
            return $_SESSION['hotelbooking']['cleaning'];
        }
        return "";
    }
    
    public function getCleaningProudcts() {
        $products = $this->getApi()->getProductManager()->getAllProducts();
        $vasking = array();
        foreach($products as $product) {
            if($product->sku == "vask") {
                $vasking[] = $product;
            }
        }
        
        return $vasking;
    }
    
    public function getHotelRooms() {
        $rooms = $this->getApi()->getProductManager()->getAllProducts();
        $types = $this->getApi()->getHotelBookingManager()->getRoomTypes();
        $roomtypes = [];
        $allrooms = array();
        foreach($types as $type) {
            /* @var $type \core_hotelbookingmanager_RoomType */
            $roomtypes[$type->name] = true;
        }
        foreach($rooms as $room) {
            /* @var $room \core_productmanager_data_Product */
            if(isset($roomtypes[$room->sku])) {
                $allrooms[] = $room;
            }
        }
        return $allrooms;
    }
    
    public function validateAndContinueToPayment() {
        $_GET['subpage'] = "summary";
        $this->setBookingData();
        foreach($this->getBookingData() as $index => $test) {
            $valid = true;
            if($this->validateInput($index)) {
                $valid = false;
                break;
            }
        }
        if($valid) {
            $this->continueToCart();
        }
    }
    
    public function getPost($name) {
        if(isset($_POST['data'][$name])) {
            return $_POST['data'][$name];
        }
        return "";
    }
    
    public function validateInput($name) {
        if(isset($_POST['data'][$name]) && !$this->partnerShipChecked()) {
            if($name == "referencenumber") {
                return "";
            }
            if(strlen(trim($_POST['data'][$name])) == 0) {
                $this->invalid = true;
                return "invalid";
            }
        }
        
        if($this->partnerShipChecked() && $name == "referencenumber") {
            $allUsers = $this->getApi()->getUserManager()->getAllUsers();
            $referenceUser = null;
            foreach($allUsers as $user) {
                if($user->referenceKey == $_POST['data']['referencenumber']) {
                    $referenceUser = $user;
                    break;
                }
            }
            
            if(!$referenceUser) {
                $this->invalid = true;
                return "invalid";
            }
        }

        
        return "";
    }

    public function setPersonCount($count) {
        $_SESSION['hotelbooking']['roomCount'] = $count;
    }
    
    public function getPersonCount() {
        if(isset($_SESSION['hotelbooking']['roomCount'])) {
            return $_SESSION['hotelbooking']['roomCount'];
        }
        return 1;
    }

    public function getCleaningPrice() {
        $cleanprodid = $this->getCleaningOption();
        if(!$cleanprodid) {
            return 0;
        }
        $cleaningproduct = $this->getApi()->getProductManager()->getProduct($cleanprodid);
        $count = (int)($this->getDayCount() / $cleaningproduct->stockQuantity);
        
        $cleaningprice = $count * $cleaningproduct->price;
        return $cleaningprice;
    }

    public function getRoomPrice() {
        return $this->getDayCount() * $this->getProduct()->price * $this->getRoomCount();
    }
    
    public function getTotal() {
        return $this->getCleaningPrice() + $this->getRoomPrice();
    }

    public function hasErrors() {
        return $this->invalid;
    }

    public function setBookingData() {
        $_SESSION['booking_data'] = serialize($_POST['data']);
    }
    
    public function getBookingData() {
        return unserialize($_SESSION['booking_data']);
    }

    
    public function loadBookingData() {
        if(isset($_SESSION['booking_data'])) {
            $_POST['data'] = unserialize($_SESSION['booking_data']);
        }
    }

    public function getContactData() {
        $booked = $this->getBookingData();
        $names = array();
        $phones = array();
        for($i=0; $i<10; $i++) {
            if(isset($booked['name_'.$i])) {
                $names[] = $booked['name_'.$i];
                $phones[] = $booked['phone_'.$i];
            }
        }
        $contact = new \core_hotelbookingmanager_ContactData();
        $contact->names = $names;
        $contact->phones = $phones;
        return $contact;
    }

    public function partnerShipChecked() {
        if(isset($_POST['data']['partnershipdeal']) && $_POST['data']['partnershipdeal'] == "true") {
            return true;
        }
        return false;
    }

}
?>
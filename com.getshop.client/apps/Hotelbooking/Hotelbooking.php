<?php
namespace ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2;

class Hotelbooking extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $failedReservation = false;
    var $invalid = false;
    var $errors = array();
    
    function __construct() {
    }
 
    public function updatePersonCount() {
        $this->setPersonCount($_POST['data']['count']);
    }
    
    public function loadSettings() {
        $this->includefile("settings");
    }
    
    public function setConfig() {
        $this->setConfigurationSetting("name", $_POST['data']['name']);
        $this->setConfigurationSetting("type", $_POST['data']['type']);
        $this->setConfigurationSetting("contine_page", $_POST['data']['contine_page']);
        $this->setConfigurationSetting("minumum_rental_days", $_POST['data']['rental_days']);
    }
    
    public function getRoomTaxes() {
        
        if($this->getProduct()->privateExcluded && !$this->isMvaRegistered()) {
            return 0;
        }
        
        if(isset($this->getProduct()->taxGroupObject)) {
            return $this->getRoomPrice() * ($this->getProduct()->taxGroupObject->taxRate / 100);
        }
        return 0;
    }
    
    public function getMinumRental() {
        $minimum = $this->getConfigurationSetting("minumum_rental_days");
        if(!$minimum) {
            return 1;
        }
        
        return $minimum;
    }
    
    public function getProjectName() {
        if($this->getConfigurationSetting("name")) {
            return $this->getConfigurationSetting("name");
        }
        return "name not set";
    }
    
    public function getServiceType() {
        if($this->getConfigurationSetting("type")) {
            return $this->getConfigurationSetting("type");
        }
        return "hotel";
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
        if($this->getServiceType() == "storage") {
            $d1 = $this->getStart();
            $d2 = $this->getEnd();
            $min_date = min($d1, $d2);
            $max_date = max($d1, $d2);
            $i = 1;

            while (($min_date = strtotime("+1 MONTH", $min_date)) <= $max_date) {
                $i++;
            }
            return $i;
        }
        
        return ($this->getEnd() - $this->getStart())/86400;
    }
    
    function checkavailability() {
        $start = strtotime($_POST['data']['start']);
        $end =  strtotime($_POST['data']['stop']);
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['roomProduct']);
        
        if($this->getServiceType() == "storage") {
            $end = $start + 86400;
        }        
        
        $this->setStartDate($start);
        $this->setEndDate($end);
        $this->setProductId($product->id);
        
        $numbers = $this->checkavailabilityFromSelection();
        if($numbers) {
            echo $numbers;
        }
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
   
   public function sendConfirmationEmail() {
       $orderId = $_GET['orderId'];
       
       $message = "test";
       $title = "test title";
       $mainemail = false;
       if(isset($this->getFactory()->getSettings()->{"mainemailaddress"})) {
           $mainemail = $this->getFactory()->getSettings()->{"mainemailaddress"}->value;
       }
       
       $order = $this->getApi()->getOrderManager()->getOrder($orderId);
       $address = $order->cart->address;
       $name = $address->fullName;
       $reference = $order->reference;
       
       if($this->getServiceType() == "hotel") {
           $title = $this->__w("Thank you for your booking a room at")." ".$this->getProjectName().".";
       } else {
           $title = $this->__w("Thank you for your booking a storage room at")." ".$this->getProjectName().".";
       }
       echo "<br><br>";
       echo "<h1>" . $title . "</h1>";
       if($this->getServiceType() == "hotel") {
           $stay = $this->__w("A confirmation email has been sent to your email. Enjoy your stay at {hotel}.");
           $stay = str_replace("{hotel}", $this->getProjectName(), $stay);
           echo $stay;
       } else {
           echo $this->__w("A confirmation email has been sent to your email, and your storage room has been reserved. Please note that the storage room might not be final and could be changed to a different one with the same size if needed.");
       }
       echo "<br><br><br><br>";
       
       $booking = $this->getApi()->getHotelBookingManager()->getReservationByReferenceId($reference);
       $room = $this->getApi()->getHotelBookingManager()->getRoom($booking->roomIds[0]);
       $logo = $this->getApi()->getLogoManager()->getLogo();
       
       $logoAddress = "http://" . $_SERVER['SERVER_NAME'] . "/displayImage.php?id=" . $logo->LogoId;
       $body = "<div><img src='$logoAddress'></div><br><br>";
       $body .= $this->__w("Dear {name}") . "<br>";
       $body .= $title;
       $body .= $this->__w("This email is a confirmation that we have reserved a room for you.") . "<br>";
       $body .= $this->__w("The room has been reserved between {start} to {end}.") . "<br>";
       if($this->getServiceType() == "storage") {
           $body .= $this->__w("The room has been reserved from {start}.") . "<br>";
       }
       $body .= $this->__w("The code for the room is : {code}.") . "<br>";
       $body .= $this->__w("The reserved room is : {roomName}.") . "<br>";
       
       $body = str_replace("{start}", date("d-m-Y", strtotime($booking->startDate)), $body);
       $body = str_replace("{end}", date("d-m-Y", strtotime($booking->endDate)), $body);
       $body = str_replace("{roomName}", $room->roomName, $body);
       $body = str_replace("{code}", $booking->codes[0], $body);
       $body = str_replace("{name}", $name, $body);
       
       $body .= "<hr>";
       $body .= "<b>" . $this->__w("Contact information related to the booking event") . "</b><br>";
       
       $contactData = $booking->contact;
       
       foreach($contactData->names as $index => $name) {
           $body .= $name . "<br>";
           $body .= $contactData->phones[$index] . "<br>";
           $body .= "<br>";
       }
       $body .= "<b>" . $this->__w("Additional contact information") . "</b><br>";
       $body .= $address->address . "<br>";
       $body .= $address->postCode . "<br>";
       $body .= $address->city . "<br>";
       
       
       $this->getApi()->getMessageManager()->sendMail("boggibill@gmail.com", "", $title, $body, "post@getshop.com", "Booking");
       $this->getApi()->getMessageManager()->sendMail($mainemail, "", $title, $body, "post@getshop.com", "Booking");
       
   }

   public function getContinuePage() {
       return $this->getConfigurationSetting("contine_page");
   }
   
    public function render() {

        //this variable is set from $this->continueToCart();
        if(isset($_GET['orderProcessed'])) {
            if($this->partnerShipChecked() || !$this->hasPaymentAppAdded()) {
                $this->sendConfirmationEmail();
//                $this->includefile("confirmation");
            } else {
                //Send the user to the payment view.
                $payment = $this->getFactory()->getApplicationPool()->getAllPaymentInstances();
                if(sizeof($payment) > 0) {
                    //Orderid is set in $this->continueToPayment()
                    $payment[0]->order = $this->getApi()->getOrderManager()->getOrder($_GET['orderId']);
                    $payment[0]->preProcess();
                }
            }
            return;
        }
        
        if(isset($_GET['set_order_page'])) {
            $this->setConfigurationSetting("contine_page", $_GET['set_order_page']);
        }
        if($this->getPage()->id == "home") {
            $this->includefile("Hotelbooking");
        } else {
            if(((isset($_GET['subpage']) && $_GET['subpage'] == "summary") || isset($_POST['data']['partnershipdeal'])) && $this->hasValidSelection()) {
                 $this->includefile("booking_part3");
            } else {
                $this->hasValidSelection();
                $this->includefile("booking_part2");
            }
        }
    }

    public function getOrderPageId() {
        $contpage = $this->getConfigurationSetting("contine_page");
        if(!$contpage) {
            return "none";
        }
        return $contpage;
    }
    
    public function updateCalendarDate() {
        $type = $_POST['data']['type'];
        $time = $_POST['data']['time'];
        if($type == "startDate") {
            if($time > $this->getEnd()) {
                $this->setEndDate($time+86400);
            }
            $this->setStartDate($time);
        }
        if($type == "endDate") {
            $this->setEndDate($time);
        }
    }
    
    public function navigateMonthCalendar() {
        $time = strtotime("20".$_POST['data']['year']."-".$_POST['data']['month']."-01");
        $type = $_POST['data']['type'];
        $this->printCalendar($time, $this->getStart(), $type, false);
    }
    
    public function printCalendar($time, $checkbefore, $id, $setSelected = true) {
        $month = date("m", $time);
        $timestamp = mktime(0,0,0,$month,1,date("y", $time));
        $maxday = date("t",$timestamp);
        $thismonth = getdate ($timestamp);
        $startday = $thismonth['wday'];
        $year = date('y', $time);
        if($startday == 0) {
            $startday = 7;
        }
        $monthText = date("M", $time) . " " . $year;
        
        echo "<div class='cal_header' type='$id' year='$year' month='$month'><i class='fa fa-arrow-left calnav' style='float:left;cursor:pointer;' navigation='prev'></i>".$monthText."<i class='fa fa-arrow-right calnav' style='float:right;cursor:pointer;' navigation='next'></i></div>";

        echo "<table width='100%' class='booking_table'>";
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
                if(date('d', $time) ==(($i - $startday)+1) && $setSelected) {
                    $class = "selected";
                }
                if($checkbefore) {
                    $aftertime = strtotime($year . "-" . $month . "-" . $day);
                    if($checkbefore > $aftertime || (date("d", $checkbefore) == date("d",$aftertime) && date("m", $checkbefore) == date("m",$aftertime))) {
                        $class .= " disabled";
                    }
                }
                $fieldtime = strtotime($year ."-" . $month . "-" . $day);
                
                echo "<td align='center' valign='middle' height='20px'><span class='cal_field $class' time='$fieldtime'>".$day."</span></td>";
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
    
    public function continueToPayment() {
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
            $cartmgr->setReference($reference);
            $cartmgr->addProduct($this->getProduct()->id, $this->getDayCount(), array());
            $cleaningid = $this->getCleaningOption();
            if($cleaningid) {
                $cleaningproduct = $this->getApi()->getProductManager()->getProduct($cleaningid);
                $interval = $cleaningproduct->stockQuantity;
                $cleaningcount = floor($this->getDayCount() / $interval);
                $cartmgr->addProduct($cleaningid, $cleaningcount, array());
            }
            
            if($this->partnerShipChecked()) {
                $order = $this->getApi()->getOrderManager()->createOrderByCustomerReference($this->getReferenceKey());
            } else {
                $address = $this->getApiObject()->core_usermanager_data_Address();
                $address->fullName = $_POST['data']['name_1'];
                $address->city = $_POST['data']['city'];
                $address->postCode = $_POST['data']['postal_code'];
                $address->address = $_POST['data']['address'];
                $address->phone = $_POST['data']['phone_1'];

                $user = new \core_usermanager_data_User();
                $user->emailAddress = $_POST['data']['email'];
                $user->birthDay = $_POST['data']['birthday'];
                $user->password = "dfsafasd#¤#cvsdfgdfasdfasf";
                $user->fullName = $_POST['data']['name_1'];
                $user->cellPhone = $_POST['data']['phone_1'];
                $user->address = $address;
                if($this->isCompany()) {
                    $user->isPrivatePerson = "false";
                }
                $user = $this->getApi()->getUserManager()->createUser($user);

                $order = $this->getApi()->getOrderManager()->createOrderByCustomerReference($user->referenceKey);
            }
            $_GET['orderProcessed'] = true;
            $_GET['orderId'] = $order->id;
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
        $prices = array();
        foreach($rooms as $room) {
            /* @var $room \core_productmanager_data_Product */
            if(isset($roomtypes[$room->sku])) {
                $prices[] = $room->price;
                $allrooms[] = $room;
            }
        }
        asort($prices);
        
        $sortedRooms = array();
        foreach($prices as $index => $price) {
            $sortedRooms[] = $allrooms[$index];
        }
        
        
        return $sortedRooms;
    }
    
    public function validateAndContinueToPayment() {
        $this->setBookingData();
        foreach($this->getBookingData() as $index => $test) {
            $valid = true;
            if($this->validateInput($index)) {
                $valid = false;
                break;
            }
        }
        if($valid) {
            $this->continueToPayment();
        }
    }
    
    public function getPost($name) {
        if(isset($_POST['data'][$name])) {
            return $_POST['data'][$name];
        }
        return "";
    }
    
    public function validateInput($name) {
        if(isset($_GET['subpage']) && $_GET['subpage'] == "summary" || (isset($_POST['event']) && $_POST['event'] == 'setBookingData')) {
            return "";
        }
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
        return $this->getCleaningPrice() + $this->getRoomPrice() + $this->getRoomTaxes() + $this->getCleaningTaxes();
    }

    public function hasErrors() {
        return $this->invalid;
    }

    public function setBookingData() {
        if($_POST['data']['company'] == "false") {
            $_POST['data']['mvaregistered'] = "false";
        }
        
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

    public function getReferenceKey() {
        if(isset($_POST['data']['referencenumber'])) {
            return $_POST['data']['referencenumber'];
        }
        return "";
    }

    public function hasValidSelection() {
        $count = $this->checkavailabilityFromSelection();
        $isvalid = true;
        $this->errors = array();
        $numberOfDays = $this->getDayCount();
        if(((int)$count < (int)$this->getRoomCount())) {
            $errorText = $this->__w("We are sorry, but there is not enough available rooms for your selection, please select a different one.");
            if($this->getServiceType() == "storage") {
                $errorText = $this->__w("Sorry, we are out of this type of storage, please select a different one.");
            }
            $this->errors[] = $errorText;
            $isvalid = false;
        }
        if((int)$this->getMinumRental() > (int)$numberOfDays) {
            if(!$this->getServiceType() == "storage") {
                $errorText = $this->__w("Sorry, your rental is too short, it has to be longer then {days} days of rental.");
                $errorText = str_replace("{days}", $this->getMinumRental(), $errorText);
                $this->errors[] = $errorText;
                $isvalid = false;
            }
        }
        return $isvalid;
    }

    public function checkavailabilityFromSelection() {
        $product = $this->getProduct();
        return $this->getApi()->getHotelBookingManager()->checkAvailable($this->getStart(),$this->getEnd(),$product->sku);
    }

    public function hasPaymentAppAdded() {
        $payment = $this->getFactory()->getApplicationPool()->getAllPaymentInstances();
        return sizeof($payment) > 0;
    }

    public function getCleaningTaxes() {
        if(isset($this->getCleaningOption()->taxGroupObject)) {
            return $this->getRoomPrice() * ($this->getCleaningOption()->taxGroupObject->taxRate / 100);
        }
        return 0;
    }

    public function isCompany() {
        return $this->getPost("company") == "true";
    }

    public function isMvaRegistered() {
        return $this->getPost("mvaregistered") == "true";
    }

    public function getMinumRentalTexted() {
        $days = $this->getMinumRental();
        if(!$days || $days == 1) {
            return "";
        }
        if($days > 30) {
            $days /= 30;
            if($days > 1) {
                $days .=  " " . $this->__w("months");
            } else {
                $days .=  " " . $this->__w("month");
            }
        } else {
            if($days > 1) {
                $days .=  " " . $this->__w("day");
            } else {
                $days .=  " " . $this->__w("days");
            }
        }
        return $days;
    }

}
?>
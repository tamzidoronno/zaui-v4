<?php

namespace ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2;

class Hotelbooking extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $failedReservation = false;
    var $invalid = false;
    var $errors = array();
    var $config;
    var $parkingProduct = null;
    var $validationNeeded = null;

    function __construct() {
        
    }

    public function getStartMaxDays() {
        return $this->getConfig()->maxRentalDaysAhead;
    }

    public function loadSettings() {
        $this->includefile("settings");
    }
    
    public function getDayCount() {
        $bookingData = $this->getBookingData();
        $reference = $bookingData->references[0];
        return round((strtotime($reference->endDate) - strtotime($reference->startDate))/86400);
    }
    
    public function getCustomerType() {
        $userData = $this->getUserData();

        if(isset($userData['customer_type'])) {
            return $userData['customer_type'];
        }
        
        return "";
    }

    public function setConfig() {
        $settings = $this->getApi()->getHotelBookingManager()->getBookingConfiguration();
        $settings->name = $_POST['data']['name'];
        $settings->type = $_POST['data']['type'];
        $settings->minRentalDays = $_POST['data']['rental_days'];
        $settings->maxRentalDaysAhead = $_POST['data']['start_max_days'];
        $settings->roomThumbNails = $_POST['data']['display_room_thumbnail'];
        $settings->showReferenceNumber = $_POST['data']['show_referencenumber'];
        $settings->displayHeardAboutUs = $_POST['data']['show_heardaboutus'];
        $settings->parkingSpots = $_POST['data']['parking_spots'];
        $settings->extraBookingInformation = $_POST['data']['extra_booking_information'];
        $settings->summaryPage = $_POST['data']['summaryPage'];
        $settings->companyPage = $_POST['data']['companyPage'];
        $settings->continuePage = $_POST['data']['contine_page'];
        $settings->maxRentalDays = $_POST['data']['max_rental_days'];
        $this->getFactory()->getApi()->getHotelBookingManager()->setBookingConfiguration($settings);
    }
    
    public function getRoomTaxes() {

        if ($this->getProduct()->privateExcluded && !$this->isMvaRegistered()) {
            return 0;
        }

        if (isset($this->getProduct()->taxGroupObject)) {
            return $this->getRoomPrice() * ($this->getProduct()->taxGroupObject->taxRate / 100);
        }
        return 0;
    }

    /**
     * @return \core_hotelbookingmanager_GlobalBookingSettings 
     */
    public function getConfig() {
        if(!$this->config) {
            $this->config = $this->getApi()->getHotelBookingManager()->getBookingConfiguration();
        }
        return $this->config;
    }
    
    public function getPartnerText() {
        $partnerText = $this->__w("* Maximum number of days of rental is {maxDays} days, if you need an extended stay or have a partnership deal with us, click <a href='?page={partnerPage}' class='partnerlink'>here</a> to use our specially designed form for that.");
        $partnerText = str_replace("{partnerPage}", $this->getConfig()->companyPage, $partnerText);
        $partnerText = str_replace("{maxDays}", $this->getConfig()->maxRentalDays, $partnerText);
        return $partnerText;
    }
    
    public function getProjectName() {
        return $this->getConfig()->name;
    }

    public function getDescription() {
        return "Hotelbooking";
    }

    function getNumberOfAvailableRooms($type, $needHandicap=false) {
        $product = $this->getProduct();
        $additonal = new \core_hotelbookingmanager_AdditionalBookingInformation();
        $additonal->needHandicap = $this->getNeedHandicap();
        if($needHandicap) {
            $additonal->needHandicap = true;
        }

        return $this->getApi()->getHotelBookingManager()->checkAvailable($this->getStart(), $this->getEnd(), $type, $additonal);
    }
    
    function fetchAppids($cells, $apps) {
        foreach($cells as $cell) {
            if($cell->appId) {
                $apps[] = $cell->appId;
            }
            if(sizeof($cell->cells)) {
                $apps = array_merge($apps, $this->fetchAppids($cell->cells, $apps));
            }
        }
        return $apps;
    }
    
    function printToolTip($product) {
        $page = $this->getApi()->getPageManager()->getPage($product->pageId);
        
        $layout = $page->layout;
        $appIds = $this->fetchAppids($layout->areas->body, []);
        
        echo "<span class='tooltip' data-productid='".$product->id."'>";
        echo "<i class='fa fa-close' style='position:absolute; right: 10px; top: 10px;'></i>";
        foreach($appIds as $app) {
            if(!$app) {
                continue;
            }
            $appConfig = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($app);
            $appInstance = $this->getFactory()->getApplicationPool()->createAppInstance($appConfig);
            if($appInstance instanceof \ns_320ada5b_a53a_46d2_99b2_9b0b26a7105a\ContentManager) {
                echo $this->getApi()->getContentManager()->getContent($app);
            }
        }
        echo "</span>";
    }

    function checkavailability() {
        $start = strtotime($_POST['data']['start']);
        $end = strtotime($_POST['data']['stop']);
        
        $additional = new \core_hotelbookingmanager_AdditionalBookingInformation();
        $additional->roomProductId = $_POST['data']['roomProduct'];
        $this->getManager()->updateAdditionalInformation($additional);
        $this->getManager()->reserveRoom($start, $end, 1);
    }
    
    function getManager() {
        return $this->getApi()->getHotelBookingManager();
    }
    
    function calculateProgressivePrice($productId) {
        $dayCount = $this->getDayCount();
        $cartMgr = $this->getApi()->getCartManager();
        $cartMgr->clear();
        $cartMgr->addProductItem($productId, $dayCount);
        return $cartMgr->getCartTotalAmount() / $dayCount;
    }
    
    function validateReferenceKey() {
        if(!$this->validationNeeded) {
            return "";
        }
        
        $additional = $this->getAdditionalInfo();
        if(!$additional->customerReference) {
            return "invalid";
        }
        if(!$this->getApi()->getUserManager()->doesUserExistsOnReferenceNumber($additional->customerReference)) {
            return "invalid";
        }
        return "";
    }
    
    function validateInput($name) {
        if(!$this->validationNeeded) {
            return "";
        }
        
        $data = $this->getUserData();
        if(!$data[$name]) {
            return "invalid";
        }
        
        if($name == "checklicenagreement" && $data['checklicenagreement'] == "false") {
            return "invalid";
        }
        
        return "";
    }
    
    function validateVisitor($field, $data) {
        if(!$this->validationNeeded) {
            return "";
        }
        
        if(!$data->{$field}) {
            return "invalid";
        }
        
        if($field == "name") {
            if(!stristr($data->name, " ")) {
                return "invalid";
            }            
        }
        
        if($field == "email") {
            if(!stristr($data->email, "@")) {
                return "invalid";
            }            
        }
        
        if($field == "phone") {
            if(!$this->startsWith($data->phone, "9") && !$this->startsWith($data->phone, "4")) {
                return "invalid";
            }
            if(strlen($data->phone) != 8) {
                return "invalid";
            }
        }
        
    }
    
    function startsWith($haystack, $needle) {
         $length = strlen($needle);
         return (substr($haystack, 0, $length) === $needle);
    }

    function endsWith($haystack, $needle) {
        $length = strlen($needle);
        if ($length == 0) {
            return true;
        }
        return (substr($haystack, -$length) === $needle);
    }
    
    
    public function getProductId() {
        return $this->getAdditionalInfo()->roomProductId;
    }

    public function getProduct() {
        return $this->getApi()->getProductManager()->getProduct($this->getProductId());
    }

    public function getStart() {
        return strtotime($this->getBookingData()->references[0]->startDate);
    }

    public function getEnd($realEndCount=false) {
        return strtotime($this->getBookingData()->references[0]->endDate);
    }
    
    public function getName() {
        return "Hotelbooking";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        $this->config = $this->getApi()->getHotelBookingManager()->getBookingConfiguration();
    }

    public function render() {
        //this variable is set from $this->complete*Checkout()
        if (isset($_GET['orderProcessed'])) {
            if($this->partnerShipChecked()) {
                echo "<h1 style='text-align:center;text-transform:uppercase;'>";
                if($this->failedReservation) {
                    echo $this->__w("Something went wrong while ordering, please contact us");
                } else {
                    echo $this->__w("Your reservations has now been completed");
                }
                echo "</h1>";
            } else {
                $this->completeCheckout();
            }
            return;
        }
        
        
        if($this->getPage()->javapage->id == "home") {
            $this->includefile("booking_part1");
        } else if($this->getPage()->javapage->id == $this->getConfig()->continuePage) {
            $this->includefile("booking_part2");
        } else if($this->getPage()->javapage->id == $this->getConfig()->summaryPage) {
            $this->getApi()->getHotelBookingManager()->updateCart();
            $this->includefile("booking_part3");
        } else if($this->getPage()->javapage->id == $this->getConfig()->companyPage) {
            $this->includefile("companybooking");
        } else {
            echo "Page not configured";
        }
    }

    public function updateCalendarDate() {
        $type = $_POST['data']['type'];
        $time = $_POST['data']['time'];
        if ($type == "startDate") {
            if ($time > $this->getEnd()) {
                $this->setEndDate($time + 86400);
            }
            $this->setStartDate($time);
        }
        if ($type == "endDate") {
            $this->setEndDate($time);
        }
    }

    public function navigateMonthCalendar() {
        $time = strtotime("20" . $_POST['data']['year'] . "-" . $_POST['data']['month'] . "-01");
        $type = $_POST['data']['type'];
        $this->printCalendar($time, $this->getStart(), $type, false);
    }

    
    function getDates($year) {
        $dates = array();

        for ($i = 1; $i <= 366; $i++) {
            $month = date('m', mktime(0, 0, 0, 1, $i, $year));
            $wk = date('W', mktime(0, 0, 0, 1, $i, $year));
            $wkDay = date('D', mktime(0, 0, 0, 1, $i, $year));
            $day = date('d', mktime(0, 0, 0, 1, $i, $year));

            $dates[$month][$wk][$day] = $i;
        }

        return $dates;
    }

    public function printCalendar($time, $checkbefore, $id, $setSelected = true) {
        $month = date("m", $time);
        $timestamp = mktime(0, 0, 0, $month, 1, date("y", $time));
        $maxday = date("t", $timestamp);
        $thismonth = getdate($timestamp);
        $startday = $thismonth['wday'];
        $year = date('y', $time);
        if ($startday == 0) {
            $startday = 7;
        }
        $monthText = date("M", $time) . " " . $year;
        $text = $this->__w("When do you check in?");

        if($id == "endDate") {
            $text = $this->__w("When do you check out?");
        }
        //Guess navigating between months was a bad idea, why do something that does not look good design wise.
//        echo "<div class='cal_header cal_nav' type='$id' year='$year' month='$month'><i class='fa fa-arrow-left calnav' style='float:left;cursor:pointer;' navigation='prev'></i>" . $monthText . "<i class='fa fa-arrow-right calnav' style='float:right;cursor:pointer;' navigation='next'></i></div>";
        echo "<div class='cal_header' type='$id' year='$year' month='$month'>$text</div>";
        echo "<div class='calspacing'></div>";
        echo "<table width='100%' class='booking_table' cellspacing='0' cellpadding='0'>";
        echo "<tr>";
        echo "<th>" . $this->__w("Mo") . "</th>";
        echo "<th>" . $this->__w("Tu") . "</th>";
        echo "<th>" . $this->__w("We") . "</th>";
        echo "<th>" . $this->__w("Th") . "</th>";
        echo "<th>" . $this->__w("Fr") . "</th>";
        echo "<th>" . $this->__w("Sa") . "</th>";
        echo "<th>" . $this->__w("Su") . "</th>";
        echo "</tr>";
        echo "<tr>";
        for ($i = 1; $i < ($maxday + $startday); $i++) {
            if ($i < $startday)
                echo "<td></td>";
            else {
                $class = "";
                $day = $i - $startday + 1;
                if (date('d', $time) == (($i - $startday) + 1) && $setSelected) {
                    $class = "selected";
                }
                $aftertime = strtotime($year . "-" . $month . "-" . $day);
                if ($checkbefore) {
                    if ($checkbefore > $aftertime || (date("d", $checkbefore) == date("d", $aftertime) && date("m", $checkbefore) == date("m", $aftertime))) {
                        $class .= " disabled";
                    }
                }

                if ($aftertime < (time() - 86400)) {
                    $class .= " disabled";
                }

                if ($id == "startDate") {
                    if ($aftertime > (time() + ($this->getStartMaxDays() * 86400))) {
                        $class .= " disabled";
                    }
                }

                $fieldtime = strtotime($year . "-" . $month . "-" . $day);

                echo "<td align='center' valign='middle' height='20px'><span class='cal_field $class' time='$fieldtime'>" . $day . "</span></td>";
            }
            if (($i % 7) == 0)
                echo "</tr><tr>";
        }
        echo "</tr>";
        echo "</table>";
    }

    public function updateRoomCount($count = false) {
        if(!$count) {
            $count = $_POST['data']['count'];
        }
        $start = $this->getStart();
        $end = $this->getEnd();
        $this->getApi()->getHotelBookingManager()->reserveRoom($start, $end, $count);
        $this->invalidateBookingData();
    }
    
    public function changeProduct($productId = false) {
        if(!$productId) {
            $productId = $_POST['data']['productid'];
        }
        $additional = $this->getAdditionalInfo();
        $additional->roomProductId = $productId;
        $this->getManager()->updateAdditionalInformation($additional);
        //To trigger a new reservation.
        $this->updateRoomCount($this->getRoomCount());
        $this->invalidateBookingData();
    }

    public function setStartDate($start) {
        $end = $this->getEnd();
        $count = $this->getRoomCount();
        $additional = $this->getAdditionalInfo();
        $productId = $this->getProductId();
        $this->getApi()->getHotelBookingManager()->reserveRoom($start, $end, $count);
        $this->invalidateBookingData();
    }

    public function setEndDate($end) {
        $start = $this->getStart();
        $count = $this->getRoomCount();
        $additional = $this->getAdditionalInfo();
        $productId = $this->getProductId();
        $this->getApi()->getHotelBookingManager()->reserveRoom($start, $end, $count);
        $this->invalidateBookingData();
    }
    
    public function updateNeedHandicap() {
        $additional = $this->getAdditionalInfo();
        if($_POST['data']['need'] == "true") {
            $additional->needHandicap = true;
        } else {
            $additional->needHandicap = false;
        }
        $this->getManager()->updateAdditionalInformation($additional);
        $this->invalidateBookingData();
    }

    public function getRoomCount() {
        return sizeof($this->getBookingData()->references[0]->roomsReserved);
    }
    
    public function getRoomsReserved() {
        return $this->getBookingData()->references[0]->roomsReserved;
    }

    public function getAdditionalInfo() {
        return $this->getBookingData()->additonalInformation;
    }

    public function doPartnerBooking() {
        $days = $_POST['data']['days'];
        $year = date("Y", time());
        $productId = $_POST['data']['product'];
        
        $this->getManager()->clearBookingReservation();
        
        $additional = new \core_hotelbookingmanager_AdditionalBookingInformation();
        $additional->isPartner = true;
        $additional->roomProductId = $productId;
        $this->getManager()->updateAdditionalInformation($additional);
        
        $dateRange = [];
        $reservations = array();
        $errors = false;
        for($j = 0; $j < 2; $j++) {
            for($i = 1; $i <= 365; $i++) {
                $offset = date("$i-$year");
                if(in_array($offset, $days)) {
                    $dateRange[] = $offset;
                } else if(sizeof($dateRange) > 0) {
                    $start = $dateRange[0];
                    $start = explode("-", $start);
                    $start = strtotime("1 Jan " . ($start[1]) . " +" . ($start[0]-1) . " day");
                    $end = $dateRange[sizeof($dateRange)-1];
                    $end = explode("-", $end);
                    $endDay = $end[0]-1;
                    if(sizeof($dateRange) == 1) {
                        $endDay++;
                    }
                    $end = strtotime("1 Jan " . ($end[1]) . " +" . ($endDay) . " day");
                    if(!$this->checkAvailabilityOnRange($start, $end, $productId)) {
                        $text = $this->__w("Sorry, we do not have any room available between {start} and {end} of this type. Try another one.");
                        $text = str_replace("{start}", date("d-m-Y", $start),$text);
                        $text = str_replace("{end}", date("d-m-Y", $end), $text);
                        echo $text . "<br>";
                        $errors = true;
                    } else {
                        $toReserve = new \stdClass();
                        $count = sizeof($dateRange)-1;
                        if($count == 0) {
                            $count = 1;
                        }
                        $toReserve->start = $start;
                        $toReserve->end = $end;
                        $toReserve->count = $count;
                        $reservations[] = $toReserve;
                    }
                    $dateRange = [];
                }
            }
            $year++;
        }
        if(!$errors) {
            
            foreach($reservations as $reservation) {
                $this->getManager()->reserveRoom($reservation->start, $reservation->end, 1);
            }
            
            echo "ok";
        }
    }
    
    public function hasAvailableParkingSpots() {
        $spots = $this->getApi()->getHotelBookingManager()->checkAvailableParkingSpots($this->getStart(), $this->getEnd());
        return $spots;
    }
    
    public function checkFailedReservation() {
        if (isset($_GET['failedreservation'])) {
            return true;
        }
        return $this->failedReservation;
    }

    public function sortRoomByPrice($a, $b) {

        if ($a->price > $b->price) {
            return 1;
        }
        
        if ($a->price < $b->price) {
            return -1;
        }
        
        return 0;
    }
    
    public function getHotelRooms() {
        $products = $this->getApi()->getProductManager()->getAllProducts();
        $roomProductIds = $this->getApi()->getHotelBookingManager()->getRoomProductIds();
        $allrooms = array();
        $prices = array();
        foreach($products as $product) {
            /* @var $product \core_productmanager_data_Product */
            if(in_array($product->id, $roomProductIds)) {
                $prices[] = $product->price;
                $allrooms[] = $product;
            }
        }

        $sortedRooms = array();
        $noPrices = array();
        
        foreach ($prices as $index => $price) {
            if ($price) {
                $sortedRooms[] = $allrooms[$index];
            } else {
                $noPrices[] = $allrooms[$index];
            }
        }
        
        usort($sortedRooms, array("ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2\Hotelbooking","sortRoomByPrice"));
        $sortedRooms = array_merge($sortedRooms, $noPrices);
        
        return $sortedRooms;
    }

    public function getPost($name) {
        if($name == "referenceNumber") {
            return $this->getAdditionalInfo()->customerReference;
        }
        
        $userData = $this->getUserData();
        if (isset($userData[$name])) {
            return $userData[$name];
        }
        return "";
    }

    public function getNeedHandicap() {
        return $this->getAdditionalInfo()->needHandicap;
    }

    public function hasErrors() {
        return $this->invalid;
    }

    /**
     * @return \core_hotelbookingmanager_UsersBookingData
     */
    public function getBookingData() {
        if(!isset($this->currentData) || !$this->currentData) {
            $this->currentData = $this->getApi()->getHotelBookingManager()->getCurrentUserBookingData();
        }
        return $this->currentData; 
    }
    
    public function invalidateBookingData() {
        $this->currentData = false;
    }
    
    public function hasPaymentAppAdded() {
        $cartManager = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
        $payment = $cartManager->getPaymentApplications();
        return sizeof($payment) > 0;
    }
    
    public function createUser() {
        $data = $this->getBookingData();
        $userData = $this->getUserData();
        $address = $this->getApiObject()->core_usermanager_data_Address();
        
        $visitor = $data->references[0]->roomsReserved[0]->visitors[0];
        $name = $visitor->name;
        $email = $visitor->email;
        $phone = $visitor->phone;
        
        $address->fullName = $name;
        $address->city = $userData['city'];
        $address->postCode = $userData['postal_code'];
        $address->address = $userData['address'];
        $address->phone = $phone;

        $user = new \core_usermanager_data_User();
        $user->emailAddress = $email;
        $user->birthDay = $userData['birthday'];
        $user->password = "dfsafasd#¤#cvsdfgdfasdfasf";
        $user->fullName = $name;
        $user->cellPhone = $phone;
        $user->address = $address;
        
        if ($this->getCustomerType() == "company") {
            $user->isPrivatePerson = "false";
        }
        return $this->getApi()->getUserManager()->createUser($user);
    }

    public function checkAvailabilityOnRange($start, $end, $productId) {
        $additional = new \core_hotelbookingmanager_AdditionalBookingInformation();
        return $this->getApi()->getHotelBookingManager()->checkAvailable($start, $end, $productId, $additional);
    }

    public function validateBookingData() {
        $this->validationNeeded = true;
        
        $bookingData = $this->getBookingData();
        if(sizeof($bookingData) == 0) {
            return false;
        }
        
        foreach($bookingData->references[0]->roomsReserved as $roomReserved) {
            if(!isset($roomReserved->visitors[0])) {
                return false;
            }
            foreach($roomReserved->visitors[0] as $key => $val) {
                if($this->validateVisitor($key, $roomReserved->visitors[0])) {
                    return false;
                }
            }
        }
        
        foreach($this->getUserData() as $key => $val) {
            if($this->validateInput($key)) {
                return false;
            }
        }
        
        $additional = $bookingData->additonalInformation;
        
        if($this->partnerShipChecked()) {
            $partnerType = $additional->partnerType;
            if(!$partnerType) {
                return false;
            }
            if(!$additional->customerReference && $partnerType == "existing") {
                return false;
            }
            if($this->validateReferenceKey() && $partnerType == "existing") {
                return false;
            }
        }
        
       
        return true;
    }

    public function completePartnerCheckout() {
        $valid = $this->validateBookingData();
        if(!$valid) {
            return;
        }
        
        $additional = $this->getAdditionalInfo();
        if($additional->partnerType == "new") {
            $user = $this->createUser();
            $additional->customerReference = $user->referenceKey;
            $this->getManager()->updateAdditionalInformation($additional);
        }
        
        $this->getManager()->completeOrder();
        $_GET['orderProcessed'] = true;
    }
    
    public function completeRegularCheckout() {
        $valid = $this->validateBookingData();
        if(!$valid) {
            return;
        }
        
        $user = $this->createUser();
        $additional = $this->getAdditionalInfo();
        $additional->customerReference = $user->referenceKey;
        $this->getManager()->updateAdditionalInformation($additional);
        
        $orderId = $this->getApi()->getHotelBookingManager()->completeOrder();
        $_GET['orderProcessed'] = true;
        $_GET['orderId'] = $orderId;
    }
    
    public function completeCheckout() {
        $cartManager = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
        $payment = null;

        foreach ($cartManager->getPaymentApplications() as $paymenti) {
            if ($paymenti->applicationSettings->id === "def1e922-972f-4557-a315-a751a9b9eff1") {
                $payment = $paymenti;
            }
        }

        if ($payment) {
            //Orderid is set in $this->continueToPayment()
            $payment->order = $this->getApi()->getOrderManager()->getOrder($_GET['orderId']);
            $payment->initPaymentMethod();
            $payment->preProcess();
        }
    }

    public function setBookingData() {
        $index = 0;
        $result = new \stdClass();
        if(isset($_POST['data']['room'])) {
            foreach($_POST['data']['room'] as $roomId => $visitorInfo) {
                $visitor = new \core_hotelbookingmanager_Visitors();
                $visitor->email = $visitorInfo['email'];
                $visitor->phone = $visitorInfo['phone'];
                $visitor->name = $visitorInfo['name'];
                $vistitorsOnRoom = array();
                $vistitorsOnRoom[] = $visitor;
                $result->{$index} = $vistitorsOnRoom;
                $index++;
            }
        }
        $this->getApi()->getHotelBookingManager()->setVistorData($result);
        
        if(isset($_POST['data']['userData'])) {
            $userData = $_POST['data']['userData'];
            $_SESSION['hotelbooking']['userData'] = serialize($userData);
        }
        
        $info = $this->getAdditionalInfo();
        if(isset($_POST['data']['partner_type'])) {
            $info->partnerType = $_POST['data']['partner_type'];
        }
        if(isset($_POST['data']['referenceNumber'])) {
            $info->customerReference = $_POST['data']['referenceNumber'];
        }
        $this->getManager()->updateAdditionalInformation($info);
    }
    
    public function getUserData() {
        if(isset($_SESSION['hotelbooking']['userData'])) {
            return unserialize($_SESSION['hotelbooking']['userData']);
        }
        
        return array();
    }
    
    
    public function getVisitorData() {
        $bookingData = $this->getBookingData();
        $count = $this->getRoomCount();
        $infodata = array();
        for($i = 1; $i <= $count; $i++) {
            $info = new \core_hotelbookingmanager_RoomInformation();
            $visitor = new \core_hotelbookingmanager_Visitors();
            if(isset($bookingData['name_' . $i])) {
                $visitor->name = $bookingData['name_' . $i];
                $visitor->phone = $bookingData['phone_' . $i];
                $visitor->email = $bookingData['email_' . $i];
            }
            $info->visitors = array();
            $info->visitors[] = $visitor;
            $infodata[] = $info;
        }
        return $infodata;
    }

    public function partnerShipChecked() {
        if(isset($this->getAdditionalInfo()->isPartner)) {
            return $this->getAdditionalInfo()->isPartner;
        }
        return false;
    }

}
?>
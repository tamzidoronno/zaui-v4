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

    public function updatePersonCount() {
        $this->setPersonCount($_POST['data']['count']);
    }

    public function getStartMaxDays() {
        return $this->getConfig()->maxRentalDaysAhead;
    }

    public function loadSettings() {
        $this->includefile("settings");
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
        $this->getFactory()->getApi()->getHotelBookingManager()->setBookingConfiguration($settings);
    }

    public function updateNeedHandicap() {
        $this->setNeedHandicap($_POST['data']['need']);
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
    
    public function getParkingSpots() {
        return $this->getConfig()->parkingSpots;
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

    function getDayCount($realDayCount=false) {
        return round(($this->getEnd() - $this->getStart()) / 86400);
    }

    function checkavailability() {
        $start = strtotime($_POST['data']['start']);
        $end = strtotime($_POST['data']['stop']);
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['roomProduct']);

        $this->setStartDate($start);
        $this->setEndDate($end);
        $this->setProductId($product->id);
    }
    
    function validateInput($name) {
        if(!$this->validationNeeded) {
            return "";
        }
        
        $bookingData = $this->getBookingData();
        if(!isset($bookingData[$name]) || $bookingData[$name] == "") {
            return "invalid";
        }
        
        if($name == "referenceNumber") {
            $number = $this->getPost($name);
            if(!$this->getApi()->getUserManager()->doesUserExistsOnReferenceNumber($number)) {
                return "";
            }
            return "invalid";
        }
        
        
        return "";
    }
    
    public function getProductId() {
        return $_SESSION['hotelbooking']['product'];
    }

    public function getProduct() {
        return $this->getApi()->getProductManager()->getProduct($this->getProductId());
    }

    public function getStart() {
        return $_SESSION['hotelbooking']['start'];
    }

    public function getEnd($realEndCount=false) {
        // End-date is always x number of days after startdate when servicetype = storage
        return $_SESSION['hotelbooking']['end'];
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
                echo "No payment required... checkout completed";
            } else {
                $this->completeCheckout();
            }
            return;
        }
        
        if(isset($_GET['partner'])) {
            $_SESSION['partner'] = $_GET['partner'];
        }
        
        if($this->getPage()->javapage->id == "home") {
            $this->includefile("booking_part1");
        } else if($this->getPage()->javapage->id == $this->getConfig()->continuePage) {
            $this->includefile("booking_part2");
        } else if($this->getPage()->javapage->id == $this->getConfig()->summaryPage) {
            $this->updateCartObject();
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

    public function updateRoomCount() {
        $this->setRoomCount($_POST['data']['count']);
    }

    public function getRoomCount() {
        if (isset($_SESSION['hotelbooking']['count'])) {
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
        if($_POST['data']['product'] == "no") {
            unset($_SESSION['hotelbooking']['cleaning']);
            return;
        }
        $_SESSION['hotelbooking']['cleaning'] = $_POST['data']['product'];
    }

    public function setParkingOption() {
        if($_POST['data']['parking'] == "false") {
            unset($_SESSION['hotelbooking']['parking']);
        } else {
            $_SESSION['hotelbooking']['parking'] = $_POST['data']['parking'];
        }
    }

    public function getParking() {
        if(isset($_SESSION['hotelbooking']['parking'])) {
            return $_SESSION['hotelbooking']['parking'];
        }
        return false;
    }
    
    public function getParkingProduct() {
        if($this->parkingProduct != null) {
            return $this->parkingProduct;
        }
        $products = $this->getApi()->getProductManager()->getAllProducts();
        foreach($products as $product) {
            if($product->sku == "parking") {
                $this->parkingProduct = $product;
                break;
            }
        }
        return $this->parkingProduct;
    }
    
    public function getBookedPartnerDays() {
        if(isset($_SESSION['bookedPartnerDays'])) {
            return $_SESSION['bookedPartnerDays'];
        }
        
        return array();
    }
    
    public function doPartnerBooking() {
        $days = $_POST['data']['days'];
        $_SESSION['bookedPartnerDays'] = $days;
        $year = date("Y", time());
        $productId = $_POST['data']['product'];
        $this->setProductId($productId);
        
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
                    $end = strtotime("1 Jan " . ($end[1]) . " +" . $end[0] . " day");
                    if(!$this->checkAvailabilityOnRange($start, $end, $productId)) {
                        $text = $this->__w("Sorry, we do not have any room available between {start} and {end} of this type. Try another one.");
                        $text = str_replace("{start}", date("m-d-Y", $start),$text);
                        $text = str_replace("{end}", date("m-d-Y", $end), $text);
                        echo $text . "<br>";
                        $errors = true;
                    } else {
                        $toReserve = new \stdClass();
                        $toReserve->start = $start;
                        $toReserve->end = $end;
                        $toReserve->count = sizeof($dateRange);
                        $reservations[] = $toReserve;
                    }
                    $dateRange = [];
                }
            }
            $year++;
        }
        if(!$errors) {
            $this->setpartnerReservatations($reservations);
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

    public function getCleaningOption() {
        if (isset($_SESSION['hotelbooking']['cleaning'])) {
            return $_SESSION['hotelbooking']['cleaning'];
        }
        return "";
    }

    public function getCleaningProudcts() {
        $products = $this->getApi()->getProductManager()->getAllProducts();
        $vasking = array();
        foreach ($products as $product) {
            if ($product->sku == "vask") {
                $vasking[] = $product;
            }
        }

        return $vasking;
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

    public function validateAndContinueToPayment() {
        $this->setBookingData();
        $valid = $this->validateBookingData();
        if ($valid) {
            $this->continueToPayment();
        }
    }

    public function getPost($name) {
        if (isset($_POST['data'][$name])) {
            return $_POST['data'][$name];
        }
        return "";
    }

    public function setPersonCount($count) {
        $_SESSION['hotelbooking']['roomCount'] = $count;
    }

    public function setNeedHandicap($need) {
        $_SESSION['hotelbooking']['needHandicap'] = $need;
    }
    
    public function getNeedHandicap() {
        if(isset($_SESSION['hotelbooking']['needHandicap'])) {
            return $_SESSION['hotelbooking']['needHandicap'] == "true";
        }
        return false;
    }

    public function getPersonCount() {
        if (isset($_SESSION['hotelbooking']['roomCount'])) {
            return $_SESSION['hotelbooking']['roomCount'];
        }
        return 1;
    }

    public function getCleaningPrice() {
        $cleanprodid = $this->getCleaningOption();
        if (!$cleanprodid) {
            return 0;
        }
        $cleaningproduct = $this->getApi()->getProductManager()->getProduct($cleanprodid);
        $count = (int) ($this->getDayCount() / $cleaningproduct->stockQuantity);

        $cleaningprice = $count * $cleaningproduct->price;
        return $cleaningprice;
    }

    public function getRoomPrice() {
        return $this->getDayCount() * $this->getProduct()->price * $this->getRoomCount();
    }

    public function getTotal() {
        return $this->getCleaningPrice() + $this->getRoomPrice() + $this->getRoomTaxes() + $this->getCleaningTaxes() + $this->getParkingPrice();
    }

    public function hasErrors() {
        return $this->invalid;
    }

    public function clearBookingData() {
        unset($_SESSION['booking_data']);
    }

    public function setBookingData() {
        print_r($_POST['data']);
        $_SESSION['booking_data'] = serialize($_POST['data']);
    }

    public function getBookingData() {
        return unserialize($_SESSION['booking_data']);
    }

    public function loadBookingData() {
        if (isset($_SESSION['booking_data'])) {
            $_POST['data'] = unserialize($_SESSION['booking_data']);
        }
    }

    public function getContactData() {
        $booked = $this->getBookingData();
        $names = array();
        $phones = array();
        for ($i = 0; $i < 10; $i++) {
            if (isset($booked['name_' . $i])) {
                $names[] = $booked['name_' . $i];
                $phones[] = $booked['phone_' . $i];
            }
        }
        $contact = new \core_hotelbookingmanager_ContactData();
        $contact->names = $names;
        $contact->phones = $phones;
        return $contact;
    }

    public function partnerShipChecked() {
        if (isset($_SESSION['partner'])) {
            return $_SESSION['partner']=="true";
        }
        return false;
    }

    public function getReferenceKey() {
        if (isset($_POST['data']['referencenumber'])) {
            return $_POST['data']['referencenumber'];
        }
        return "";
    }

    public function hasValidSelection() {
        if($this->partnerShipChecked()) {
            return true;
        }
        
        $count = $this->checkavailabilityFromSelection();
        $isvalid = true;
        $this->errors = array();
        $numberOfDays = $this->getDayCount();
        if (((int) $count < (int) $this->getRoomCount())) {
            $errorText = $this->__w("We are sorry, but there is not enough available rooms for your selection, please select a different one.");
            $this->errors[] = $errorText;
            $isvalid = false;
        }
        return $isvalid;
    }

    public function checkavailabilityFromSelection() {
        $product = $this->getProduct();
        $additonal = new \core_hotelbookingmanager_AdditionalBookingInformation();
        $additonal->needHandicap = $this->getNeedHandicap();
        
        return $this->getApi()->getHotelBookingManager()->checkAvailable($this->getStart(), $this->getEnd(), $product->id, $additonal);
    }

    public function hasPaymentAppAdded() {
        $cartManager = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
        $payment = $cartManager->getPaymentApplications();
        return sizeof($payment) > 0;
    }

    public function getCleaningTaxes() {
        if (isset($this->getCleaningOption()->taxGroupObject)) {
            return $this->getRoomPrice() * ($this->getCleaningOption()->taxGroupObject->taxRate / 100);
        }
        return 0;
    }

    public function isCompany() {
        return $this->getPost("customer_type") == "firma";
    }

    public function isPrivate() {
        if(!$this->isCompany()) {
            return true;
        }
//        return $this->getPost("customer_type") == "private";
    }

    public function isMvaRegistered() {
        return $this->getPost("mvaregistered") == "true";
    }

    public function getMinumRentalTexted() {
        $days = $this->getConfig()->minRentalDays;
        if (!$days || $days == 1) {
            return "";
        }
        
        if ($days > 30) {
            $days /= 30;
            if ($days > 1) {
                $days .= " " . $this->__w("months");
            } else {
                $days .= " " . $this->__w("month");
            }
        } else {
            if ($days > 1) {
                $days .= " " . $this->__w("day");
            } else {
                $days .= " " . $this->__w("days");
            }
        }
        return $days;
    }

    public function requestAdminRights() {
        $this->requestAdminRight("UserManager", "getAllUsers", $this->__o("This app need to be able to get the users to check if it has a reference number."));
        $this->requestAdminRight("HotelBookingManager", "updateReservation", $this->__o("Need to update reservation after order has been placed."));
        $this->requestAdminRight("OrderManager", "saveOrder", $this->__o("Must be able to save order to set start and end date"));
    }

    public function includeEcommerceTransaction() {
        $orderId = $_GET['orderId'];
        $order = $this->getApi()->getOrderManager()->getOrder($orderId);
        $taxes = $this->getRoomTaxes();
        $price = $this->getRoomPrice();
        $total = $price + $taxes;

        echo "<script>";
        echo "ga('ecommerce:addTransaction', {
            'id': '" . $order->id . "',
            'affiliation': '" . $this->getProjectName() . "',
            'revenue': '" . $price . "',
            'shipping': '0',
            'tax': '" . $taxes . "'
          });";

        foreach ($order->cart->items as $item) {
            /* @var $product \core_productmanager_data_Product */
            $product = $item->product;

            echo "ga('ecommerce:addItem', {
                'id': '" . $order->incrementOrderId . "',
                'name': '" . $product->name . "',
                'sku': '" . $product->sku . "',
                'category': '',
                'price': '" . $product->price . "',
                'quantity': '1'
              });";
        }

        echo "ga('ecommerce:send');";
        echo "</script>";
    }

    public function getParkingPrice() {
        if(!$this->getParkingProduct()) {
            return 0;
        }
        return $this->getParkingProduct()->price * $this->getDayCount();
    }

    public function getpartnerReservatations() {
        if(!isset($_SESSION['partnerReservations'])) {
            return array();
        }
        return unserialize($_SESSION['partnerReservations']);
    }

    public function setpartnerReservatations($object) {
        $_SESSION['partnerReservations'] = serialize($object);
    }
    
    public function updateCartObject() {
        $mgr = $this->getApi()->getCartManager();
        $mgr->clear();

        if($this->partnerShipChecked()) {
            $partnerReservations = $this->getpartnerReservatations();
            foreach($partnerReservations as $reservation) {
                $mgr->addProductItem($this->getProductId(), $reservation->count, null);
            }
        } else {
            for($i = 0; $i < $this->getRoomCount(); $i++) {
                $mgr->addProductItem($this->getProductId(), $this->getDayCount(), null);
            }
        }
    }

    public function createUser() {
        $address = $this->getApiObject()->core_usermanager_data_Address();
        $address->fullName = $_POST['data']['name_1'];
        $address->city = $_POST['data']['city'];
        $address->postCode = $_POST['data']['postal_code'];
        $address->address = $_POST['data']['address'];
        $address->phone = $_POST['data']['phone_1'];

        $user = new \core_usermanager_data_User();
        $user->emailAddress = $_POST['data']['email_1'];
        $user->birthDay = $_POST['data']['birthday'];
        $user->password = "dfsafasd#¤#cvsdfgdfasdfasf";
        $user->fullName = $_POST['data']['name_1'];
        $user->cellPhone = $_POST['data']['phone_1'];
        $user->address = $address;
        
        if (isset($_POST['data']['mvaregistered'])) {
            $user->mvaRegistered = $_POST['data']['mvaregistered'];
        } else {
            $user->mvaRegistered = "true";
        }
        if ($this->isCompany()) {
            $user->isPrivatePerson = "false";
        }
        return $this->getApi()->getUserManager()->createUser($user);
    }

    public function createOrder($user, $i = null) {
        if ($this->partnerShipChecked()) {
            $order = $this->getApi()->getOrderManager()->createOrderByCustomerReference($this->getReferenceKey());
        } else {
            $order = $this->getApi()->getOrderManager()->createOrderByCustomerReference($user->referenceKey);
        }
        
        if($i != null) {
            $startDate = date('M d, Y h:m:s A', strtotime("+".$i." months", $this->getStart()));
            $endDate = date('M d, Y h:m:s A', strtotime("-1 day",strtotime("+".($i+1)." months", $this->getStart())));
            $order->startDate = $startDate;
            $order->endDate = $endDate;
        } else {
            $order->startDate = date('M d, Y h:m:s A', $this->getStart());
            $order->endDate = date('M d, Y h:m:s A', $this->getEnd());
        }
        
//        $this->startAdminImpersonation("OrderManager", "saveOrder");
        $this->getApi()->getOrderManager()->saveOrder($order);
//        $this->stopImpersionation();
        return $order;
    }

    public function getShowExtraInformation() {
        return $this->getConfig()->extraBookingInformation;
    }

    public function createReservation() {
        $productId = $this->getProduct()->id;
        $start = $this->getStart();
        $end = $this->getEnd();

        $infodata = array();
        $cart = $this->getApi()->getCartManager()->getCart();
        $cartItems = array();
        
        foreach($cart->items as $item) {
            /* @var $item core_cartmanager_data_CartItem */
            if($item->product->id == $productId) {
                $cartItems[] = $item->cartItemId;
            }
        }

        for($i = 1; $i <= $count; $i++) {
            $info = new \core_hotelbookingmanager_RoomInformation();
            $info->cartItemId = $cartItems[$i-1];
            $visitor = new \core_hotelbookingmanager_Visitors();
            $visitor->name = $_POST['data']['name_' . $i];
            $visitor->phone = $_POST['data']['phone_' . $i];
            $visitor->email = $_POST['data']['email_' . $i];
            
            $info->visitors = array();
            $info->visitors[] = $visitor;
            $infodata[] = $info;
        }
        
        $additionaldata = new \core_hotelbookingmanager_AdditionalBookingInformation();
        $additionaldata->needHandicap = $this->getNeedHandicap();
        
        $reference = $this->getApi()->getHotelBookingManager()->reserveRoom($productId, $start, $end, $infodata, $additionaldata);        
        return $reference;
    }

    public function checkAvailabilityOnRange($start, $end, $productId) {
        $additional = new \core_hotelbookingmanager_AdditionalBookingInformation();
        return $this->getApi()->getHotelBookingManager()->checkAvailable($start, $end, $productId, $additional);
    }

    public function validateBookingData() {
        $this->validationNeeded = true;
        foreach ($this->getBookingData() as $index => $test) {
            $valid = true;
            if ($this->validateInput($index)) {
                $valid = false;
                break;
            }
        }
        return $valid;
    }

    public function completePartnerCheckout() {
        $valid = $this->validateBookingData();
        if(!$valid) {
            return;
        }
        
        $bookingData = $this->getBookingData();
        
        $referenceNumber = $bookingData['referenceNumber'];
        
        
        $_GET['orderProcessed'] = true;
    }
    
    public function completeRegularCheckout() {
        $valid = $this->validateBookingData();
        if(!$valid) {
            return;
        }
        
        $reference = $this->createReservation();
        if (($reference) > 0) {
            if (!$this->partnerShipChecked()) {
                $this->getApi()->getHotelBookingManager()->getReservationByReferenceId($reference);
                $cartmgr = $this->getApi()->getCartManager();
                $cartmgr->setReference($reference);
                $user = $this->createUser();
                $order = $this->createOrder($user);
            }
            
            $_GET['orderProcessed'] = true;
            $_GET['orderId'] = $order->id;
        } else {
            $_GET['failedreservation'] = true;
            $this->failedReservation = true;
        }
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
}
?>
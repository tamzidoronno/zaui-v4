<?php
namespace ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73;

class PmsSearchBooking extends \MarketingApplication implements \Application {
    private $channels;
    
    /* @var \core_pmsmanager_PmsBooking */
    public $pmsBooking = null;
    
    /* @var \core_bookingengine_data_Booking */
    public $bookingEngineBooking = null;
    
    public $selectedRoom = null;
    
    function __construct() {
        $this->formatter = new PmsSearchBookingColumnFormatters($this);
    }

    public function getDescription() {   
    }

    public function getName() {
        return "PmsSearchBooking";
    }

    public function formatRoom($room) {
        return $this->formatter->formatRoom($room);
    }
    
    public function formatRoomId($room) {
        return $room->pmsRoomId;
    }
    
    public function formatVistior($room) {
        return $this->formatter->formatVistior($room);
    }
    
    public function formatState($room) {
        return $this->formatter->formatState($room);
    }
    
    public function formatPrice($room) {
        return $this->formatter->formatPrice($room);
    }
    
    public function formatRegDate($room) {
        return $this->formatter->formatRegDate($room);
    }
    
    public function formatStartPeriode($room) {
        return $this->formatter->formatStartPeriode($room);
    }
    
    public function render() {
        $this->setDefaults();
        
        if (!$this->isGroupBookingView()) {
            $this->renderFilterBox();
        }
        
        return $this->renderDataTable();
    }
    
    private function renderFilterBox() {
        $this->includefile("filterbox");
    }

    public function PmsManager_getSimpleRooms() {
        $this->setDefaults();
        $this->includefile('bookingoverview');
    }
    
    public function PmsManager_getSimpleRoomsForGroup() {
        $this->includefile('bookingoverview');
    }
    
    private function renderDataTable() {   
        $this->setData();
        $filter = $this->getSelectedFilter();
        $domainName = $this->getSelectedMultilevelDomainName();
        $args = array($domainName, $filter);
        
        $attributes = array(
            array('id', 'gs_hidden', 'bookingEngineId'),
            array('roomId', 'gs_hidden', 'roomId', 'formatRoomId'),
            array('reg', 'REG', 'regDate', 'formatRegDate'),
            array('periode', 'PERIODE', null, 'formatStartPeriode'),
            array('visitor', 'VISTOR', null, 'formatVistior'),
            array('room', 'ROOM', null, 'formatRoom'),
            array('price', 'PRICE', null, 'formatPrice'),
            array('state', 'STATE', null, 'formatState')
        );
        
        $functionToUse = "getSimpleRooms";
        
        if ($this->isGroupBookingView()) {
            $functionToUse = "getSimpleRoomsForGroup";
            $args = array($domainName, $this->getCurrentGroupBookingEngineId());
        }
        
        $table = new \GetShopModuleTable($this, 'PmsManager', $functionToUse, $args, $attributes);
        $table->render();
    }

    public function getSelectedFilter() {
        
        if (isset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()])) {
            return unserialize($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]);
        }
        
        $config = $this->getConfig();
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->state = 0;
        $filter->startDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 00:00", time()))-(86400*$config->defaultNumberOfDaysBack));
        $filter->endDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 00:00", time()))+86300);
        $filter->sorting = "regdate";
        if($config->bookingProfile == "conferense") {
            $filter->groupByBooking = true;
        }
        if(isset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]) && $_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]) {
            $filter->includeDeleted = true;
        }
        
        return $filter;
    }

    public function getConfig() {
        if(isset($this->config) && $this->config) {
            return $this->config;
        }
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $this->config = $config;
        return $this->config;
    }
    
    public function getAllProducts() {
        if(!isset($this->allProducts) || !$this->allProducts) {
            $this->allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        }
        
        return $this->allProducts;
    }
    
    private function getPmsRoom() {
        $pmsBooking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        
        foreach ($pmsBooking->rooms as $room) {
            if ($room->bookingId == $_POST['data']['id']) {
                return $room;
            }
        }
        return null;
    }
    public function updateBooking() {
        $this->setData();
        $selectedRoom = $this->getPmsRoom();
        $this->updateGuests($selectedRoom);
        $this->setStay($selectedRoom);
        $this->updateUnitPrices($selectedRoom);
        $this->setData(true);
        $this->includefile("bookingoverview");
        die();
    }
    
    public function loaditemview() {
        $av = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $av->loaditemview();
    }

    public function hasUnsettledAmount() {
        $booking = $this->getPmsBooking();
        $this->setData();
        
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->avoidOrderCreation = true;
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($booking->endDate));

        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $this->getPmsBooking()->id, $filter);    

        $itemCount = count($this->getApi()->getCartManager()->getCart()->items);
        
        echo $itemCount > 0 ? "YES" : "NO";
        die();
    }
    
    public function setStay($selectedRoom) {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $typeId = explode("_", $_POST['data']['itemid']);
        $itemId = "";
        
        if (count($typeId) == 2) {
            $itemId = $typeId[1];
            $typeId = $typeId[0];
        } else {
            $typeId = $typeId[0];
        }
        
        $this->getApi()->getPmsManager()->setBookingItemAndDate(
                $this->getSelectedMultilevelDomainName(), 
                $selectedRoom->pmsBookingRoomId,
                $itemId, 
                false, 
                $start, 
                $end);
    }

    public function getCurrentGroupBookingEngineId() {
        if (isset($_GET['bookingEngineId'])) {
            $_SESSION['PmsSearchBooking_groupbookingengineid'] = $_GET['bookingEngineId'];
        }
        
        return $_SESSION['PmsSearchBooking_groupbookingengineid'];
    }

    public function isGroupBookingView() {
        return $this->getPage()->getId() == "groupbooking";
    }

    public function getChannelMatrix() {
        $channels = $this->getApi()->getPmsManager()->getChannelMatrix($this->getSelectedMultilevelDomainName());
        
        if($this->channels) {
            return $this->channels;
        }
        
        $this->channels = $channels;
        return $channels;
    }

    public function showCheckins() {
        $this->clearFilter();
        $filter = $this->getSelectedFilter();
        $filter->filterType = "checkin";
        $filter->startDate = $this->convertToJavaDate(strtotime($_POST['data']['day']." days", time()));
        $filter->endDate = $this->convertToJavaDate(time());
        $this->setCurrentFilter($filter);
    }
    
    public function showCheckouts() {
        $this->clearFilter();
        $filter = $this->getSelectedFilter();
        $filter->filterType = "checkout";
        $filter->startDate = $this->convertToJavaDate(strtotime($_POST['data']['day']." days", time()));
        $filter->endDate = $this->convertToJavaDate(time());
        $this->setCurrentFilter($filter);
    }
    
    public function setCurrentFilter($filter) {
        $_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()] = serialize($filter);
    }
    
    public function searchBooking() {
        $this->clearFilter();
        $filter = $this->getSelectedFilter();
        $filter->searchWord = $_POST['data']['searchtext'];
        $this->setCurrentFilter($filter);
    }

    public function clearFilter() {
        unset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]);
    }
    
    public function startPaymentProcess() {
        if (isset($_SESSION['payment_process_cart_'.$this->getSelectedRoom()->pmsBookingRoomId])) {
            $this->includefile("paymentprocess_started_for_room");
            return false;
        }
        
        $bookingId = $this->getPmsBooking()->id;
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $bookingId);

        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->avoidOrderCreation = true;
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($booking->endDate));

        $this->getApi()->getCartManager()->clear();
        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $bookingId, $filter);    

        $itemCount = count($this->getApi()->getCartManager()->getCart()->items);
        if (!$itemCount) {
            return false;
        }
        
        echo "<div class='SalesPointCartCheckout'>";
        $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salesPointCartCheckout->setOriginalCart();
        $salesPointCartCheckout->render();
        echo "</div>";
        
        
        return true;
    }
    
    public function renderPaymentProcess() {
        $this->includefile("paymentprocess");
    }

    public function updateCartAndPrice() {
        $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salesPointCartCheckout->updateCartAndPrice();
    }
    
    public function subMenuChanged() {
        $_SESSION['currentSubMenu'] = $_POST['data']['selectedTab'];
        $this->renderTabContent();
        die();
    }

    public function isTabActive($tabname) {
        if (isset($_SESSION['currentSubMenu']) && $_SESSION['currentSubMenu'] == $tabname) {
            echo "active";
        }
        
        echo "";
    }

    public function setData($reload = false) {
        if (isset($_POST['data']['roomid'])) {
            $_POST['data']['roomId'] = $_POST['data']['roomid'];
        }
        if ((isset($_POST['data']['id']) && !$this->bookingEngineBooking) || $reload) {
            $this->bookingEngineBooking = $this->getApi()->getBookingEngine()->getBooking($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        }
        
        if ((isset($_POST['data']['id']) && !$this->pmsBooking) || $reload) {
            $this->pmsBooking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        }
        
        if ((isset($_POST['data']['roomId']) && !$this->selectedRoom) || $reload) {
            foreach ($this->pmsBooking->rooms as $room) {
                if ($room->pmsBookingRoomId == $_POST['data']['roomId']) {
                    $this->selectedRoom = $room;
                }
            }
        }
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getPmsBooking() {
        $this->setData();
        return $this->pmsBooking;
    }
    
    /**
     * 
     * @return \core_bookingengine_data_Booking
     */
    public function getBookingEngineBooking() {
        $this->setData();
        return $this->bookingEngineBooking;
    }

    public function setDefaults() {
        if (!isset($_SESSION['currentSubMenu'])) {
            $_SESSION['currentSubMenu'] = "bookinginformation";
        }
    }

    
    /**
     * @return \core_usermanager_data_User 
     */
    public function getUserForBooking() {
        if (!isset($this->currentUserForBooking)) {
            $this->currentUserForBooking = $this->getApi()->getUserManager()->getUserById($this->pmsBooking->userId);
        }

        return $this->currentUserForBooking;
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getSelectedRoom() {
        $this->setData();
        return $this->selectedRoom;
    }

    public function hasAccountingTransfer() {
        return false;
    }

    public function saveUser() {
        $this->setData();
         
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->fullName = $_POST['data']['fullName'];
        $user->prefix = $_POST['data']['prefix'];
        $user->emailAddress = $_POST['data']['emailAddress'];
        $user->cellPhone = $_POST['data']['cellPhone'];
        if(!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        $user->address->address = $_POST['data']['address.address'];
        $user->address->postCode = $_POST['data']['address.postCode'];
        $user->address->city = $_POST['data']['address.city'];
        $user->address->countrycode = $_POST['data']['countryCode'];
        $user->birthDay = $_POST['data']['birthDay'];
        $user->relationship = $_POST['data']['relationship'];
        $user->preferredPaymentType = $_POST['data']['preferredpaymenttype'];
        $this->getApi()->getUserManager()->saveUser($user);
        
        $booking = $this->getPmsBooking();
        $booking->countryCode = $user->address->countrycode;
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        
        $this->setData(true);
        $this->includefile("edituser");
        die();
    }
    
       
    public function createNewUser(){
        $name = $_POST['data']['name'];
        $bookingId = $this->getPmsBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, "");
        
        $this->setData(true);
        $this->includefile("edituser");
        die();
    }
    
    public function searchForUsers() {
        $this->includefile("searchusers");
        die();
    }
    
    public function changeUser() {
        $this->setData();
        $booking = $this->getPmsBooking();
        $booking->userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
        
        $this->setData(true);
        $this->includefile("edituser");
        die();
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $bookingId = $this->getPmsBooking()->id;
        
        $this->getApi()->getPmsManager()->createNewUserOnBooking($this->getSelectedMultilevelDomainName(),$bookingId, $name, $vat);
        
        $this->setData(true);
        $this->includefile("edituser");
        die();
    }

    private function updateGuests($selectedRoom) {
        $guests = array();
        
        foreach ($_POST['data'] as $key => $value) {
            $rowDel = explode("_", $key);
            if (count($rowDel) === 3 && $rowDel[0] === "guestinfo") {
                $guests[$rowDel[1]][$rowDel[2]] = $value;
            }
        }
        
        $guestsToUse = array();
        foreach ($guests as $count => $guest) {
            $guestsToUse[] = $guest;
        }
        
        $pmsBooking = $this->getPmsBooking();
        $this->getApi()->getPmsManager()->setGuestOnRoom($this->getSelectedMultilevelDomainName(), $guestsToUse, $pmsBooking->id, $selectedRoom->pmsBookingRoomId);
        
    }

    public function renderTabContent() {
        if ($_SESSION['currentSubMenu'] == "log") {
            $this->includefile("log");
        }
        if ($_SESSION['currentSubMenu'] == "orders") {
            $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
            $this->includefile("orderstab");
        } 
    }

    public function addCurrentCartForPaymentProcess() {
        $this->setData();
        $roomId = $this->getSelectedRoom()->pmsBookingRoomId;
        $salespoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salespoint->addCurrentCartForPaymentProcess($roomId);
        
        $this->includefile("orderstab");
        die();
    }
    
    public function cancelPaymentProcess() {
        $this->setData();
        $roomId = $this->getSelectedRoom()->pmsBookingRoomId;
        $salespoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salespoint->cancelPaymentProcessForRoom($roomId);
        
        $this->includefile("orderstab");
        die();
    }
    
    public function cancelAllPaymentProcesses() {
        $salespoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salespoint->cancelAllPaymentProcesses();
        die();
    }

    public function getCartsForPaymentProcess() {
        $carts = array();
        foreach ($_SESSION as $key => $value) {
            if (strstr($key, "payment_process_cart_") > -1) {
                try {
                    $cart = json_decode($_SESSION[$key]);
                } catch (Exception $ex) {
                    continue;
                }
                @$keyArr = explode("_", $key);
                @$cart->roomId = $keyArr[3];
                $carts[] = $cart;
            }
        }
        return $carts;
    }

    public function doPaymentNormal() {
        $this->setData();
        
        $salesPoint = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $cart = $salesPoint->getTmpCartForRoom($_POST['data']['roomid']);
        
        $app = $this->getApi()->getStoreApplicationPool()->getApplication($_POST['data']['paymentid']);
        $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
        $order = $instance->doPayment($cart);
        $order->createByManager = "SalesPoint";
        $this->getApi()->getOrderManager()->saveOrder($order);
        
        $this->sendPaymentLinkIfNeeded($order);
        $this->setData(true);
    }

    public function sendPaymentLinkIfNeeded($order) {
        if (isset($_POST['data']['sendPaymentLink']) && $_POST['data']['sendPaymentLink'] !== "true") {
            return;
        }        
        
        $pmsBooking = $this->getPmsBooking();
        $email = $_POST['data']['email'];
        $prefix = $_POST['data']['prefix'];
        $phone = $_POST['data']['cellphone'];
        $smsText = $_POST['data']['textMessage'];
        
        $this->getApi()->getPmsManager()->sendPaymentLinkWithText($this->getSelectedMultilevelDomainName(), $order->id, $pmsBooking->id, $email, $prefix, $phone, $smsText);
    }

    public function updateUnitPrices($selectedRoom) {
        $priceMatrix = array();
        
        foreach ($_POST['data'] as $key => $value) {
            if (strpos($key, "room_price_") > -1) {
                $arr = explode("_", $key);
                $date = $arr[2];
                $priceMatrix[$date] = $value;
            }
        }
        
        $this->setData(true);
        $pmsBooking = $this->getPmsBooking();
        foreach ($pmsBooking->rooms as $room) {
            if ($room->pmsBookingRoomId == $this->getSelectedRoom()->pmsBookingRoomId) {
                $room->priceMatrix = $priceMatrix;
            }
        }
        
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $pmsBooking);
    }

}
?>

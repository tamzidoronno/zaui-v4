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
    
    
    public function getCurrentGroupBookingEngineId() {
        if (isset($_GET['bookingEngineId'])) {
            $_SESSION['PmsSearchBooking_groupbookingengineid'] = $_GET['bookingEngineId'];
        }
        
        return $_SESSION['PmsSearchBooking_groupbookingengineid'];
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
    
    public function formatEndPeriode($room) {
        return $this->formatter->formatEndPeriode($room);
    }
    
    public function formatExpandButton($room) {
        return $this->formatter->formatExpandButton($room);
    }
    
    public function formatBookedFor($room) {
        return $this->formatter->formatBookedFor($room);
    }
    
    public function formatAddons($room) {
        return $this->formatter->formatAddons($room);
    }
    
    public function formatTotalPrice($room) {
        return $this->formatter->formatTotalPrice($room);
    }
    
    public function render() {
        $this->setDefaults();
        
        if (!$this->isGroupBookingView()) {
//            $this->renderFilterBox();
        }
        
        return $this->renderDataTable();
    }
    
    public function isGroupBookingView() {
        return $this->getPage()->getId() == "groupbooking";
    }

    
    private function renderFilterBox() {
        $this->includefile("filterbox");
    }

    public function PmsManager_getSimpleRooms() {
        $roomView = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $roomView->setRoomId($_POST['data']['id']);
        $roomView->renderApplication(true, $this);
        
    }
    
    public function PmsManager_getSimpleRoomsForGroup() {
        $this->includefile('bookingoverview');
    }
    
    private function renderDataTable() {   
//        $this->setData();
        $filter = $this->getSelectedFilter();
        $domainName = $this->getSelectedMultilevelDomainName();
        $args = array($domainName, $filter);
        
        $attributes = array(
            array('id', 'gs_hidden', 'pmsRoomId'),
            array('state', 'STATE', null, 'formatState'),            
            array('roomId', 'gs_hidden', 'roomId', 'formatRoomId'),
            array('reg', 'REG', 'regDate', 'formatRegDate'),
            array('checkin', 'CHECKIN', null, 'formatStartPeriode'),
            array('checkout', 'CHECKOUT', null, 'formatEndPeriode'),
            array('visitor', 'VISITORS', null, 'formatVistior'),
            array('addons', 'ADDONS', null, 'formatAddons'),
            array('bookedfor', 'BOOKED FOR', null, 'formatBookedFor'),
            array('room', 'ROOM', null, 'formatRoom'),
            array('price', 'PRICE', null, 'formatPrice'),
            array('totalprice', 'TOTAL', null, 'formatTotalPrice'),
            array('expandbutton', '', null, 'formatExpandButton')
        );
        
        $functionToUse = "getSimpleRooms";
        
        if ($this->isGroupBookingView()) {
            $functionToUse = "getSimpleRoomsForGroup";
            $args = array($domainName, $this->getCurrentGroupBookingEngineId());
        }
        
        $table = new \GetShopModuleTable($this, 'PmsManager', $functionToUse, $args, $attributes);
        $table->render();
    }
    
    public function getAllProducts() {
        if(!isset($this->allProducts) || !$this->allProducts) {
            $this->allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        }
        
        return $this->allProducts;
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
    
    
    public function setDefaults() {
        if (!isset($_SESSION['currentSubMenu'])) {
            $_SESSION['currentSubMenu'] = "bookinginformation";
        }
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
    
    public function searchBooking() {
        $this->clearFilter();
        $filter = $this->getSelectedFilter();
        $filter->searchWord = $_POST['data']['searchtext'];
        $this->setCurrentFilter($filter);
    }

    public function clearFilter() {
        unset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]);
    }
    
     
    public function setCurrentFilter($filter) {
        $_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()] = serialize($filter);
    }
    
    
}
?>

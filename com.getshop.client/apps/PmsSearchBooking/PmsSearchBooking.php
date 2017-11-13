<?php
namespace ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73;

class PmsSearchBooking extends \MarketingApplication implements \Application {
    private $channels;
    
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
        $this->includefile("overlaybox");
        
        if (!$this->isGroupBookingView()) {
            $this->renderFilterBox();
        }
        
        return $this->renderDataTable();
    }
    
    private function renderFilterBox() {
        $this->includefile("filterbox");
    }

    public function PmsManager_getSimpleRooms() {
        $this->includefile('bookingoverview');
    }
    
    public function PmsManager_getSimpleRoomsForGroup() {
        $this->includefile('bookingoverview');
    }
    
    private function renderDataTable() {    
        $filter = $this->getSelectedFilter();
        $domainName = $this->getSelectedMultilevelDomainName();
        $args = array($domainName, $filter);
        
        $attributes = array(
            array('id', 'gs_hidden', 'bookingEngineId'),
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
    public function updateGuestInformation() {
        $selectedRoom = $this->getPmsRoom();
        $this->setStay($selectedRoom);
        $this->includefile("bookingoverview");
        die();
    }
    
    public function loaditemview() {
        $av = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $av->loaditemview();
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
        $bookingId = $_POST['data']['pmsbookingid'];
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $bookingId);
                
        $filter = new \core_pmsmanager_NewOrderFilter();
        $filter->avoidOrderCreation = true;
        $filter->endInvoiceAt = $this->convertToJavaDate(strtotime($booking->endDate));
                
        
        $this->getApi()->getCartManager()->clear();
        $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $bookingId, $filter);
        
        $result = new \stdClass();
        $result->pmsbookingid = $bookingId;
        
        ob_start();
        echo "<div class='SalesPointCartCheckout'>";
        $salesPointCartCheckout = new \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout();
        $salesPointCartCheckout->renderPreview();
        $html = ob_get_contents();
        echo "</div>";
        ob_end_clean();
        
        $result->previewhtml = $html;
        
        echo json_encode($result);
        
        \ns_90d14853_2dd5_4f89_96c1_1fa15a39babd\SalesPointCartCheckout::clearCheckout();
        die();
    }

    public function subMenuChanged() {
        $_SESSION['currentSubMenu'] = $_POST['data']['selectedTab'];
    }

    public function isTabActive($tabname) {
        if (isset($_SESSION['currentSubMenu']) && $_SESSION['currentSubMenu'] == $tabname) {
            echo "active";
        }
        
        echo "";
    }

}
?>

<?php
namespace ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73;

class PmsSearchBooking extends \MarketingApplication implements \Application {
    private $channels;
    
    /* @var \core_pmsmanager_PmsBooking */
    public $pmsBooking = null;
    
    /* @var \core_bookingengine_data_Booking */
    public $bookingEngineBooking = null;
    
    public $selectedRoom = null;
    public $avoidGroupedBookingOptions = false;

    function __construct() {
        $this->formatter = new PmsSearchBookingColumnFormatters($this);
    }

    public function getDescription() {   
    }
    
    public function tryAddToBookingEngine() {
       $this->getApi()->getPmsManager()->tryAddToEngine($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
    }

    public function loadUnpaidView() {
        $app = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $app->loadUnpaidView();
    }
    
    public function toggleQuickAddAddons() {
        $_SESSION['quickaddaddons'] = true;
    }
    
    public function getName() {
        return "PmsSearchBooking";
    }
    
    public function quickAction() {
        $app = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $app->quickAction();
    }
    
    public function loadQuickMenu() {
        $type = $_POST['data']['type'];
        $app = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $app->loadQuickMenu($type);
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
        return $this->formatter->formatVistior($room, $this->hasSamleFaktura());
    }
    
    public function formatGuests($room) {
        return $this->formatter->formatGuests($room);
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
        
        if($this->getApi()->getPmsManager()->hasNoBookings($this->getSelectedMultilevelDomainName())) {
            $this->includefile("nobookingsyet");
            return;
        }
//        
        return $this->renderDataTable();
    }
    
    public function isGroupBookingView() {
        if (isset($this->page)) {
            return ($this->page->getId() == "groupbooking") && !$this->avoidGroupedBookingOptions;
        }
        return false;
    }

    
    private function renderFilterBox() {
        $this->includefile("filterbox");
    }

    public function PmsManager_getSimpleRooms() {
        
        $roomView = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $roomView->removeGroupView();
        $roomView->setRoomId($_POST['data']['id']);
        $roomView->renderApplication(true, $this);
        
    }
    
    public function PmsManager_getSimpleRoomsForGroup() {
        $roomView = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $roomView->setRoomId($_POST['data']['id']);
        $roomView->renderApplication(true, $this);
    }
    
    public function startPaymentProcessForSelectedRooms() {
        
        $foundBookings = array();
        foreach($_POST['data']['rooms'] as $room) {
            if(in_array($room, array_keys($foundBookings))) {
                continue;
            }
            $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $room);
            foreach($booking->rooms as $r) {
                $foundBookings[$r->pmsBookingRoomId] = $booking->id;
            }
        }
        
        $bookingIds = array_unique($foundBookings);
        $this->getApi()->getCartManager()->clear();
        $bookings = array_values($bookingIds);
        foreach($bookings as $bookingId) {
            $pmsRooms = array();
            foreach($_POST['data']['rooms'] as $room) {
                if($foundBookings[$room] != $bookingId) {
                    continue;
                }
                $pmsRooms[] = $room;
            }
            
            $filter = new \core_pmsmanager_NewOrderFilter();
            $filter->avoidClearingCart = true;
            $filter->pmsRoomIds = $pmsRooms;
            $filter->avoidOrderCreation = true;
            $this->getApi()->getPmsInvoiceManager()->createOrder($this->getSelectedMultilevelDomainName(), $bookingId, $filter);
        }
        $app = new \ns_2e51d163_8ed2_4c9a_a420_02c47b1f7d67\PmsCheckout();
        $app->renderApplication(true);
    }
    
    public function formatCheck($row) {
        return "<input type='checkbox' class='dontExpand groupedactioncheckbox' roomid='".$row->pmsRoomId."'></input>";
    }
    
    private function renderDataTable() {   
        $filter = $this->getSelectedFilter();
        $domainName = $this->getSelectedMultilevelDomainName();
        $args = array($domainName, $filter);
//        
//        
        $functionToUse = "getSimpleRooms";
        $data = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedMultilevelDomainName(), $filter);
        
        if ($this->isGroupBookingView()) {
            
            $attributes = array(
                array('id', 'gs_hidden', 'pmsRoomId'),
                array('check', '<input type="checkbox" class="toggleallrooms">', null, 'formatCheck'),            
                array('state', 'STATE', null, 'formatState'),            
                array('roomId', 'gs_hidden', 'roomId', 'formatRoomId'),
                array('reg', 'REG', 'regDate', 'formatRegDate'),
                array('checkin', 'CHECKIN', null, 'formatStartPeriode'),
                array('checkout', 'CHECKOUT', null, 'formatEndPeriode'),
                array('guest', '<span title="Guest count">GC</span>', null, 'formatGuests'),
                array('visitor', 'GUEST NAME', null, 'formatVistior'),
                array('addons', 'ADDONS', null, 'formatAddons'),
                array('room', 'ROOM', null, 'formatRoom'),
                array('price', 'PRICE', null, 'formatPrice'),
                array('totalprice', 'TOTAL', null, 'formatTotalPrice')

            );
        } else {
            $attributes = array(
                array('id', 'gs_hidden', 'pmsRoomId'),
                array('state', 'STATE', null, 'formatState'),            
                array('roomId', 'gs_hidden', 'roomId', 'formatRoomId'),
                array('reg', 'REG', 'regDate', 'formatRegDate'),
                array('checkin', 'CHECKIN', null, 'formatStartPeriode'),
                array('checkout', 'CHECKOUT', null, 'formatEndPeriode'),
                array('guest', '<span title="Guest count">GC</span>', null, 'formatGuests'),
                array('visitor', 'VISITORS', null, 'formatVistior'),
                array('addons', 'ADDONS', null, 'formatAddons'),
                array('bookedfor', 'BOOKED FOR', null, 'formatBookedFor'),
                array('room', 'ROOM', null, 'formatRoom'),
                array('price', 'PRICE', null, 'formatPrice'),
                array('totalprice', 'TOTAL', null, 'formatTotalPrice')
            );
        }
        
        ob_start();

        echo "<div class='GetShopModuleTableHeader'>";
        if($this->hasSamleFaktura()) {
            echo "<span style='float:left;font-size:26px; color:#bbb;'><i class='fa fa-trash-o clearCheckoutProcess' style='cursor:pointer;' title='Clear checkout process'></i> <span class='totaladdedtocheckout'>0</span> room(s) added to checkout <span class='continuetocheckout' style='display:none; color:blue; cursor:pointer;'>continue to payment <i class='fa fa-arrow-right'></i></span></span>";
        }
        echo "<input type='txt' class='gsniceinput1 tablefilterinput' placeholder='Do filter on table below by entering a text here'>";
        echo "<div class='nothinginfilertodisplay'>No result found, please choose a different filter string or clear this filter</div>";
        echo "<div class='pmscheckoutforrooms checkoutview'>";
        echo "<div style='text-align:right;'><i class='fa fa-close' onclick='$(\".checkoutview\").hide();' style='cursor:pointer;'></i></div>";
        echo "<div class='innercheckout'></div></div>";
        echo "</div>";
        
        $table = new \GetShopModuleTable($this, 'PmsManager', $functionToUse, $args, $attributes);
        $table->setSorting(array("reg","checkin","guest","checkout","visitor","bookedfor","room","price","totalprice"));
        $table->loadContentInOverlay = true;
        $table->setData($data);
        if(isset($_SESSION['pmsroomviewlistgrouproom']) && $_SESSION['pmsroomviewlistgrouproom']) {
            $table->appendClassToRow("roomId", $_SESSION['pmsroomviewlistgrouproom'], "activeroom");
        }
        $table->render();
        
        $rowCount = 0;
        $guests = 0;
        $nightscount = 0;
        foreach($data as $row ){
            $rowCount++;
            $guests += $row->numberOfGuests;
            $nightscount += $row->numberOfNights;
        }
        echo "<div style='text-align:center;padding: 10px;'>Row count: $rowCount, Guest count: $guests, nights: $nightscount</div>";
        
        echo "<script>";
        echo "$('.tablefilterinput').val(localStorage.getItem('filterKeyword'));";
        echo "app.PmsSearchBooking.filterRows();";
        echo "app.PmsSearchBooking.printAddedToCheckout();";
        echo "</script>";
        
        $toPrint = ob_get_contents();
        
        
        ob_end_clean();
//        if(sizeof($data) == 0) {
//            $this->includefile("noresultfound");
//        } else {
            echo $toPrint;
//        }

        if(isset($_SESSION['PmsSearchBooking_loadBooking'])) {
            $btmpid = $_SESSION['PmsSearchBooking_loadBooking'];
            $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedMultilevelDomainName(), $btmpid);
            if(sizeof($booking->rooms) > 1) {
                $_SESSION['forceroomlistview'] = true;
            }
            echo "<script>";
            echo "$('.datarow[rownumber=\"1\"] .datarow_inner').click();";
            echo "</script>";
            
            unset($_SESSION['PmsSearchBooking_loadBooking']);
        }

    }
    
    public function getAllProducts() {
        if(!isset($this->allProducts) || !$this->allProducts) {
            $this->allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        }
        
        return $this->allProducts;
    }
        
    public function getSelectedFilter() {
        if($_SERVER['PHP_SELF'] == "/json.php" || isset($_SESSION['firstloadpage'])) {
            unset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]);
            ?>
            <script>
                localStorage.setItem('advancesearchtoggled', "false");
                $('.PmsSearchBox .simplesearch').show();
                $('.PmsSearchBox .advancesearch').hide();
                $('input[gsname="searchtext"]').focus();
            </script>
            <?php
        }
        if (isset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()])) {
            return unserialize($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]);
        }
        
        $config = $this->getConfig();
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->state = 0;
        $filter->startDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 00:00", time())));
        $filter->endDate = $this->formatTimeToJavaDate(strtotime(date("d.m.Y 23:59", time())));
        $filter->filterType = "checkin";
        if($config->bookingProfile == "conferense") {
            $filter->groupByBooking = true;
        }
        if(isset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]) && $_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]) {
            $filter->includeDeleted = true;
        }
        
        if(isset($_SESSION['PmsSearchBooking_loadBooking']) && $_SESSION['PmsSearchBooking_loadBooking']) {
            $filter->bookingId = $_SESSION['PmsSearchBooking_loadBooking'];
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
        $filter->includeDeleted = true;
        $this->setCurrentFilter($filter);
    }

    public function clearFilter() {
        unset($_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()]);
    }
    
     
    public function setCurrentFilter($filter) {
        $this->clearFilter();
        $_SESSION['pmfilter'][$this->getSelectedMultilevelDomainName()] = serialize($filter);
    }

    public function advanceSearchBooking() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $app->setStartDate($_POST['data']['from']);
        $app->setEndDate($_POST['data']['to']);
 
        $this->clearFilter();
        $filter = $this->getSelectedFilter();
        $filter->filterType = $_POST['data']['filtertype'];
        $filter->startDate = $this->convertToJavaDate(strtotime($_POST['data']['from']));
        $filter->endDate = $this->convertToJavaDate(strtotime($_POST['data']['to']));
        $filter->channel = $_POST['data']['channel'];
        if(isset($_POST['data']['addons'])) {
            $filter->addons = $_POST['data']['addons'];
        }
        if(isset($_POST['data']['customers'])) {
            $filter->customers = $_POST['data']['customers'];
        }
        if(isset($_POST['data']['codes'])) {
            $filter->codes = $_POST['data']['codes'];
        }
        
        if($_POST['data']['groupbooking'] == "true") {
            $filter->groupByBooking = true;
        }
        if($_POST['data']['includedeleted'] == "true") {
            $filter->includeDeleted = true;
        }
        
        $types = array();
        foreach($_POST['data'] as $key => $val) {
            if($val == "true" && stristr($key, "typeselection_")) {
                $types[] = str_replace("typeselection_", "", $key);
            }
        }
        $filter->typeFilter = $types;

        $this->setCurrentFilter($filter);
    }

    public function quickfilterselection() {
        $this->clearFilter();
        $filter = $this->getSelectedFilter();
        $filter->filterType = $_POST['data']['type'];
        switch($_POST['data']['date']) {
            case "today":
                $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time())));
                $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time())));
                break;
            case "tomorrow":
                $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()+86400)));
                $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()+86400)));
                break;
            case "aftertomorrow":
                $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()+2*86400)));
                $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()+2*86400)));
                break;
            case "yesterday":
                $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()-(86400))));
                $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()-(86400))));
                break;
            case "beforeyesterday":
                $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()-(2*86400))));
                $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()-(2*86400))));
                break;
            case "pastthreedays":
                $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()-(3*86400))));
                $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time())));
                break;
            case "nextthreedays":
                $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time())));
                $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()+(86400*3))));
                break;
            default:
                echo "date not found" . $_POST['data']['date'];
                
        }
        $this->setCurrentFilter($filter);
    }

    public function hasSamleFaktura() {
        if(isset($this->hasSamleFakturaCheck)) {
            return $this->hasSamleFakturaCheck;
        }
        $app = $this->getApi()->getStoreApplicationPool()->getApplication("cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba");
        if($app) { 
            $this->hasSamleFakturaCheck = true;
            return true; 
        }
        $this->hasSamleFakturaCheck = false;
        return false;
    }
}
?>

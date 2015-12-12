<?php
namespace ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a;

class PmsManagement extends \WebshopApplication implements \Application {
    private $selectedBooking;
    public $errors = array();
    
    public function getDescription() {
        
    }
    
    public function showBookingInformation() {
        $this->includefile("bookinginformation");
    }

    /**
     * @return core_bookingengine_data_BookingItemType[]
     */
    public function getTypes() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        $types2 = array();
        foreach ($types as $idx => $type) {
            $types2[$type->id] = $type;
        }
        return $types2;
    }
    
    public function setItemType() {
        $error = $this->getManager()->setNewRoomType($this->getSelectedName(), $_POST['data']['roomid'], $_POST['data']['bookingid'], $_POST['data']['itemtype']);
        if($error) {
            $this->errors[] = $error;
        }
        $this->showBookingInformation();
    }
    
    public function setNewDates() {
        $error = $this->getManager()->changeDates($this->getSelectedName(),
                $_POST['data']['roomid'], 
                $_POST['data']['bookingid'], 
                $this->convertToJavaDate(strtotime($_POST['data']['start'])),
                $this->convertToJavaDate(strtotime($_POST['data']['end'])));
        if($error) {
            $this->errors[] = $error;
        }
        $this->showBookingInformation();
    }
    
    /**
     * @return \core_pmsmanager_PmsBooking
     */
    public function getSelectedBooking() {
        if($this->selectedBooking) {
            return $this->selectedBooking;
        }
        $bookingid = $_POST['data']['bookingid'];
        $booking = $this->getApi()->getPmsManager()->getBooking($this->getSelectedName(), $bookingid);
        $this->selectedBooking = $booking;
        return $booking;
    }
    
    public function getName() {
        return "PmsManagement";
    }

    public function render() {
        if($this->isEditorMode()) {
            if(!$this->getSelectedName()) {
                echo "No booking engine name set yet. please set it first";
                return;
            }
            $this->includefile("managementview");
        }
    }
    
    public function setFilter() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $filter->endDate = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $filter->filterType = $_POST['data']['filterType'];
        $filter->state = 0;
        $filter->searchWord = $_POST['data']['searchWord'];
        
        $_SESSION['pmfilter'][$this->getSelectedName()] = serialize($filter);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function getSelectedFilter() {
        if(isset($_SESSION['pmfilter'][$this->getSelectedName()])) {
            return unserialize($_SESSION['pmfilter'][$this->getSelectedName()]);
        }
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->state = 0;
        $filter->startDate = $this->formatTimeToJavaDate(time()-(86400*3));
        $filter->endDate = $this->formatTimeToJavaDate(time()+(86400*3));
        return $filter;
    }

    public function getManager() {
        return $this->getApi()->getPmsManager();
    }

}
?>

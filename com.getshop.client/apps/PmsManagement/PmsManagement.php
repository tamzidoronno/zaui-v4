<?php
namespace ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a;

class PmsManagement extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function showBookingInformation() {
        $this->includefile("bookinginformation");
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

}
?>

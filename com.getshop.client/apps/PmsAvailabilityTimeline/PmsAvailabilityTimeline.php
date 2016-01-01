<?php
namespace ns_176ea989_c7bb_4cef_a4bd_0c8421567e0b;

class PmsAvailabilityTimeline extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAvailabilityTimeline";
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }

    public function saveSettings() {
        foreach ($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function render() {
        if (!$this->getSelectedName()) {
            echo "You need to specify a booking engine first.";
            return;
        }
        $this->includefile("timelineview");
    }
    
    public function getData() {
        if(isset($_SESSION['pmsavailabilitytimelinedata'])) {
            return unserialize($_SESSION['pmsavailabilitytimelinedata']);
        }
        return array();
    }
    
    public function getStart() {
        $data = $this->getData();
        if(isset($data['start'])) {
            return $data['start'];
        }
        return time();
    }

    public function getEnd() {
        $data = $this->getData();
        if(isset($data['end'])) {
            return $data['end'];
        }
        return time()+86400;
    }
    
    public function getInterval() {
        $data = $this->getData();
        if(isset($data['interval'])) {
            return $data['interval'];
        }
        return "";
    }

    public function showTimeLine() {
        $data = array();
        $data['start'] = strtotime($_POST['data']['start']);
        $data['end'] = strtotime($_POST['data']['end']);
        $data['interval'] = $_POST['data']['interval'];
        $_SESSION['pmsavailabilitytimelinedata'] = serialize($data);
    }    
}
?>

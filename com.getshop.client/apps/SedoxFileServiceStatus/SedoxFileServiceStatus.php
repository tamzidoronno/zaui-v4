<?php
namespace ns_b9642995_2bb2_474c_b1e8_5c859dad67a1;

class SedoxFileServiceStatus extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxFileServiceStatus";
    }

    public function render() {
        $this->includefile("fileservicestatus");
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }

    public function getTime() {
        return date('H:i');
    }
    
    public function saveStatus() {
        $this->setConfigurationSetting("forced", $_POST['forced']);
        $this->setConfigurationSetting("forcedstatus", $_POST['forcedstatus']);
    }
    
    public function getStatus() {
        $forced = filter_var($this->getConfigurationSetting("forced"), FILTER_VALIDATE_BOOLEAN);
        $colorClass = "red";
        
        if($forced) {
            $forcedStatus = $this->getConfigurationSetting("forcedstatus");
            if($forcedStatus == "off") {
                $colorClass = "red";
            } else if($forcedStatus == "limited") {
                $colorClass = "yellow";
            } else if($forcedStatus == "full") {
                $colorClass = "green";
            }
        } else if(!$forced) {
            $colorClass = $this->getStatusColor(date("l"), date("H"), $openingApp);
        }
        return $colorClass;
    }
    
    public function getStatusColor($day, $hour, $openingApp) {
        $app = $this->getApi()->getStoreApplicationPool()->getApplication("cf3f46d9-0073-4966-977d-8e202dc5abbb");
        
        if (!$app) {
            return "red";
        }
        
        $openingApp = $this->getFactory()->getApplicationPool()->createInstace($app);
        $day = strtolower($day);
        
        if($day != "sunday" && $day != "saturday") {
            $day = "monday_friday";
        }
        
        if($day != "sunday") {
            $fullOpenTime = explode("-", $openingApp->getConfigurationSetting($day . "_fullservice"));
        }
        $limitedOpenTime = explode("-", $openingApp->getConfigurationSetting($day . "_limitedservice"));
        
        if($hour >= $fullOpenTime[0] && $hour < $fullOpenTime[1]  && $day != "sunday") {
            $colorClass = "green";
        } else if($hour >= $limitedOpenTime[0] && $hour < $limitedOpenTime[1]) {
            $colorClass = "yellow";
        }
        
        return $colorClass;
    }
}
?>
